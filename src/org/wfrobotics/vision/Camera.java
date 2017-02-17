//package org.wfrobotics.vision;
//
//import com.ni.vision.NIVision;
//import com.ni.vision.NIVision.Image;
//import com.ni.vision.VisionException;
//
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//
//@SuppressWarnings("unused")
//public class Camera
//{
//
//    public static String kDefaultCameraName = "cam0";
//
//    private static String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
//    private static String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
//    private static String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
//    private static String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
//    private static String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
//    private static String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
//    private static String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
//    private static String ATTR_ST_MODE = "CameraAttributes::Saturation::Mode";
//    private static String ATTR_ST_VALUE = "CameraAttributes::Saturation::Value";
//
//    public class WhiteBalance
//    {
//        public static final int kFixedIndoor = 3000;
//        public static final int kFixedOutdoor1 = 4000;
//        public static final int kFixedOutdoor2 = 5000;
//        public static final int kFixedFluorescent1 = 5100;
//        public static final int kFixedFlourescent2 = 5200;
//    }
//
//    private String m_name = kDefaultCameraName;
//    private int m_sessionId = -1;
//    private boolean m_active = false;
//    private String m_whiteBalance = "auto";
//    private int m_whiteBalanceValue = -1;
//    private String m_exposure = "auto";
//    private double m_exposureValue = 100;
//    private double m_brightness = 50;
//    private double m_saturation = 50;
//    private boolean m_needSettingsUpdate = true;
//    private int m_mode = 93;
//
//    public Camera()
//    {
//        m_name = kDefaultCameraName;
//        openCamera();
//    }
//
//    public Camera(String name)
//    {
//        m_name = name;
//        openCamera();
//    }
//    
//    public synchronized void changeCamera(String name)
//    {
//        closeCamera();
//        
//        m_needSettingsUpdate = true;
//        m_name = name;
//        openCamera();
//    }
//
//    public synchronized void openCamera()
//    {
//        if (m_sessionId != -1)
//        {
//            return; // Camera is already open
//        }
//
//        try
//        {
//            m_sessionId = NIVision.IMAQdxOpenCamera(m_name, NIVision.IMAQdxCameraControlMode.CameraControlModeController);
//            SmartDashboard.putString("Vision error", "");
//        }
//        catch (VisionException e)
//        {
//            SmartDashboard.putString("Vision error", "Open:" + e.getMessage());
//        }
//    }
//
//    public synchronized void closeCamera()
//    {
//        if (m_sessionId == -1)
//        {
//            return;
//        }
//
//        NIVision.IMAQdxCloseCamera(m_sessionId);
//        m_sessionId = -1;
//    }
//
//    public synchronized void startCapture()
//    {
//        if (m_sessionId == -1 || m_active)
//        {
//            return;
//        }
//        NIVision.IMAQdxSetAttributeU32(m_sessionId, ATTR_VIDEO_MODE, m_mode);
//        
//        NIVision.IMAQdxConfigureGrab(m_sessionId);
//        NIVision.IMAQdxStartAcquisition(m_sessionId);
//        m_active = true;
//    }
//
//    public synchronized void stopCapture()
//    {
//        if (m_sessionId == -1 || !m_active)
//        {
//            return;
//        }
//
//        NIVision.IMAQdxStopAcquisition(m_sessionId);
//        NIVision.IMAQdxUnconfigureAcquisition(m_sessionId);
//        m_active = false;
//    }
//    
//    public synchronized boolean isOpen()
//    {
//        return m_sessionId != -1;
//    }
//    
//    public synchronized boolean isCapturing()
//    {
//        return m_active && isOpen();
//    }
//
//    public synchronized void updateSettings()
//    {
//        boolean wasActive = m_active;
//        
//        // Stop acquistion, close and reopen camera
//        if (wasActive)
//        {
//            stopCapture();
//        }
//        if (m_sessionId != -1)
//        {
//            closeCamera();
//        }
//        
//        openCamera();
//
//        // Video Mode
//
//        // White Balance
////        if (m_whiteBalance == "auto")
////        {
////            NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_WB_MODE, "Auto");
////        }
////        else
////        {
//            NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_WB_MODE, "Manual");
//            if (m_whiteBalanceValue != -1)
//            {
//                NIVision.IMAQdxSetAttributeI64(m_sessionId, ATTR_WB_VALUE, m_whiteBalanceValue);
//            }
////        }
//
//        // Exposure
////        if (m_exposure == "auto")
////        {
////            NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_EX_MODE, "AutoAperaturePriority");
////        }
////        else
////        {
//            NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_EX_MODE, "Manual");
//            if (m_exposureValue > -1)
//            {
////                long minv = NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_EX_VALUE);
////                long maxv = NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_EX_VALUE);
////                long val = minv + (long) (((double) (maxv - minv)) * m_exposureValue);
//                NIVision.IMAQdxSetAttributeF64(m_sessionId, ATTR_EX_VALUE, m_exposureValue);
//            }
////        }
//
//        // Brightness
////        NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_BR_MODE, "Manual");
////        long minv = NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_BR_VALUE);
////        long maxv = NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_BR_VALUE);
////        long val = minv + (long) (((double) (maxv - minv)) * m_brightness);
//        NIVision.IMAQdxSetAttributeF64(m_sessionId, ATTR_BR_VALUE, m_brightness);
//
//        NIVision.IMAQdxSetAttributeString(m_sessionId, ATTR_ST_MODE, "Manual");
//        NIVision.IMAQdxSetAttributeI64(m_sessionId, ATTR_ST_VALUE, (long)m_saturation);
//
//        // Restart acquisition
//        if (wasActive)
//        {
//            startCapture();
//        }
//    }
//
//    public synchronized void setVideoMode(int mode)
//    {
//        m_mode = mode;
//        m_needSettingsUpdate = true;
//    }
//
//    public synchronized int getVideoMode()
//    {
//        return m_mode; 
//    }
//    
//    *//** Set the brightness, as a percentage (0-1). *//*
//    public synchronized void setBrightness(double brightness)
//    {
////        if (brightness > 1)
////        {
////            m_brightness = 1;
////        }
////        else if (brightness < 0)
////        {
////            m_brightness = 0;
////        }
////        else
////        {
//            m_brightness = brightness;
//        //}
//        m_needSettingsUpdate = true;
//    }
//
//    *//** Get the brightness, as a percentage (0-1). *//*
//    public synchronized double getBrightness()
//    {
//        return m_brightness;
//    }
//    
//    *//** Set the white balance to auto. *//*
//    public synchronized void setWhiteBalanceAuto()
//    {
//        m_whiteBalance = "auto";
//        m_whiteBalanceValue = -1;
//        m_needSettingsUpdate = true;
//    }
//
//    *//** Set the white balance to hold current. *//*
//    public synchronized void setWhiteBalanceHoldCurrent()
//    {
//        m_whiteBalance = "manual";
//        m_whiteBalanceValue = -1;
//        m_needSettingsUpdate = true;
//    }
//
//    *//** Set the white balance to manual, with specified color temperature. *//*
//    public synchronized void setWhiteBalanceManual(int value)
//    {
//        m_whiteBalance = "manual";
//        m_whiteBalanceValue = value;
//        m_needSettingsUpdate = true;
//    }
//    
//    public synchronized int getWhiteBalanceManual()
//    {
//        return m_whiteBalanceValue; 
//    }
//
//    *//** Set the exposure to auto aperature. *//*
//    public synchronized void setExposureAuto()
//    {
//        m_exposure = "auto";
//        m_exposureValue = -1;
//        m_needSettingsUpdate = true;
//    }
//
//    *//** Set the exposure to hold current. *//*
//    public synchronized void setExposureHoldCurrent()
//    {
//        m_exposure = "manual";
//        m_exposureValue = -1;
//        m_needSettingsUpdate = true;
//    }
//
//    *//** Set the exposure to manual, as a percentage (0-1). *//*
//    public synchronized void setExposureManual(double value)
//    {
//        m_exposure = "manual";
////        if (value > 1)
////        {
////            m_exposureValue = 1;
////        }
////        else if (value < 0)
////        {
////            m_exposureValue = 0;
////        }
////        else
////        {
//            m_exposureValue = value;
//        //}
//        m_needSettingsUpdate = true;
//    }
//    
//    public synchronized double getExposureManual()
//    {
//        return m_exposureValue; 
//    }
//
//    public synchronized void setSaturation(double sat)
//    {
//        m_saturation = sat;
//    }
//    
//    public synchronized double getSaturation()
//    {
//        return m_saturation;
//    }
//    
//    public synchronized void getImage(Image image)
//    {
//        if (m_needSettingsUpdate)
//        {
//            m_needSettingsUpdate = false;
//            updateSettings();
//        }
//
//        NIVision.IMAQdxGrab(m_sessionId, image, 1);
//    }
//    
//    public synchronized void printRanges()
//    {
//        SmartDashboard.putString("Exp", NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_EX_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeI64(       m_sessionId, ATTR_EX_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_EX_VALUE));
//        
//        SmartDashboard.putString("Bri", NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_BR_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeI64(       m_sessionId, ATTR_BR_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_BR_VALUE));
//        
//        SmartDashboard.putString("Sat", NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_ST_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeI64(       m_sessionId, ATTR_ST_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_ST_VALUE));
//        
//        SmartDashboard.putString("WhB", NIVision.IMAQdxGetAttributeMinimumI64(m_sessionId, ATTR_WB_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeI64(       m_sessionId, ATTR_WB_VALUE) + " " +
//                                        NIVision.IMAQdxGetAttributeMaximumI64(m_sessionId, ATTR_WB_VALUE));
//
//    }
//}
