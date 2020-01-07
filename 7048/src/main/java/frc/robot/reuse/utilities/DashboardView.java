package frc.robot.reuse.utilities;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

public class DashboardView
{
    private DashboardView() { }

    public static void startCustomCamera(int width, int height, int fps)
    {
        startStream(width, height, fps);
    }

    public static void startPerformanceCamera()
    {
        startStream(416, 240, 15);
    }

    public static void startQualityCamera()
    {
        startStream(416, 240, 20);
    }

    private static void startStream(int width, int height, int fps)
    {
        new Thread(() -> {
            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
            camera.setResolution(width, height);
            camera.setFPS(fps);
            
        }).start();  // Retries to connect, then exits thread on success

    }
}