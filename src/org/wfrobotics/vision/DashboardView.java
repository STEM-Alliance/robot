package org.wfrobotics.vision;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class DashboardView
{
    public DashboardView()
    {
        new Thread(() -> {
            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
            camera.setResolution(720, 480);
            
            
        }).start();
    }
}
