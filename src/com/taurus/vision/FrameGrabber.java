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
    private int brightness;
    private int cameraQuality = 50;
    
    private final Image frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
    private Camera camera;    
    
    public FrameGrabber(String cameraName, int brightness)
    {
        Init(cameraName, brightness);
    }
    
    public void Init(String cameraName, int brightness)
    {
        NAME = cameraName;
        this.brightness = brightness;
    }
    
    /**
     * Continuously capture frames
     */
    @Override
    public void run()
    {
        double TimeStart;
        
        SmartDashboard.putString("FrameGetter", "Running");
        
        setup();        
        SmartDashboard.putString("FrameGetter", "Capturing");
        
        while (running)
        {            
            try
            {
                TimeStart = Timer.getFPGATimestamp();
                
                if (camera.isCapturing())
                {
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

        camera.setBrightness(brightness);        
        camera.printRanges();
        
        updateSettings(true);
        
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
    private void updateSettings(boolean forceUpdate)
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
        
        int whiteBalance = Preferences.getInstance().getInt("WhiteBalance", 4500);
        if(camera.getWhiteBalanceManual() != whiteBalance || forceUpdate)
        {
            camera.setWhiteBalanceManual(whiteBalance);
        }
        
        double exposure = Preferences.getInstance().getDouble("Exposure", 1);
        if(camera.getExposureManual() != exposure || forceUpdate)
        {
            camera.setExposureManual(exposure);
        }
        
        double brightness = Preferences.getInstance().getDouble("Brightness", 1);
        if(camera.getBrightness() != brightness || forceUpdate)
        {
            camera.setBrightness(brightness);
        }
        
        double sat = Preferences.getInstance().getDouble("Saturation", .5);
        if(camera.getSaturation() != sat || forceUpdate)
        {
            camera.setSaturation(sat);
        }
    }
    
    public boolean fixCamera(boolean set)
    {
        boolean done = false;
        
        if(camera != null && camera.isCapturing())
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
            done = true;
        }
        return done;
    }
}
