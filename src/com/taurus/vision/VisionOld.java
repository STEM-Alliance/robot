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

public class VisionOld implements Runnable {

    private final double TIME_RATE_VISION = .05;// .033;
    private DecimalFormat df = new DecimalFormat("#.##");

    private SendableChooser imageChooser = new SendableChooser();

    private static final String ATTR_VIDEO_MODE =
            "AcquisitionAttributes::VideoMode";
     private static final String ATTR_WB_MODE =
     "CameraAttributes::WhiteBalance::Mode";
     private static final String ATTR_WB_VALUE =
     "CameraAttributes::WhiteBalance::Value";
     private static final String ATTR_EX_MODE =
     "CameraAttributes::Exposure::Mode";
     private static final String ATTR_EX_VALUE =
     "CameraAttributes::Exposure::Value";
    private static final String ATTR_BR_MODE =
            "CameraAttributes::Brightness::Mode";
    private static final String ATTR_BR_VALUE =
            "CameraAttributes::Brightness::Value";

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
    
    public VisionOld()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input", Integer.valueOf(0));
        imageChooser.addObject("Threshold", Integer.valueOf(1));
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
        Image frame, frameTH, frameTHP, frameDownsampled, frameTHDownsampled;
        NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
        NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_CONVEX_HULL_AREA, 0, 100.0, 0, 0);
        int prevImageToSend = -1;

        double TimeLastVision = 0;

        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameTHP = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTHDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

        while (true)
        {
            // grab from the chooser which image to use
            int imageToSend = ((Integer) imageChooser.getSelected()).intValue();

            if (session == -1 || prevImageToSend != imageToSend)    
            {
                prevImageToSend = imageToSend;
                
                try
                {
                    if (session != -1)
                    {
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
                
                session = OpenCamera("cam0");
            }


            if (session != -1)
            {
                TimeLastVision = Timer.getFPGATimestamp();

                try
                {
                    NIVision.IMAQdxGrab(session, frame, 1);
                    
                    GetImageSizeResult size = NIVision.imaqGetImageSize(frame);
                    
                    SmartDashboard.putString("FrameSize", size.width
                                                          + ","
                                                          + size.height);

                    if (imageToSend == 0)
                    {
                        NIVision.imaqScale(frameDownsampled, frame, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
                        
                        GetImageSizeResult sizeDownsampled = 
                                NIVision.imaqGetImageSize(frameDownsampled);
                        SmartDashboard.putString("FrameSizeDownsampled", 
                                sizeDownsampled.width
                                + ","
                                + sizeDownsampled.height);
                        
                        // just send the raw image
                        CameraServer.getInstance().setImage(frameDownsampled);
                    }

                    if (targetDetectionOn)
                    {
                        // tote detection is on, so adjust the image to filter
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

                        if (imageToSend == 1)
                        {
                            NIVision.imaqScale(frameTHDownsampled, frameTH, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
                            
                            GetImageSizeResult sizeDownsampled = 
                                    NIVision.imaqGetImageSize(frameTHDownsampled);
                            SmartDashboard.putString("FrameSizeDownsampled", 
                                    sizeDownsampled.width
                                    + ","
                                    + sizeDownsampled.height);
                            
                            // send the raw black/white image that should have
                            // just yellow totes
                            CameraServer.getInstance().setImage(frameTHDownsampled);
                        }

                        int particleCount = NIVision.imaqCountParticles(frameTH, 1);
                        SmartDashboard.putNumber("Thresh Particles", particleCount);
                        

                        SmartDashboard.putNumber("Particles", particleCount);

                        // find the bounding rectangle, then send
                        double biggestArea = -1;
                        double biggestX = 0, biggestY = 0;
                        double biggestW = 0, biggestH = 0;
                        double biggestOrientation = 0;

                        for (int i = 0; i < particleCount; i++)
                        {
                            double centerx =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_CENTER_OF_MASS_X);
                            double centery =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_CENTER_OF_MASS_Y);
                            double width =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_BOUNDING_RECT_WIDTH);
                            double height =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_BOUNDING_RECT_HEIGHT);
                            double area =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_AREA);
                            double orientation =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_ORIENTATION);

                            if (area > biggestArea)
                            {
                                biggestArea = area;
                                biggestX = centerx;
                                biggestY = centery;
                                biggestH = height;
                                biggestW = width;
                                biggestOrientation = orientation;
                            }
                        }

                        SmartDashboard.putNumber("Area", biggestArea);
                        SmartDashboard.putString("Center", df.format(biggestX)
                                                     + ","
                                                     + df.format(biggestY));
                        SmartDashboard.putNumber("Width", biggestW);
                        SmartDashboard.putNumber("Height", biggestH);
                        SmartDashboard.putNumber("Orientation",
                                biggestOrientation);

                        synchronized (visionThread)
                        {
                            largestTarget = new Target(biggestX, biggestY, biggestArea, biggestH, biggestW,biggestOrientation);
                            
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
            session =
                    NIVision.IMAQdxOpenCamera(
                            CamName,
                            NIVision.IMAQdxCameraControlMode.CameraControlModeController);
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
/*
//             int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4500);
//             NIVision.IMAQdxSetAttributeString(session, ATTR_WB_MODE, "Manual");
//             NIVision.IMAQdxSetAttributeI64(session, ATTR_WB_VALUE,
//             whiteBalance,0.0);
//
//             int exposure = Preferences.getInstance().getInt("Exposure", 1);
//             NIVision.IMAQdxSetAttributeString(session, ATTR_EX_MODE, "Manual");
//             NIVision.IMAQdxSetAttributeI64(session, ATTR_EX_VALUE, exposure);
*/
            {
                double brightness =
                        Preferences.getInstance().getDouble("Brightness", .25);

                NIVision.IMAQdxSetAttributeString(session, ATTR_BR_MODE,
                        "Manual");
                long minv =
                        NIVision.IMAQdxGetAttributeMinimumI64(session,
                                ATTR_BR_VALUE);
                long maxv =
                        NIVision.IMAQdxGetAttributeMaximumI64(session,
                                ATTR_BR_VALUE);

                long val =
                        (long) Utilities.scaleToRange(brightness, 0, 1, minv,
                                maxv);
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
