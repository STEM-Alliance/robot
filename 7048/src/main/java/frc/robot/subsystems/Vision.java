package frc.robot.subsystems;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.visionUpdate;

/** Raspberry Pi running modified Chicken Vision */
public final class Vision extends SubsystemBase
{
    enum CAMERA_MODE
    {
        DETECT_TAPE,
        STREAM,
        UNKNOWN;
    }

    private final NetworkTableInstance netInstance = NetworkTableInstance.getDefault();
    private final NetworkTable chickenVision = netInstance.getTable("ChickenVision");
    public CachedIO cachedIO = new CachedIO();

    /** Will return this from getters - CachedIO was sample when in this mode */
    private CAMERA_MODE modeWhenCaching = CAMERA_MODE.UNKNOWN;
    /** Will store this - Commands set the mode, then cachedIO is incorrect for the new mode */
    private CAMERA_MODE modeActual = CAMERA_MODE.UNKNOWN;

    public Vision()
    {
        setMode(CAMERA_MODE.DETECT_TAPE);
        visionUpdate();
        setDefaultCommand(new visionUpdate(this));
    }
    public void visionUpdate()
    {
        cacheSensors(false);
        reportState();
    }
    
    public void cacheSensors(boolean isDisabled)
    {
        if (!isDisabled)
        {
            modeWhenCaching = modeActual;
            switch (modeWhenCaching)
            {
                case DETECT_TAPE:
                    cachedIO.inView = _getTapeInView();
                    cachedIO.errorYaw = (cachedIO.inView) ? _getTapeYaw() : 0.0;
                    cachedIO.errorPitch = (cachedIO.inView) ? _getTapePitch() : 0.0;
                    cachedIO.distance = (cachedIO.inView) ? _tapeDistance() : 0.0;

                    break;
                default:
                    cachedIO = new CachedIO();
                    break;
            }
        }
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Vision Mode Tape", getModeTape());
        SmartDashboard.putBoolean("Vision In view", getInView());
        SmartDashboard.putNumber("Vision Angle Yaw", -getYawError());  // Invert so Smartdash radial reads correctly
        SmartDashboard.putNumber("Vision Angle Pitch", -getPitchError());  // Invert so Smartdash radial reads correctly

    }

    public void setModeStream()
    {
        setMode(CAMERA_MODE.STREAM);
    }

    public double getYawError()
    {
        return cachedIO.errorYaw;
    }
    public double getPitchError()
    {
        return cachedIO.errorYaw;
    }
    public double getDistance()
    {
        return cachedIO.distance;
    }

    public boolean getInView()
    {
        return cachedIO.inView;
    }

    public boolean getModeTape()
    {
        return modeWhenCaching == CAMERA_MODE.DETECT_TAPE;
    }

    public boolean getModeStream()
    {
        return modeWhenCaching == CAMERA_MODE.STREAM;
    }

    private void setMode(CAMERA_MODE mode)
    {
        if (this.modeWhenCaching != mode)
        {
            switch (mode)
            {
                case DETECT_TAPE:
                    chickenVision.getEntry("Tape").setBoolean(true);
                    break;
                case STREAM:
                    chickenVision.getEntry("Driver").setBoolean(true);
                    break;
                default:
                    chickenVision.getEntry("Tape").setBoolean(true);
                    break;
            }
            this.modeActual = mode;
        }
    }

    private boolean _getTapeInView()  {   return chickenVision.getEntry("tapeDetected").getBoolean(false);    }
    private double _getTapeYaw()      {   return chickenVision.getEntry("tapeYaw").getDouble(0.0);    }
    private double _getTapePitch()      {   return chickenVision.getEntry("tapePitch").getDouble(0.0);    }
    public boolean _getDriveCamera() {   return chickenVision.getEntry("Driver").getBoolean(false);  }
    public boolean _getTapeCamera()  {   return chickenVision.getEntry("Tape").getBoolean(false);    }
    public double _getLastTimestamp(){   return chickenVision.getEntry("VideoTimestamp").getDouble(0.0); }
    public double _tapeDistance(){   return chickenVision.getEntry("tapeDistance").getDouble(0.0); }



    private class CachedIO
    {
        public boolean inView = false;
        public double errorPitch = 0.0;
        public double errorYaw = 0.0;
        public double distance = 0.0;
    }
}
