package frc.robot.config;

public class VisionConfig
{
    /** Field of view, aka how many degrees from right to left */
    public final double kCameraWidthDegrees;

    public VisionConfig(double cameraWidthDegrees)
    {
        kCameraWidthDegrees = cameraWidthDegrees;
    }

    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("CameraWidth: " + kCameraWidthDegrees + " Degrees\n");

        return sb.toString();
    }
}
