package com.taurus.vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FrameGrabber implements Runnable
{
    private String NAME;
    private volatile boolean running;
    private int cameraQuality = 70;
    
    private boolean setupNeeded = false;
    
    private final Image frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
    private Camera camera;    
    
    public FrameGrabber(String cameraName)
    {
        Init(cameraName);
    }
    
    public void Init(String cameraName)
    {
        NAME = cameraName;
        setupNeeded = true;
    }
    
    /**
     * Continuously capture frames
     */
    @Override
    public void run()
    {
        double TimeStart;
        
        while(true)
        {
            if(setupNeeded)
            {
                SmartDashboard.putString("FrameGetter", "Setting up");
                
                setup();  
                setupNeeded = false;
            }
            
            if(running)
                SmartDashboard.putString("FrameGetter", "Capturing");
            
            while (running)
            {
                 
                try
                {
                    TimeStart = Timer.getFPGATimestamp();
                    
                    if (camera.isCapturing())
                    {
                        updateSettings(false, Vision.getInstance().getCurrentCamera());
                        
                        camera.getImage(frame);
                    }
                    
                    SmartDashboard.putNumber("FrameGetter Task Length", Timer.getFPGATimestamp() - TimeStart);
                }
                catch (Exception e)
                {
                    SmartDashboard.putString("FrameGetter error", "Processing: " + e.getMessage());
                }
            }
            
            teardown();
            SmartDashboard.putString("FrameGetter", "Not capturing");
        }
    }
    
    /**
     * Request run() terminate
     */
    public void terminate() 
    {
        running = false;
    }
    
    /**
     * Get the latest frame received from the camera
     * @return latest frame
     */
    public Image getFrame()
    {
        return frame;
    }
    
    private void setup()
    {
        camera = new Camera(NAME);
        camera.openCamera();
        camera.startCapture();
        
        updateSettings(true, Vision.getInstance().getCurrentCamera());
        
        running = true;
    }
    
    private void teardown()
    {
        if (camera.isCapturing())
        {
            camera.stopCapture();
        }
        if (camera.isOpen())
        {
            camera.closeCamera();
        }
    }
    
    /**
     * If any of the Preferences change, send the latest to the camera object
     */
    private void updateSettings(boolean forceUpdate, Vision.CAMERAS currentCamera)
    {
        int cameraQual = Preferences.getInstance().getInt("CameraQuality", 50);
        if(cameraQual != cameraQuality || forceUpdate)
        {
            CameraServer.getInstance().setQuality(cameraQual);
            cameraQuality = cameraQual;
        }
        
        int videoMode = Preferences.getInstance().getInt("VIDEO_MODE", 93);
        if(camera.getVideoMode() != videoMode || forceUpdate)
        {
            camera.setVideoMode(videoMode);
        }
        
        
        int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4400);
        if(camera.getWhiteBalanceManual() != whiteBalance || forceUpdate)
        {
            camera.setWhiteBalanceManual(whiteBalance);
        }
        

        if(currentCamera == Vision.CAMERAS.MAIN)
        {
            double exposure = Preferences.getInstance().getDouble("Exposure", 5);
            if(camera.getExposureManual() != exposure || forceUpdate)
            {
                camera.setExposureManual(exposure);
            }
            
            double brightness = Preferences.getInstance().getDouble("Brightness", 60);
            if(camera.getBrightness() != brightness || forceUpdate)
            {
                camera.setBrightness(brightness);
            }
            
            double sat = Preferences.getInstance().getDouble("Saturation", 200);
            if(camera.getSaturation() != sat || forceUpdate)
            {
                camera.setSaturation(sat);
            }
        }
        else
        {

            double exposure = Preferences.getInstance().getDouble("Exposure2", 5.1);
            if(camera.getExposureManual() != exposure || forceUpdate)
            {
                camera.setExposureManual(exposure);
            }
            
            double brightness = Preferences.getInstance().getDouble("Brightness2", 100);
            if(camera.getBrightness() != brightness || forceUpdate)
            {
                camera.setBrightness(brightness);
            }
            
            double sat = Preferences.getInstance().getDouble("Saturation2", 150);
            if(camera.getSaturation() != sat || forceUpdate)
            {
                camera.setSaturation(sat);
            }
        }
        
        camera.printRanges();
    }
    
    public boolean fixCamera(boolean set)
    {
        boolean done = false;
        
        if(camera != null && camera.isCapturing())
        {
            if(Vision.getInstance().getCurrentCamera() == Vision.CAMERAS.MAIN)
            {
                if(set)
                {
                    camera.setBrightness(Preferences.getInstance().getDouble("Brightness", 1) + 1);
                    Preferences.getInstance().putDouble("Brightness", Preferences.getInstance().getDouble("Brightness", 1) + 1);
                }
                else
                {
                    camera.setBrightness(Preferences.getInstance().getDouble("Brightness", 1) - 1);
                    Preferences.getInstance().putDouble("Brightness", Preferences.getInstance().getDouble("Brightness", 1) - 1);
                }
            }
            else if(Vision.getInstance().getCurrentCamera() == Vision.CAMERAS.BACK)
            {
                if(set)
                {
                    camera.setBrightness(Preferences.getInstance().getDouble("Brightness2", 1) + 1);
                    Preferences.getInstance().putDouble("Brightness2", Preferences.getInstance().getDouble("Brightness2", 1) + 1);
                }
                else
                {
                    camera.setBrightness(Preferences.getInstance().getDouble("Brightness2", 1) - 1);
                    Preferences.getInstance().putDouble("Brightness2", Preferences.getInstance().getDouble("Brightness2", 1) - 1);
                }
            }
            done = true;
        }
        return done;
    }
}
