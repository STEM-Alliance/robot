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
        DriveBack,
        INVALID
    };

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
    
    private int RescaleSize = 1; // large size == smaller image; 2 == 1/2 image size
    
    private static Vision instance = null;
    
    private final double TIME_RATE_VISION = 1.0/30.0; // frame time at 30fps
    private DecimalFormat df = new DecimalFormat("#.##");
    
    private SendableChooser imageChooser = new SendableChooser();

    Thread visionThread;

    Camera cameraMain;
    Camera cameraBack;

    private boolean targetDetectionOn = true;
    private boolean imageRotation = true;
    private boolean BackCameraEnabled = false;
    
    private int cameraQuality = 50;
    
    private Target largestTarget;

    public static Vision getInstance(){
        if (instance == null){
            instance = new Vision();
         
        }
        return instance;
        
    }
    
    public void enableBackCamera(boolean BackCamera)
    {
        synchronized (visionThread)
        {
            this.BackCameraEnabled = BackCamera;
        }
    }
    
    public void setTargetDetectionOn(boolean enable){
        synchronized(visionThread){
            targetDetectionOn = enable;
        }
    }

    public void setImageRotation(boolean enable){
        synchronized(visionThread){
            imageRotation = enable;
        }
    }

    public Target getTarget(){
        synchronized (visionThread){
            return largestTarget;
        }
    }

    private Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input", ImageChoices.Input);
        imageChooser.addObject("Threshold", ImageChoices.Threshold);
        SmartDashboard.putData("Image to show", imageChooser);

        visionThread = new Thread(this);
        visionThread.setPriority(Thread.MIN_PRIORITY);
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

        SmartDashboard.putString("CameraMainState", "Running");
        cameraMain = new Camera("cam0");
        cameraBack = new Camera("cam1");
        
        final Image frame, frameBack, frameTH, frameDownsampled, frameDownsampledTH;
        
        double TimeLastVision = 0;
        double TimeStart = Timer.getFPGATimestamp();

        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameBack = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
        frameDownsampled = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameDownsampledTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

        while (true)
        {
            // grab from the chooser which image to use
            ImageChoices imageToSend = ((ImageChoices) imageChooser.getSelected());

            if(!BackCameraEnabled)
            {

                if (cameraBack.isCapturing())    
                {
                    SmartDashboard.putString("CameraBackState", "Stop Capture");
                    cameraBack.stopCapture();
                }
                
                if(cameraBack.isOpen())
                {
                    SmartDashboard.putString("CameraBackState", "Closing");
                    cameraBack.closeCamera();
                }
                
                TimeStart = setupMain(TimeStart);
            }
            else
            {
                
                if (cameraMain.isCapturing())    
                {
                    SmartDashboard.putString("CameraMainState", "Stop Capture");
                    cameraMain.stopCapture();
                }
                
                if(cameraMain.isOpen())
                {
                    SmartDashboard.putString("CameraMainState", "Closing");
                    cameraMain.closeCamera();
                }
                
                setupBack(imageToSend);
            }
            
            try
            {
                // save off the start time so we can see how long 
                // it takes to process a frame
                TimeLastVision = Timer.getFPGATimestamp();
                
                // we have an open camera session 
                if (cameraMain.isCapturing() && !BackCameraEnabled)
                {
                    SmartDashboard.putString("CameraMainState", "Capturing");

                    // grab a new frame
                    cameraMain.getImage(frame);

                    if(imageRotation)
                    {
                        NIVision.imaqFlip(frame, frame, FlipAxis.HORIZONTAL_AXIS);
                        NIVision.imaqFlip(frame, frame, FlipAxis.VERTICAL_AXIS);
                    }
                    
                    // print input image size
                    //GetImageSizeResult size = NIVision.imaqGetImageSize(frame);
                    //SmartDashboard.putString("FrameSize", size.width + "," + size.height);

                    // only run if enabled
                    if (targetDetectionOn)
                    {
                        // target detection is on, so adjust the image to filter
                        // out all the nonsense
                        filterTargets(frame, frameTH);

                        printTargetInfo();
                    }
                }
                
                if (cameraBack.isCapturing() && (imageToSend == ImageChoices.DriveBack || BackCameraEnabled))
                {
                    SmartDashboard.putString("CameraBackState", "Capturing");
                    cameraBack.getImage(frameBack);
                }
                
                // get the latest rescale size from the prefs
                RescaleSize = Preferences.getInstance().getInt("ImageRescaleSize", 1);
                
                if(imageToSend == ImageChoices.Input && !BackCameraEnabled)
                {
                    addTargets(frame, COLORS.MAGENTA);

                    // scale if needed
                    //NIVision.imaqScale(frameDownsampled, frame, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                    //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
                    //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

                    CameraServer.getInstance().setImage(frame);
                }
                else if(imageToSend == ImageChoices.Threshold && !BackCameraEnabled)
                {
                    addTargets(frameTH, COLORS.WHITE);
                    
                    // scale if needed
                    //NIVision.imaqScale(frameDownsampledTH, frameTH, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                    //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampledTH);
                    //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

                    CameraServer.getInstance().setImage(frameTH);
                }
                else if (cameraBack.isCapturing() && (imageToSend == ImageChoices.DriveBack || BackCameraEnabled))
                {
                    // scale if needed
                    //NIVision.imaqScale(frameDownsampled, frameBack, RescaleSize, RescaleSize, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);

                    //GetImageSizeResult sizeDownsampled = NIVision.imaqGetImageSize(frameDownsampled);
                    //SmartDashboard.putString("FrameSizeDownsampled", sizeDownsampled.width + "," + sizeDownsampled.height);

                    CameraServer.getInstance().setImage(frameBack);
                }
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
    
    private void setupBack(ImageChoices imageToSend)
    {
        if(imageToSend == ImageChoices.DriveBack || BackCameraEnabled)
        {
            // if we haven't opened the camera session
            if(!cameraBack.isOpen())
            {
                SmartDashboard.putString("CameraBackState", "Opening");
                cameraBack.openCamera();
            }
    
            // if we haven't started capturing
            if (!cameraBack.isCapturing())    
            {
                SmartDashboard.putString("CameraBackState", "Start Capture");
                cameraBack.startCapture();
    
                cameraBack.setBrightness(130);
                
                
            }
        }
        else
        {
            if (cameraBack.isCapturing())    
            {
                SmartDashboard.putString("CameraBackState", "Stop Capture");
                cameraBack.stopCapture();
            }
            
            if(cameraBack.isOpen())
            {
                SmartDashboard.putString("CameraBackState", "Closing");
                cameraBack.closeCamera();
            }
    
            SmartDashboard.putString("CameraBackState", "Closed");
        }
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
        synchronized (visionThread)
        {
            if(biggestArea > Preferences.getInstance().getInt("MinArea", 0))
            {
                largestTarget = new Target(biggestX, biggestY, biggestArea, biggestH, biggestW, biggestOrientation);

            }
            else
            {
                largestTarget = null;
            }
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

    private double setupMain(double TimeStart)
    {        
        // if we haven't opened the camera session
        if(!cameraMain.isOpen())
        {
            SmartDashboard.putString("CameraMainState", "Opening");
            cameraMain.openCamera();
        }

        // if we haven't started capturing
        if (!cameraMain.isCapturing())    
        {
            SmartDashboard.putString("CameraMainState", "Start Capture");
            cameraMain.startCapture();

            cameraMain.setBrightness(86);
            
//            try
//            {
//                Thread.sleep(2000);
//            }
//            catch (InterruptedException e1)
//            {
//            }
            
            TimeStart = Timer.getFPGATimestamp();
        }
        
        cameraMain.printRanges();
        
        if(Timer.getFPGATimestamp() - TimeStart < 2)
        {
            // force it for 2 seconds to handle the weird settings bug
            updateSettingsMain(true);
        }
        else
        {
            // otherwise only update if it changes
            updateSettingsMain();
        }
        
        return TimeStart;
    }
    
    /**
     * Send the image to SmartDashboard
     * @param frame input image
     * @param frameDownsampled downscaled image to save it to
     */
    private synchronized void addTargets(Image frame, int color)
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
            // draw an outline of a rectangle in RED around the target
            NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, color);
            
        }
    }

    private synchronized void updateSettingsMain()
    {
        updateSettingsMain(false);
    }

    /**
     * If any of the Preferences change, send the latest to the camera object
     */
    private synchronized void updateSettingsMain(boolean forceUpdate)
    {
        int cameraQual = Preferences.getInstance().getInt("CameraQuality", 50);
        if(cameraQual != cameraQuality || forceUpdate)
        {
            CameraServer.getInstance().setQuality(cameraQual);
            cameraQuality = cameraQual;
        }
        
        int videoMode = Preferences.getInstance().getInt("VIDEO_MODE", 93);
        if(cameraMain.getVideoMode() != videoMode || forceUpdate)
        {
            cameraMain.setVideoMode(videoMode);
        }
        
        int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4500);
        if(cameraMain.getWhiteBalanceManual() != whiteBalance || forceUpdate)
        {
            cameraMain.setWhiteBalanceManual(whiteBalance);
        }
        
        double exposure = Preferences.getInstance().getDouble("Exposure", 1);
        if(cameraMain.getExposureManual() != exposure || forceUpdate)
        {
            cameraMain.setExposureManual(exposure);
        }
        
        double brightness = Preferences.getInstance().getDouble("Brightness", 1);
        if(cameraMain.getBrightness() != brightness || forceUpdate)
        {
            cameraMain.setBrightness(brightness);
        }
        
        double sat = Preferences.getInstance().getDouble("Saturation", .5);
        if(cameraMain.getSaturation() != sat || forceUpdate)
        {
            cameraMain.setSaturation(sat);
        }
    }
    
    public synchronized void fixCamera(boolean set)
    {
        if(set)
        {
            cameraMain.setBrightness(Preferences.getInstance().getDouble("Brightness", 1) + 1);
            Preferences.getInstance().putDouble("Brightness", Preferences.getInstance().getDouble("Brightness", 1) + 1);
        }
        else
        {
            cameraMain.setBrightness(Preferences.getInstance().getDouble("Brightness", 1) - 1);
            Preferences.getInstance().putDouble("Brightness", Preferences.getInstance().getDouble("Brightness", 1) - 1);
        }
    }
}
