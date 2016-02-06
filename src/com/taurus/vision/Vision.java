package com.taurus.vision;

import java.text.DecimalFormat;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Runnable
{
    enum ImageChoices
    {
        Input,
        Threshold,
        Target,
        INVALID
    };

    @SuppressWarnings("unused")
    private static final class COLORS
    {
        private static final float BLACK   = 0x000000;
        private static final float WHITE   = 0xffffff;
        private static final float RED     = 0xff0000;
        private static final float ORANGE  = 0xff8000;
        private static final float YELLOW  = 0xffff00;
        private static final float LIME    = 0x80ff00;
        private static final float GREEN   = 0x00ff00;
        private static final float TEAL    = 0x00ff80;
        private static final float CYAN    = 0x00ffff;
        private static final float VIOLET  = 0x0080ff;
        private static final float BLUE    = 0x0000ff;
        private static final float PINK    = 0x8000ff;
        private static final float MAGENTA = 0xff00ff;
    }
    
    private int RescaleSize = 1; // large size == smaller image; 2 == 1/2 image size
    
    private final double TIME_RATE_VISION = .05;// .033;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    private SendableChooser imageChooser = new SendableChooser();

    Thread visionThread;
    
    Camera camera;

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
        camera = new Camera("cam0");
        Image frame, frameTH, frameDownsampled, frameTHDownsampled;
        
//        NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
//        NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
//        criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_CONVEX_HULL_AREA, 0, 100.0, 0, 0);
        
        double TimeLastVision = 0;

        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTHDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

        while (true)
        {
            // grab from the chooser which image to use
            ImageChoices imageToSend = ((ImageChoices) imageChooser.getSelected());

            // if we haven't opened the camera session
            if(!camera.isOpen())
            {
                camera.openCamera();
            }

            // if we haven't started capturing
            if (!camera.isCapturing())    
            {
                camera.startCapture();
            }
            
            updateSettings();
            

            // we have an open camera session 
            if (camera.isCapturing())
            {
                // save off the start time so we can see how long 
                // it takes to process a frame
                TimeLastVision = Timer.getFPGATimestamp();

                try
                {
                    // grab a new frame
                    camera.getImage(frame);

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
                            //NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, COLORS.RED);
                            
                            //Rect centerRect = new Rect(top, left, height, width);
                            // draw a circle in the center of the image/where the ball shoots
                            //NIVision.imaqDrawShapeOnImage(frame, frame, centerRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_OVAL, COLORS.RED);
                            
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
            Timer.delay(Math.max(0, TIME_RATE_VISION - (Timer.getFPGATimestamp() - TimeLastVision)));
        }
    }

    /**
     * If any of the Preferences change, send the latest to the camera object
     */
    private synchronized void updateSettings()
    {
        int videoMode = Preferences.getInstance().getInt("VIDEO_MODE", 93);
        if(camera.getVideoMode() != videoMode)
        {
            camera.setVideoMode(videoMode);
        }
        
        int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4500);
        if(camera.getWhiteBalanceManual() != whiteBalance)
        {
            camera.setWhiteBalanceManual(whiteBalance);
        }
        
        int exposure = Preferences.getInstance().getInt("Exposure", 1);
        if(camera.getExposureManual() != exposure)
        {
            camera.setExposureManual(exposure);
        }
        
        double brightness = Preferences.getInstance().getDouble("Brightness", .25);
        if(camera.getBrightness() != brightness)
        {
            camera.setBrightness(brightness);
        }
    }
}
