package org.wfrobotics.reuse.utilities;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class DashboardView
{
    public DashboardView()
    {
        new Thread(() -> {
            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
            camera.setResolution(480, 360);
        }).start();
    }
}
