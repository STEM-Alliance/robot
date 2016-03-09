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
    enum IMAGE_TYPE
    {
        Input,
        Threshold,
        DriveBack,
        INVALID
    };
    
    enum CAMERAS
    {
        MAIN,
        BACK,
        NONE
    }

    @SuppressWarnings("unused")
    private static final class COLORS
    {
        // order is bgr
        private static final int BLACK   = 0x000000;
        private static final int WHITE   = 0xffffff;
        private static final int RED     = 0x0000ff;
        private static final int ORANGE  = 0x0080ff;
        private static final int YELLOW  = 0x00ffff;
        private static final int LIME    = 0x00ff80;
        private static final int GREEN   = 0x00ff00;
        private static final int TEAL    = 0x80ff00;
        private static final int CYAN    = 0xffff00;
        private static final int VIOLET  = 0xff8000;
        private static final int BLUE    = 0xff0000;
        private static final int PINK    = 0xff0080;
        private static final int MAGENTA = 0xff00ff;
    }
    
    private final double TIME_RATE_VISION = 1.0/30.0; // frame time at 30fps
    private int RescaleSize = 1; // large size == smaller image; 2 == 1/2 image size
    private boolean targetDetectionOn = true;
    private boolean imageRotation = true;    // TODO - DRL make sure main camera is physically oriented upright, then remove this code.
    private CAMERAS cameraRequested = CAMERAS.MAIN;
    private CAMERAS cameraCurrent = CAMERAS.NONE;
    
    private SendableChooser imageChooser = new SendableChooser();
    private DecimalFormat df = new DecimalFormat("#.##");
    private static Vision instance = null;
    private Target largestTarget;

    private Thread visionThread;
    private Thread frameThread;
    private FrameGrabber frameGrabber;

    public static Vision getInstance()
    {
        if (instance == null)
        {
            instance = new Vision();         
        }
        return instance;        
    }
    
    public synchronized void enableBackCamera(boolean enable)
    {
        cameraRequested = (enable) ? CAMERAS.BACK : CAMERAS.MAIN;
    }
    
    public synchronized void setTargetDetectionOn(boolean enable)
    {
        targetDetectionOn = enable;
    }

    public synchronized void setImageRotation(boolean enable)
    {
        imageRotation = enable;
    }

    public Target getTarget()
    {
        return largestTarget;
    }

    private Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input", IMAGE_TYPE.Input);
        imageChooser.addObject("Threshold", IMAGE_TYPE.Threshold);
        SmartDashboard.putData("Image to show", imageChooser);

        visionThread = new Thread(this);
        visionThread.setPriority(Thread.NORM_PRIORITY);
        Start();
    }

    public void Start()
    {
        if(!visionThread.isAlive())
            visionThread.start();
    }

    @Override
    public void run()
    {
        SmartDashboard.putString("Vision", "Running");
        
        double TimeLastVision = 0;

        Image frame;
        final Image frameBack, frameTH, frameDownsampled, frameDownsampledTH;
        
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameBack = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameDownsampledTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

        while (true)
        {
            // grab from the chooser which image to use
            IMAGE_TYPE imageToSend = ((IMAGE_TYPE) imageChooser.getSelected());

            if(cameraRequested != cameraCurrent)
            {
                setupFrameGrabber(cameraRequested);
            }
            
            try
            {
                // save off the start time so we can see how long it takes to process a frame
                TimeLastVision = Timer.getFPGATimestamp();
                
                // grab a new frame
                if (frameThread.isAlive())  // Redundant check
                {
                    frame = frameGrabber.getFrame();
                }
                
                if (cameraCurrent == CAMERAS.MAIN)
                {
                    SmartDashboard.putString("Vision", "Main camera");

                    if(imageRotation)
                    {
                        NIVision.imaqFlip(frame, frame, FlipAxis.HORIZONTAL_AXIS);
                        NIVision.imaqFlip(frame, frame, FlipAxis.VERTICAL_AXIS);
                    }
                    
                    //GetImageSizeResult size = NIVision.imaqGetImageSize(frame);
                    //SmartDashboard.putString("FrameSize", size.width + "," + size.height);

                    if (targetDetectionOn)
                    {
                        // target detection is on, so adjust the image to filter out all the nonsense
                        filterTargets(frame, frameTH);
                        
                        printTargetInfo();
                    }
                }
                
                RescaleSize = Preferences.getInstance().getInt("ImageRescaleSize", 1);
                
                sendSmartDashboardImage(frame, frameTH, frameBack, imageToSend);
                
                SmartDashboard.putString("Vision error", "");                
                SmartDashboard.putNumber("Vision Task Length", Timer.getFPGATimestamp() - TimeLastVision);
            }
            catch (Exception e)
            {
                SmartDashboard.putString("Vision error", "Processing: " + e.getMessage());
            }

            //Timer.delay(Math.max(0, TIME_RATE_VISION - (Timer.getFPGATimestamp() - TimeLastVision)));
        }
    }
    
    private void setupFrameGrabber(CAMERAS newCamera)
    {
        if (cameraCurrent != CAMERAS.NONE)
        {
            frameGrabber.terminate();
            try
            {
                frameThread.join();
            }
            catch (InterruptedException e)
            {
                SmartDashboard.putString("Vision", "Could not join prior frame grabber");
            }
        }
        
        if (newCamera != CAMERAS.NONE)
        {        
            String name = "";
            int brightness = 0;
            
            if (newCamera == CAMERAS.MAIN)
            {
                name = "cam0";
                brightness = 86;
            }
            else if (newCamera == CAMERAS.BACK)
            {
                name = "cam1";
                brightness = 130;
            }
          
            frameGrabber = new FrameGrabber(name, brightness);
            frameThread = new Thread(frameGrabber);
            frameThread.setPriority(Thread.NORM_PRIORITY);
            frameThread.run();
        }
      
        cameraCurrent = newCamera;
    }

    private void filterTargets(Image frame, Image frameTH)
    {
        NIVision.imaqColorThreshold(frameTH, frame, 255,
                ColorMode.HSL,
                new Range(Preferences.getInstance().getInt("Hmin", 98),
                          Preferences.getInstance().getInt("Hmax", 150)),
                new Range(Preferences.getInstance().getInt("Smin", 78),
                          Preferences.getInstance().getInt("Smax", 255)),
                new Range(Preferences.getInstance().getInt("Lmin", 73),
                          Preferences.getInstance().getInt("Lmax", 255)));

        // get the number of particles
        int particleCount = NIVision.imaqCountParticles(frameTH, 1);
        SmartDashboard.putNumber("Particles", particleCount);

        // find the bounding rectangle, then send
        double biggestArea = Preferences.getInstance().getInt("MinArea", 0) - 1;
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

        // store largest target
        if(biggestArea > Preferences.getInstance().getInt("MinArea", 0))
        {
            largestTarget = new Target(biggestX, biggestY, biggestArea, biggestH, biggestW, biggestOrientation);

        }
        else
        {
            largestTarget = null;
        }
    }

    private void printTargetInfo()
    {
        if(largestTarget != null)
        {
            // Write Text of Target Location
            String descXY = (int)largestTarget.X() + ", " + (int)largestTarget.Y();
            String descWH = (int)largestTarget.W() + ", " + (int)largestTarget.H();
            String descPitch = df.format(largestTarget.Pitch()) + "\u00b0";
            String descYaw = df.format(largestTarget.Yaw()) + "\u00b0";
    
            SmartDashboard.putString("TargetXY", descXY);
            SmartDashboard.putString("TargetWH", descWH);
            SmartDashboard.putString("TargetPitch", descPitch);
            SmartDashboard.putString("TargetYaw", descYaw);
        }
        
        SmartDashboard.putString("TargetXY", "");
        SmartDashboard.putString("TargetWH", "");
        SmartDashboard.putString("TargetPitch", "");
        SmartDashboard.putString("TargetYaw", "");
    }
    
    private void processSmartDashboardImage(Image frame, int color)
    {
        //Rect centerRect = new Rect(Constants.BallShotY - Constants.BallShotDiameter/2,  // top
//                                   Constants.BallShotX - Constants.BallShotDiameter/2,  // left
//                                   Constants.BallShotDiameter,                          // width
//                                   Constants.BallShotDiameter);                         // height
        // draw a circle in the center of the image/where the ball shoots
        //NIVision.imaqDrawShapeOnImage(frame, frame, centerRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_OVAL, color);

        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(0,Constants.BallShotY),
                new Point((int) Constants.Width,Constants.BallShotY), color);
        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(Constants.BallShotX,0),
                new Point(Constants.BallShotX,(int) Constants.Height), color);
                                   
        if(largestTarget != null)
        {
            // add Target Drawing
            Rect targetRect = new Rect((int)largestTarget.Top(), (int)largestTarget.Left(), (int)Math.ceil(largestTarget.H()), (int)Math.ceil(largestTarget.W()));
            // draw an outline of a rectangle around the target
            NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, color);            
        }
    }
    
    private void sendSmartDashboardImage(Image frame, Image frameTH, Image frameBack, IMAGE_TYPE imageToSend)
    {        
        if(imageToSend == IMAGE_TYPE.Input && cameraCurrent == CAMERAS.MAIN)
        {
            processSmartDashboardImage(frame, COLORS.MAGENTA);

            // scale if needed
            //NIVision.imaqScale(frameDownsampled, frame, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

            //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
            //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

            CameraServer.getInstance().setImage(frame);
        }
        else if(imageToSend == IMAGE_TYPE.Threshold && cameraCurrent == CAMERAS.MAIN)
        {
            processSmartDashboardImage(frameTH, COLORS.WHITE);
            
            // scale if needed
            //NIVision.imaqScale(frameDownsampledTH, frameTH, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

            //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampledTH);
            //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

            CameraServer.getInstance().setImage(frameTH);
        }
        else if ((imageToSend == IMAGE_TYPE.DriveBack || cameraCurrent == CAMERAS.BACK))
        {
            // scale if needed
            //NIVision.imaqScale(frameDownsampled, frameBack, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

            //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
            //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

            CameraServer.getInstance().setImage(frameBack);
        }
    }
}