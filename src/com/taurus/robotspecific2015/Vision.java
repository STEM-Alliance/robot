package com.taurus.robotspecific2015;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.GetImageSizeResult;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.Range;

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
    private Image frame;

    Thread visionThread;

    private int ToteLineStartX = 100 / 320;
    private int ToteLineStartY = 60 / 240;
    private int ToteLineEndX = 60 / 320;
    private int ToteLineEndY = 180 / 240;

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

                chooser = ((Integer) imageChooser.getSelected()).intValue();

                if (chooser == 0)
                {
                    CameraServer.getInstance().setImage(frame);
                }
                else if (chooser == 1)
                {
                    NIVision.imaqColorThreshold(frame, frame, 255,
                            ColorMode.HSL,
                            new Range(Application.prefs.getInt("Hmin", 30),
                                    Application.prefs.getInt("Hmax", 60)),
                            new Range(Application.prefs.getInt("Smin", 60),
                                    Application.prefs.getInt("Smax", 255)),
                            new Range(Application.prefs.getInt("Lmin", 100),
                                    Application.prefs.getInt("Lmax", 255)));

                    // TODO: NIVision.imaqParticleFilter4(frame, frame, new
                    // Particle, options, roi)

                    CameraServer.getInstance().setImage(frame);
                }
                else if (chooser == 2)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        NIVision.imaqDrawLineOnImage(frame, frame,
                                DrawMode.DRAW_VALUE, ToteLines[0][i],
                                ToteLines[1][i], 0.0f);

                    }

                    // for (int i = 0; i <= size.height; i += (size.height/12))
                    // {
                    // NIVision.imaqDrawLineOnImage(frame, frame,
                    // DrawMode.DRAW_VALUE, new Point(0, i),
                    // new Point(size.width, i), 0.0f);
                    // }
                    // for (int i = 0; i <= size.width; i += (size.width/16))
                    // {
                    // NIVision.imaqDrawLineOnImage(frame, frame,
                    // DrawMode.DRAW_VALUE, new Point(i, 0),
                    // new Point(i, size.height), 0.0f);
                    // }

                    CameraServer.getInstance().setImage(frame);
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
}
