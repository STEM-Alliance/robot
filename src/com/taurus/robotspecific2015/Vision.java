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

    private int ToteLineStartX = 100 / 320;
    private int ToteLineStartY = 60 / 240;
    private int ToteLineEndX = 60 / 320;
    private int ToteLineEndY = 180 / 240;

    private boolean toteDetectionOn = true;

    public Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input image", Integer.valueOf(0));
        imageChooser.addObject("Processed image", Integer.valueOf(1));
        imageChooser.addObject("Guide image", Integer.valueOf(2));
        SmartDashboard.putData("Image to show", imageChooser);

        visionThread = new Thread(new Vision());
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

        int chooser = 0;

        NIVision.IMAQdxGrab(session, frame, 1);
        GetImageSizeResult size = NIVision.imaqGetImageSize(frame);

        // set up the lines for tote detection
        Point[][] ToteLines = new Point[2][4];
        ToteLines[0][0] = new Point(ToteLineStartX * size.width, ToteLineStartY
                * size.height);
        ToteLines[0][1] = new Point(size.width - ToteLineStartX * size.width,
                ToteLineStartY * size.height);
        ToteLines[0][2] = new Point(ToteLineStartX * size.width + 1,
                ToteLineStartY * size.height);
        ToteLines[0][3] = new Point(size.width - ToteLineStartX * size.width
                + 1, ToteLineStartY * size.height);

        ToteLines[1][0] = new Point(ToteLineEndX * size.width, ToteLineEndY
                * size.height);
        ToteLines[1][1] = new Point(size.width - ToteLineStartX * size.width,
                ToteLineStartY * size.height);
        ToteLines[1][2] = new Point(ToteLineEndX * size.width + 1, ToteLineEndY
                * size.height);
        ToteLines[1][3] = new Point(size.width - ToteLineStartX * size.width
                + 1, ToteLineStartY * size.height);

        while (true)
        {
            if ((Timer.getFPGATimestamp() - TimeLastVision) > TIME_RATE_VISION)
            {
                TimeLastVision = Timer.getFPGATimestamp();

                NIVision.IMAQdxGrab(session, frame, 1);
                
                if (((Integer)imageChooser.getSelected()).intValue() == 0)
                {
                    CameraServer.getInstance().setImage(frame);
                }                
                else if (((Integer)imageChooser.getSelected()).intValue() == 1)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        NIVision.imaqDrawLineOnImage(frameWithLine, frame,
                                DrawMode.DRAW_VALUE, ToteLines[0][i],
                                ToteLines[1][i], 0.0f);
                    }
                        
                    CameraServer.getInstance().setImage(frameWithLine);
                }

                if (toteDetectionOn)
                {
                    NIVision.imaqColorThreshold(frameTH, frame, 255, ColorMode.HSL, 
                            new Range(Application.prefs.getInt("Hmin", 30), Application.prefs.getInt("Hmax", 60)), 
                            new Range(Application.prefs.getInt("Smin", 60), Application.prefs.getInt("Smax", 255)), 
                            new Range(Application.prefs.getInt("Lmin", 100), Application.prefs.getInt("Lmax", 255)));
                    
                    if (((Integer)imageChooser.getSelected()).intValue() == 2)
                    {
                        CameraServer.getInstance().setImage(frameTH);
                    }
                    
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
                    
                    for (int i = 0; i < particleCount; i++)
                    {
                        NIVision.imaqDrawShapeOnImage(frameTH, frameTH, new Rect(0,0,10,10), DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 255);
                    }
                    
                    if (((Integer)imageChooser.getSelected()).intValue() == 3)
                    {
                        CameraServer.getInstance().setImage(frameTH);
                    }
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
