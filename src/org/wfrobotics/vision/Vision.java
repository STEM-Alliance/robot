//package org.wfrobotics.vision;
//
//import java.text.DecimalFormat;
//
//import com.ni.vision.NIVision;
//import com.ni.vision.NIVision.*;
//
//import edu.wpi.first.wpilibj.Preferences;
//import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//
//public class Vision implements Runnable {
//    enum IMAGE_TYPE {
//        Input, Threshold, DriveBack, INVALID
//    };
//
//    enum CAMERAS {
//        MAIN, BACK, NONE
//    };
//
//    @SuppressWarnings("unused")
//    public static final class COLORS {
//        // order is bgr
//        private static final int BLACK = 0x000000;
//        public static final int WHITE = 0xffffff;
//        private static final int RED = 0x0000ff;
//        private static final int ORANGE = 0x0080ff;
//        private static final int YELLOW = 0x00ffff;
//        private static final int LIME = 0x00ff80;
//        private static final int GREEN = 0x00ff00;
//        private static final int TEAL = 0x80ff00;
//        public static final int CYAN = 0xffff00;
//        public static final int VIOLET = 0xff8000;
//        public static final int BLUE = 0xff0000;
//        private static final int PINK = 0xff0080;
//        private static final int MAGENTA = 0xff00ff;
//    }
//
//    @SuppressWarnings("unused")
//    private final double TIME_RATE_VISION = 1.0 / 30.0; // frame time at 30fps
//
//    @SuppressWarnings("unused")
//    private int RescaleSize = 1; // large size == smaller image; 2 == 1/2 image
//                                 // size
//    private boolean targetDetectionOn = true;
//    private CAMERAS cameraRequested = CAMERAS.MAIN;
//    private CAMERAS cameraCurrent = CAMERAS.NONE;
//
//    private SendableChooser imageChooser = new SendableChooser();
//    private DecimalFormat df = new DecimalFormat("#.##");
//    private static Vision instance = null;
//    private Target largestTarget;
//
//    private Thread visionThread;
//    private Thread frameThread;
//    private FrameGrabber frameGrabber;
//
//    private Thread senderThread;
//    private FrameSender frameSender;
//
//    public static Vision getInstance()
//    {
//        if (instance == null)
//        {
//            instance = new Vision();
//        }
//        return instance;
//    }
//
//    int EnableBackCount = 0;
//
//    public void enableBackCamera(boolean enable)
//    {
//        SmartDashboard.putBoolean("EnableBack", enable);
//        SmartDashboard.putNumber("EnableBackCount", EnableBackCount++);
//        cameraRequested = (enable) ? CAMERAS.BACK : CAMERAS.MAIN;
//    }
//
//    public synchronized void setTargetDetectionOn(boolean enable)
//    {
//        targetDetectionOn = enable;
//    }
//
//    public Target getTarget()
//    {
//        return largestTarget;
//    }
//
//    public CAMERAS getCurrentCamera()
//    {
//        return cameraCurrent;
//    }
//
//    private Vision()
//    {
//        imageChooser = new SendableChooser();
//        imageChooser.addDefault("Input", IMAGE_TYPE.Input);
//        imageChooser.addObject("Threshold", IMAGE_TYPE.Threshold);
//        SmartDashboard.putData("Image to show", imageChooser);
//
//        frameSender = new FrameSender();
//        senderThread = new Thread(frameSender);
//        senderThread.setPriority(Thread.MIN_PRIORITY);
//        senderThread.start();
//
//        visionThread = new Thread(this);
//        visionThread.setPriority(Thread.MIN_PRIORITY);
//        Start();
//    }
//
//    public void Start()
//    {
//        if (!visionThread.isAlive())
//            visionThread.start();
//    }
//
//    @Override
//    public void run()
//    {
//        SmartDashboard.putString("Vision", "Running");
//
//        double TimeLastVision = 0;
//
//        Image frame;
//        final Image frameTH;
//
//        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
//        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
//
//        while (true)
//        {
//            // grab from the chooser which image to use
//            IMAGE_TYPE imageToSend = ((IMAGE_TYPE) imageChooser.getSelected());
//
//            if (cameraRequested != cameraCurrent)
//            {
//                setupFrameGrabber(cameraRequested);
//            }
//
//            SmartDashboard.putString("CameraCurrent", cameraCurrent.toString());
//            SmartDashboard.putString("CameraRequested",
//                    cameraRequested.toString());
//
//            try
//            {
//                // save off the start time so we can see how long it takes to
//                // process a frame
//                TimeLastVision = Timer.getFPGATimestamp();
//
//                // grab a new frame
//                if (frameThread.isAlive()) // Redundant check
//                {
//                    frame = frameGrabber.getFrame();
//                }
//
//                if (cameraCurrent == CAMERAS.MAIN)
//                {
//                    // SmartDashboard.putString("Vision", "Main camera");
//
//                    // GetImageSizeResult size =
//                    // NIVision.imaqGetImageSize(frame);
//                    // SmartDashboard.putString("FrameSize", size.width + "," +
//                    // size.height);
//
//                    if (targetDetectionOn)
//                    {
//                        // target detection is on, so adjust the image to filter
//                        // out all the nonsense
//                        filterTargets(frame, frameTH);
//
//                        printTargetInfo();
//                    }
//                    else
//                    {
//                        largestTarget = null;
//                    }
//                }
//
//                else if (cameraCurrent == CAMERAS.BACK)
//                {
//                    // SmartDashboard.putString("Vision", "Back camera");
//
//                    // GetImageSizeResult size =
//                    // NIVision.imaqGetImageSize(frame);
//                    // SmartDashboard.putString("FrameSize", size.width + "," +
//                    // size.height);
//                    largestTarget = null;
//
//                }
//
//                RescaleSize =
//                        Preferences.getInstance().getInt("ImageRescaleSize", 1);
//
//                sendSmartDashboardImage(frame, frameTH, imageToSend);
//
//                SmartDashboard.putString("Vision error", "");
//                SmartDashboard.putNumber("Vision Task Length",
//                        Timer.getFPGATimestamp() - TimeLastVision);
//            }
//            catch (Exception e)
//            {
//                SmartDashboard.putString("Vision error",
//                        "Processing: " + e.getMessage());
//            }
//
//            // Timer.delay(Math.max(0, TIME_RATE_VISION -
//            // (Timer.getFPGATimestamp() - TimeLastVision)));
//        }
//    }
//
//    private void setupFrameGrabber(CAMERAS newCamera)
//    {
//        if (cameraCurrent != CAMERAS.NONE)
//        {
//            frameGrabber.terminate();
//        }
//
//        if (newCamera != CAMERAS.NONE)
//        {
//            String name = "";
//
//            if (newCamera == CAMERAS.MAIN)
//            {
//                name = "cam0";
//            }
//            else if (newCamera == CAMERAS.BACK)
//            {
//                name = "cam1";
//            }
//
//            if (frameGrabber == null)
//            {
//                frameGrabber = new FrameGrabber(name);
//            }
//            else
//            {
//                frameGrabber.Init(name);
//            }
//
//            if (frameThread == null)
//            {
//                frameThread = new Thread(frameGrabber);
//                frameThread.setPriority(Thread.NORM_PRIORITY);
//                frameThread.start();
//            }
//        }
//
//        cameraCurrent = newCamera;
//    }
//
//    private void filterTargets(Image frame, Image frameTH)
//    {
//        NIVision.imaqColorThreshold(frameTH, frame, 255, ColorMode.HSL,
//                new Range(Preferences.getInstance().getInt("Hmin", 98),
//                        Preferences.getInstance().getInt("Hmax", 150)),
//                new Range(Preferences.getInstance().getInt("Smin", 78),
//                        Preferences.getInstance().getInt("Smax", 255)),
//                new Range(Preferences.getInstance().getInt("Lmin", 73),
//                        Preferences.getInstance().getInt("Lmax", 255)));
//
//        // get the number of particles
//        int particleCount = NIVision.imaqCountParticles(frameTH, 1);
//        SmartDashboard.putNumber("Particles", particleCount);
//
//        // find the bounding rectangle, then send
//        double minArea = Preferences.getInstance().getInt("MinArea", 0);
//        double biggestArea = minArea - 1;
//
//        int largestIndex = -1;
//
//        // for each particle, calculate values
//        for (int i = 0; i < particleCount; i++)
//        {
//            double area =
//                    NIVision.imaqMeasureParticle(frameTH, i, 0,
//                            MeasurementType.MT_AREA);
//
//            if (area > biggestArea)
//            {
//                biggestArea = area;
//                largestIndex = i;
//            }
//        }
//
//        // store largest target
//        if (largestIndex > -1)
//        {
//            double biggestX = 0, biggestY = 0;
//            double biggestW = 0, biggestH = 0;
//
//            biggestX =
//                    NIVision.imaqMeasureParticle(frameTH, largestIndex, 0,
//                            MeasurementType.MT_CENTER_OF_MASS_X);
//            biggestY =
//                    NIVision.imaqMeasureParticle(frameTH, largestIndex, 0,
//                            MeasurementType.MT_CENTER_OF_MASS_Y);
//            biggestH =
//                    NIVision.imaqMeasureParticle(frameTH, largestIndex, 0,
//                            MeasurementType.MT_BOUNDING_RECT_HEIGHT);
//            biggestW =
//                    NIVision.imaqMeasureParticle(frameTH, largestIndex, 0,
//                            MeasurementType.MT_BOUNDING_RECT_WIDTH);
//
//            largestTarget =
//                    new Target(biggestX, biggestY, biggestArea, biggestH,
//                            biggestW);
//        }
//        else
//        {
//            largestTarget = null;
//        }
//    }
//
//    private void printTargetInfo()
//    {
//        if (largestTarget != null)
//        {
//            // Write Text of Target Location
//            String descXY =
//                    (int) largestTarget.X() + ", " + (int) largestTarget.Y();
//            String descWH =
//                    (int) largestTarget.W() + ", " + (int) largestTarget.H();
//            String descPitch = df.format(largestTarget.Pitch()) + "\u00b0";
//            String descYaw = df.format(largestTarget.Yaw()) + "\u00b0";
//            String descDist = df.format(largestTarget.DistanceToTarget()) + "in";
//
//            SmartDashboard.putString("TargetXY", descXY);
//            SmartDashboard.putString("TargetWH", descWH);
//            SmartDashboard.putString("TargetPitch", descPitch);
//            SmartDashboard.putString("TargetYaw", descYaw);
//            SmartDashboard.putString("TargetDistance", descDist);
//        }
//        else
//        {
//            SmartDashboard.putString("TargetXY", "");
//            SmartDashboard.putString("TargetWH", "");
//            SmartDashboard.putString("TargetPitch", "");
//            SmartDashboard.putString("TargetYaw", "");
//            SmartDashboard.putString("TargetDistance", "");
//        }
//    }
//
//    private void sendSmartDashboardImage(Image frame, Image frameTH,
//            IMAGE_TYPE imageToSend)
//    {
//
//        if (imageToSend == IMAGE_TYPE.Input && cameraCurrent == CAMERAS.MAIN)
//        {
//            frameSender.sendFrame(frame, largestTarget, COLORS.MAGENTA);
//        }
//        else if (imageToSend == IMAGE_TYPE.Threshold
//                 && cameraCurrent == CAMERAS.MAIN)
//        {
//            frameSender.sendFrame(frameTH, largestTarget);
//        }
//        else if ((imageToSend == IMAGE_TYPE.DriveBack || cameraCurrent == CAMERAS.BACK))
//        {
//            frameSender.sendFrame(frame);
//        }
//    }
//
//    public boolean fixCamera(boolean set)
//    {
//        boolean done = false;
//
//        if (cameraCurrent == CAMERAS.MAIN)
//        {
//            if (frameGrabber.fixCamera(set))
//            {
//                done = true;
//            }
//        }
//        else if (cameraCurrent == CAMERAS.BACK)
//        {
//            if (frameGrabber.fixCamera(set))
//            {
//                done = true;
//            }
//        }
//
//        return done;
//    }
//}