package com.taurus.vision;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Vector;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Runnable
{
    private final String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
    private final String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
    private final String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
    private final String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
    private final String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
    private final String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
    private final String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";

    enum ImageChoices
    {
        Input,
        Threshold,
        Target,
        INVALID
    };

    private final float COLOR_BLACK   = 0x000000;
    private final float COLOR_WHITE   = 0xffffff;
    private final float COLOR_RED     = 0xff0000;
    private final float COLOR_ORANGE  = 0xff8000;
    private final float COLOR_YELLOW  = 0xffff00;
    private final float COLOR_LIME    = 0x80ff00;
    private final float COLOR_GREEN   = 0x00ff00;
    private final float COLOR_TEAL    = 0x00ff80;
    private final float COLOR_CYAN    = 0x00ffff;
    private final float COLOR_VIOLET  = 0x0080ff;
    private final float COLOR_BLUE    = 0x0000ff;
    private final float COLOR_PINK    = 0x8000ff;
    private final float COLOR_MAGENTA = 0xff00ff;

    private int RescaleSize = 1; // large size == smaller image; 2 == 1/2 image size
    
    private final double TIME_RATE_VISION = .05;// .033;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    private SendableChooser imageChooser = new SendableChooser();

    Thread visionThread;

    private boolean targetDetectionOn = true;
    private Target largestTarget;

    public void setTargetDetectionOn(boolean enable){
        synchronized(visionThread){
            targetDetectionOn = enable;
        }
    }

    public Target getTarget(){
        synchronized (visionThread){
            return largestTarget;
        }
    }

    public Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input", ImageChoices.Input);
        imageChooser.addObject("Threshold", ImageChoices.Threshold);
        imageChooser.addObject("Target", ImageChoices.Target);
        SmartDashboard.putData("Image to show", imageChooser);

        CameraServer.getInstance().setQuality(30);

        visionThread = new Thread(this);
        visionThread.setPriority(Thread.MIN_PRIORITY);
    }

    public void Start()
    {
        visionThread.start();
    }

    @Override
    public void run()
    {
        int session = -1;
        Image frame, frameTH, frameDownsampled, frameTHDownsampled;
        
        NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
        NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_CONVEX_HULL_AREA, 0, 100.0, 0, 0);
        
        ImageChoices prevImageToSend = ImageChoices.INVALID;

        double TimeLastVision = 0;

        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTHDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

        while (true)
        {
            // grab from the chooser which image to use
            ImageChoices imageToSend = ((ImageChoices) imageChooser.getSelected());

            // if we haven't opened the camera session, OR
            // if the dashboard image choose changed (we might have changed cameras)
            if (session == -1 || prevImageToSend != imageToSend)    
            {
                prevImageToSend = imageToSend;

                try
                {
                    if (session != -1)
                    {
                        // a session is running so stop it
                        NIVision.IMAQdxStopAcquisition(session);
                        NIVision.IMAQdxUnconfigureAcquisition(session);
                        NIVision.IMAQdxCloseCamera(session);
                        session = -1;
                    }
                }
                catch (Exception e)
                {
                    SmartDashboard.putString("Vision error 131", e.getMessage());
                }

                // open a new camera session
                session = OpenCamera("cam0");
            }

            // we have an open camera session 
            if (session != -1)
            {
                // save off the start time so we can see how long 
                // it takes to process a frame
                TimeLastVision = Timer.getFPGATimestamp();

                try
                {
                    // grab a new frame
                    NIVision.IMAQdxGrab(session, frame, 1);

                    // print input image size
                    GetImageSizeResult size = NIVision.imaqGetImageSize(frame);
                    SmartDashboard.putString("FrameSize", size.width + "," + size.height);

                    // get the latest rescale size from the prefs
                    RescaleSize = Preferences.getInstance().getInt("ImageRescaleSize", 1);
                    
                    // send image if requested
                    if (imageToSend == ImageChoices.Input)
                    {
                        // scale size
                        NIVision.imaqScale(frameDownsampled, frame, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                        GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
                        SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);
                        
                        // just send the raw image
                        CameraServer.getInstance().setImage(frameDownsampled);
                    }

                    // only run if enabled
                    if (targetDetectionOn)
                    {
                        // target detection is on, so adjust the image to filter
                        // out all the nonsense

                        NIVision.imaqColorThreshold(frameTH, frame, 255,
                                ColorMode.HSL,
                                new Range(Preferences.getInstance().getInt("Hmin", 54),
                                        Preferences.getInstance().getInt("Hmax", 97)),
                                new Range(Preferences.getInstance().getInt("Smin", 78),
                                        Preferences.getInstance().getInt("Smax", 255)),
                                new Range(
                                        Preferences.getInstance().getInt("Lmin", 73),
                                        Preferences.getInstance().getInt("Lmax", 255)));

                        // send threshold image if requested
                        if (imageToSend == ImageChoices.Threshold)
                        {
                            NIVision.imaqScale(frameTHDownsampled, frameTH, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                            GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameTHDownsampled);
                            SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

                            // send the raw black/white image that should have just the target
                            CameraServer.getInstance().setImage(frameTHDownsampled);
                        }

                        // get the number of particles
                        int particleCount = NIVision.imaqCountParticles(frameTH, 1);
                        SmartDashboard.putNumber("Particles", particleCount);

                        // find the bounding rectangle, then send
                        double biggestArea = -1;
                        double biggestX = 0, biggestY = 0;
                        double biggestW = 0, biggestH = 0;
                        double biggestOrientation = 0;

                        // for each particle, calculate values
                        for (int i = 0; i < particleCount; i++)
                        {
                            double area = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_AREA);
                                    
                            if (area > biggestArea)
                            {
                                biggestArea = area;
                                biggestX = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CENTER_OF_MASS_X);
                                biggestY = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CENTER_OF_MASS_Y);
                                biggestH = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_HEIGHT);
                                biggestW = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_WIDTH);
                                biggestOrientation = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_ORIENTATION);
                            }
                        }

                        // print some info to dashboard
                        SmartDashboard.putNumber("Area", biggestArea);
                        SmartDashboard.putString("Center", df.format(biggestX) + "," + df.format(biggestY));
                        SmartDashboard.putNumber("Width", biggestW);
                        SmartDashboard.putNumber("Height", biggestH);
                        SmartDashboard.putNumber("Orientation", biggestOrientation);

                        // store largest target
                        synchronized (visionThread)
                        {
                            largestTarget = new Target(biggestX, biggestY, biggestArea, biggestH, biggestW, biggestOrientation);
                        }

                        if (imageToSend == ImageChoices.Target)
                        {
                            //TODO add Target Drawing here
                            //Rect targetRect = new Rect(top, left, height, width);
                            // draw an outline of a rectangle in RED around the target
                            //NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, COLOR_RED);
                            
                            //Rect centerRect = new Rect(top, left, height, width);
                            // draw a circle in the center of the image/where the ball shoots
                            //NIVision.imaqDrawShapeOnImage(frame, frame, centerRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_OVAL, COLOR_RED);
                            
                            NIVision.imaqScale(frameDownsampled, frame, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                            GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
                            SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

                            CameraServer.getInstance().setImage(frameDownsampled);
                        }
                    }                    

                }
                catch (Exception e)
                {
                    SmartDashboard.putString("Vision error", "Processing:" + e.getMessage());
                }

                SmartDashboard.putNumber("Vision Task Length",
                        Timer.getFPGATimestamp() - TimeLastVision);
            }
            Timer.delay(Math.max(0,
                    TIME_RATE_VISION
                    - (Timer.getFPGATimestamp() - TimeLastVision)));
        }

        // NIVision.IMAQdxStopAcquisition(session);
    }
    
    private int OpenCamera(String CamName)
    {
        int session = -1;
        try
        {
            // the camera name (ex "cam0") can be found through the roborio web
            // interface
            session = NIVision.IMAQdxOpenCamera( CamName, NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        }
        catch (Exception e)
        {
            SmartDashboard.putString("Vision error", "Open:" + e.getMessage());
            return -1;
        }

        try
        {
            // print to dashboard a list of all available video modes
            // NIVision.dxEnumerateVideoModesResult enumerated =
            // NIVision.IMAQdxEnumerateVideoModes(session);
            // String Modes = "";
            // for (NIVision.IMAQdxEnumItem mode : enumerated.videoModeArray)
            // {
            // Modes += mode.Name + " " + mode.Value + ", ";
            // }
            // SmartDashboard.putString("Modes", Modes);

            int videoMode = Preferences.getInstance().getInt("VIDEO_MODE", 93);
            NIVision.IMAQdxSetAttributeU32(session, ATTR_VIDEO_MODE, videoMode);
            
//             int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4500);
//             NIVision.IMAQdxSetAttributeString(session, ATTR_WB_MODE, "Manual");
//             NIVision.IMAQdxSetAttributeI64(session, ATTR_WB_VALUE,
//             whiteBalance,0.0);
//
//             int exposure = Preferences.getInstance().getInt("Exposure", 1);
//             NIVision.IMAQdxSetAttributeString(session, ATTR_EX_MODE, "Manual");
//             NIVision.IMAQdxSetAttributeI64(session, ATTR_EX_VALUE, exposure);
             
            {
                double brightness = Preferences.getInstance().getDouble("Brightness", .25);

                NIVision.IMAQdxSetAttributeString(session, ATTR_BR_MODE, "Manual");
                long minv = NIVision.IMAQdxGetAttributeMinimumI64(session, ATTR_BR_VALUE);
                long maxv = NIVision.IMAQdxGetAttributeMaximumI64(session, ATTR_BR_VALUE);

                long val = (long) Utilities.scaleToRange(brightness, 0, 1, minv, maxv);
                NIVision.IMAQdxSetAttributeI64(session, ATTR_BR_VALUE, val);
            }

            NIVision.IMAQdxConfigureGrab(session);

            NIVision.IMAQdxStartAcquisition(session);

        }
        catch (Exception e)
        {
            SmartDashboard.putString("Vision error 353: ", e.getMessage());
        }
        return session;
    }

}
