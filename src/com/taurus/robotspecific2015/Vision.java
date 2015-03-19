package com.taurus.robotspecific2015;

import java.text.DecimalFormat;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Runnable {

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

    private int session = -1;
    private Image frame, frameTH;

    Thread visionThread;

    private boolean toteDetectionOn = true;

    private double resultX = 0, resultY = 0;
    private double resultOrientation = 0;
    private boolean particleSeen = false;

    public double getResultX()
    {
        synchronized (visionThread)
        {
            return resultX;
        }
    }

    public double getResultY()
    {
        synchronized (visionThread)
        {
            return resultY;
        }
    }

    public double getResultOrientation()
    {
        synchronized (visionThread)
        {
            return resultOrientation;
        }
    }

    public boolean getToteSeen()
    {
        synchronized (visionThread)
        {
            return particleSeen;
        }
    }

    public Vision()
    {
        imageChooser = new SendableChooser();
        imageChooser.addObject("Input Front", Integer.valueOf(0));
        imageChooser.addDefault("Input Back", Integer.valueOf(1));
        imageChooser.addObject("Thresholded", Integer.valueOf(2));
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
        int prevImageToSend = -1;

        double TimeLastVision = 0;

        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

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
                    SmartDashboard.putString("Vision error", e.getMessage());
                }
                
                session = OpenCamera(imageToSend == 1 ? "cam0" : "cam1");
            }


            if (session != -1 && (Timer.getFPGATimestamp() - TimeLastVision) > TIME_RATE_VISION)
            {
                TimeLastVision = Timer.getFPGATimestamp();

                try
                {
                    NIVision.IMAQdxGrab(session, frame, 1);
                    
                    GetImageSizeResult size = NIVision.imaqGetImageSize(frame);
                    
                    SmartDashboard.putString("FrameSize", size.width
                                                          + ","
                                                          + size.height);

                    if (imageToSend == 0 || imageToSend == 1)
                    {
                        // just send the raw image
                        CameraServer.getInstance().setImage(frame);
                    }

                    if (toteDetectionOn)
                    {
                        // tote detection is on, so adjust the image to filter
                        // out all the nonsense                        

                        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);

                        NIVision.imaqColorThreshold(frameTH, frame, 255,
                                ColorMode.HSL,
                                new Range(Application.prefs.getInt("Hmin", 30),
                                        Application.prefs.getInt("Hmax", 60)),
                                new Range(Application.prefs.getInt("Smin", 60),
                                        Application.prefs.getInt("Smax", 255)),
                                new Range(
                                        Application.prefs.getInt("Lmin", 100),
                                        Application.prefs.getInt("Lmax", 255)));

                        if (imageToSend == 2)
                        {
                            // send the raw black/white image that should have
                            // just yellow totes
                            CameraServer.getInstance().setImage(frameTH);
                        }

                        // filter out any missed particles that aren't the tote
                        ParticleFilterCriteria2[] criteria =
                                new ParticleFilterCriteria2[] { new ParticleFilterCriteria2(
                                        MeasurementType.MT_AREA_BY_IMAGE_AREA,
                                        /* lower */Application.prefs.getInt(
                                                "AreaMin", 1),
                                        /* upper */Application.prefs.getInt(
                                                "AreaMax", 76800),
                                        /* calibrated */0, /* exclude */0), };
                        ParticleFilterOptions2 options =
                                new ParticleFilterOptions2(
                                /* rejectMatches */0, /* rejectBorder */
                                0, /* fillHoles */0, /* connectivity8 */
                                1);
                        ROI roi = NIVision.imaqCreateROI();

                        NIVision.imaqParticleFilter4(frameTH, frameTH,
                                criteria, options, roi);

                        int particleCount =
                                NIVision.imaqCountParticles(frameTH, /* connectivity8 */
                                        1);
                        SmartDashboard.putNumber("Particles", particleCount);

                        // find the bounding rectangle, then send
                        double biggestArea = -1;
                        double biggestX = 0, biggestY = 0;
                        double biggestOrientation = 0;

                        for (int i = 0; i < particleCount; i++)
                        {
                            double centerx =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_CENTER_OF_MASS_X);
                            double centery =
                                    NIVision.imaqMeasureParticle(frameTH, i, 0,
                                            MeasurementType.MT_CENTER_OF_MASS_Y);
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
                                biggestOrientation = orientation;
                            }
                        }

                        SmartDashboard.putNumber("Area", biggestArea);
                        SmartDashboard
                                .putString("Center", df.format(biggestX)
                                                     + ","
                                                     + df.format(biggestY));
                        SmartDashboard.putNumber("Orientation",
                                biggestOrientation);

                        synchronized (visionThread)
                        {
                            particleSeen = particleCount > 0;
                            resultX = biggestX / size.width;
                            resultY = biggestY / size.height;
                            resultOrientation = biggestOrientation;
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

            int videoMode = Application.prefs.getInt("VIDEO_MODE", 93);
            NIVision.IMAQdxSetAttributeU32(session, ATTR_VIDEO_MODE, videoMode);

            // int whiteBalance = Application.prefs.getInt("WhiteBalance",
            // 4500);
//            NIVision.IMAQdxSetAttributeString(session, ATTR_WB_MODE,
//                    "Manual");
            // NIVision.IMAQdxSetAttributeI64(session, ATTR_WB_VALUE,
            // whiteBalance);

            // int exposure = Application.prefs.getInt("Exposure", 1);
            // NIVision.IMAQdxSetAttributeString(session, ATTR_EX_MODE,
            // "Manual");
            // NIVision.IMAQdxSetAttributeI64(session, ATTR_EX_VALUE, exposure);

            {
                double brightness =
                        Application.prefs.getDouble("Brightness", .25);

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
            SmartDashboard.putString("Vision error", e.getMessage());
        }
        return session;
    }
}
