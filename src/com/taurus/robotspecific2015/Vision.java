package com.taurus.robotspecific2015;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Runnable {

    private final double TIME_RATE_VISION = .033;

    private SendableChooser imageChooser = new SendableChooser();

    private static final String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
//    private static final String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
//    private static final String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
//    private static final String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
//    private static final String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
//    private static final String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
//    private static final String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";

    private int session;
    private Image frame, frameWithLine, frameTH;

    Thread visionThread;

    private double ToteLineStartX = 100 / 320;
    private double ToteLineStartY = 60 / 240;
    private double ToteLineEndX = 60 / 320;
    private double ToteLineEndY = 180 / 240;

    private boolean toteDetectionOn = true;

    public Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input", Integer.valueOf(0));
        imageChooser.addObject("Guide", Integer.valueOf(1));
        imageChooser.addObject("Thresholded", Integer.valueOf(2));
        imageChooser.addObject("Particles", Integer.valueOf(3));
        SmartDashboard.putData("Image to show", imageChooser);

        visionThread = new Thread(this);
    }

    public void Start()
    {
        visionThread.setPriority(Thread.MIN_PRIORITY);
        visionThread.start();
    }

    @Override
    public void run()
    {
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        frameWithLine = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 0);

        // the camera name (ex "cam0") can be found through the roborio web
        // interface
        session = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);

        // print to dashboard a list of all available video modes
        // NIVision.dxEnumerateVideoModesResult enumerated =
        // NIVision.IMAQdxEnumerateVideoModes(session);
        // String Modes = "";
        // for (NIVision.IMAQdxEnumItem mode : enumerated.videoModeArray)
        // {
        // Modes += mode.Name + " " + mode.Value + ", ";
        // }
        // SmartDashboard.putString("Modes", Modes);

        int videoMode = Application.prefs.getInt("VIDEO_MODE", 93);

        NIVision.IMAQdxSetAttributeU32(session, ATTR_VIDEO_MODE, videoMode);
        NIVision.IMAQdxConfigureGrab(session);

        NIVision.IMAQdxStartAcquisition(session);

        double TimeLastVision = 0;

        CameraServer.getInstance().setQuality(15);

        NIVision.IMAQdxGrab(session, frame, 1);
        GetImageSizeResult size = NIVision.imaqGetImageSize(frame);

        // set up the lines for tote detection
        Point[][] ToteLines = new Point[][] {
                {
                    new Point(
                            (int)(ToteLineStartX * size.width), 
                            (int)(ToteLineStartY * size.height)),
                    new Point(
                            (int)(size.width - ToteLineStartX * size.width), 
                            (int)(ToteLineStartY * size.height)),
                    new Point(
                            (int)(ToteLineStartX * size.width + 1), 
                            (int)(ToteLineStartY * size.height)),
                    new Point(
                            (int)(size.width - ToteLineStartX * size.width + 1), 
                            (int)(ToteLineStartY * size.height)),
                },
                {
                    new Point(
                            (int)(ToteLineEndX * size.width), 
                            (int)(ToteLineEndY * size.height)),
                    new Point(
                            (int)(size.width - ToteLineEndX * size.width), 
                            (int)(ToteLineEndY * size.height)),
                    new Point(
                            (int)(ToteLineEndX * size.width + 1), 
                            (int)(ToteLineEndY * size.height)),
                    new Point(
                            (int)(size.width - ToteLineEndX * size.width + 1), 
                            (int)(ToteLineEndY * size.height)),
                },
        };

        int imageToSend = ((Integer)imageChooser.getSelected()).intValue();
        
        while (true)
        {
            if ((Timer.getFPGATimestamp() - TimeLastVision) > TIME_RATE_VISION)
            {
                TimeLastVision = Timer.getFPGATimestamp();
                
                // grab from the chooser which image to use
                imageToSend = ((Integer)imageChooser.getSelected()).intValue();
                
                try
                {
                    NIVision.IMAQdxGrab(session, frame, 1);
                    
                    SmartDashboard.putString("FrameSize", size.width+","+size.height);
                    
                    if (imageToSend == 0)
                    {
                        // just send the raw image
                        CameraServer.getInstance().setImage(frame);
                    }                
                    else if (imageToSend == 1)
                    {
                        // send the image with the tote lines on it
                        NIVision.imaqDuplicate(frameWithLine, frame);
                        
                        for (int i = 0; i < 4; i++)
                        {
                            NIVision.imaqDrawLineOnImage(frameWithLine, frameWithLine,
                                    DrawMode.DRAW_VALUE, ToteLines[0][i],
                                    ToteLines[1][i], 127);
                        }
                            
                        CameraServer.getInstance().setImage(frameWithLine);
                    }
    
                    if (toteDetectionOn)
                    {
                        // tote detection is on, so adjust the image to filter out all the nonsense
                        NIVision.imaqLowPass(frame, frame, 3, 3, .4f, null);
                        
                        NIVision.imaqColorThreshold(frameTH, frame, 255, ColorMode.HSL, 
                                new Range(Application.prefs.getInt("Hmin", 30), Application.prefs.getInt("Hmax", 60)), 
                                new Range(Application.prefs.getInt("Smin", 60), Application.prefs.getInt("Smax", 255)), 
                                new Range(Application.prefs.getInt("Lmin", 100), Application.prefs.getInt("Lmax", 255)));
                        
                        if (imageToSend == 2)
                        {
                            // send the raw black/white image that should have just yellow totes
                            CameraServer.getInstance().setImage(frameTH);
                        }
                        
                        // filter out any missed particles that aren't the tote
                        ParticleFilterCriteria2[] criteria =
                            new ParticleFilterCriteria2[] {
                                new ParticleFilterCriteria2(
                                        MeasurementType.MT_AREA_BY_IMAGE_AREA, 
                                        /*lower*/ Application.prefs.getInt("AreaMin", 10), 
                                        /*upper*/ Application.prefs.getInt("AreaMax", 76800), 
                                        /*calibrated*/ 0, /*exclude*/ 0),
                            };
                        ParticleFilterOptions2 options = 
                                new ParticleFilterOptions2(/*rejectMatches*/ 0, /*rejectBorder*/ 0, /*fillHoles*/ 0, /*connectivity8*/ 1);
                        ROI roi = NIVision.imaqCreateROI();
                        
                        NIVision.imaqParticleFilter4(frameTH, frameTH, criteria, options, roi);
                        
                        int particleCount = NIVision.imaqCountParticles(frameTH, /*connectivity8*/ 1);
                        SmartDashboard.putNumber("Particles", particleCount);
                        
                        if (imageToSend == 3)
                        {
                            // find the bounding rectangle, then send
                            for (int i = 0; i < particleCount; i++)
                            {
                                double left = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_LEFT);
                                double top = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_TOP);
                                double right = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_RIGHT);
                                double bottom = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_BOUNDING_RECT_BOTTOM);
                                double centerx = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CENTER_OF_MASS_X);                                
                                double centery = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CENTER_OF_MASS_Y);
                                double ellipsemajor = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_ELLIPSE_MAJOR_AXIS);
                                double ellipseminor =  NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_ELLIPSE_MINOR_AXIS);
                                double ellipseminorf = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_ELLIPSE_MINOR_AXIS_FERET);
                                double rectmajor = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_RECT_LONG_SIDE);
                                double rectminor = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_RECT_SHORT_SIDE);
                                double rectminorf = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_EQUIVALENT_RECT_SHORT_SIDE_FERET);
                                double area = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_AREA);
                                double convexarea = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CONVEX_HULL_AREA);
                                double convexperimeter = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_CONVEX_HULL_PERIMETER);
                                double orientation = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_ORIENTATION);
                                double perimiter = NIVision.imaqMeasureParticle(frameTH, i, 0, MeasurementType.MT_PERIMETER);
                                
                                SmartDashboard.putString("Bounding", left+","+top+","+right+","+bottom);
                                SmartDashboard.putString("Ellipse", ellipsemajor+","+ellipseminor+","+ellipseminorf);
                                SmartDashboard.putString("Rectangle", rectmajor+","+rectminor+","+rectminorf);
                                SmartDashboard.putString("Convex", convexarea+","+convexperimeter);
                                SmartDashboard.putString("Outside", area+","+perimiter);
                                SmartDashboard.putString("Center", centerx+","+centery);
                                SmartDashboard.putNumber("Orientation", orientation);
                            }
                        
                            CameraServer.getInstance().setImage(frameTH);
                        }
                    }
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                } 
                
                SmartDashboard.putNumber("Vision Task Length", Timer.getFPGATimestamp() - TimeLastVision);
            }
            Timer.delay(Math.max(0,
                    TIME_RATE_VISION
                            - (Timer.getFPGATimestamp() - TimeLastVision)));
        }

        // NIVision.IMAQdxStopAcquisition(session);
    }
}
