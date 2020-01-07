package frc.robot.reuse.vision;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/** Raspberry Pi running modified Chicken Vision */
public final class Vision extends SubsystemBase
{
    static class SingletonHolder
    {
        static Vision instance = new Vision();
    }

    public static Vision getInstance()
    {
        return SingletonHolder.instance;
    }

    enum CAMERA_MODE
    {
        DETECT_TAPE,
        DETECT_CARGO,
        STREAM,
        UNKNOWN;
    }

    private final NetworkTableInstance netInstance = NetworkTableInstance.getDefault();
    private final NetworkTable chickenVision = netInstance.getTable("ChickenVision");
    private CachedIO cachedIO = new CachedIO();

    /** Will return this from getters - CachedIO was sample when in this mode */
    private CAMERA_MODE modeWhenCaching = CAMERA_MODE.UNKNOWN;
    /** Will store this - Commands set the mode, then cachedIO is incorrect for the new mode */
    private CAMERA_MODE modeActual = CAMERA_MODE.UNKNOWN;
    private boolean elevatorCamera;

    public Vision()
    {
        elevatorCamera = true;  // Intentionally opposite to force update
        setCamera(!elevatorCamera);
        setModeTape();
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
                    cachedIO.error = (cachedIO.inView) ? _getTapeYaw() : 0.0;
                    cachedIO.size = _getTapeRadius();
                    break;
                default:
                    cachedIO = new CachedIO();
                    break;
            }
        }
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Vision Camera Elevator", getCamera());
        SmartDashboard.putBoolean("Vision Mode Tape", getModeTape());
        SmartDashboard.putBoolean("Vision In view", getInView());
        SmartDashboard.putNumber("Vision Angle", -getError());  // Invert so Smartdash radial reads correctly
        SmartDashboard.putNumber("Vision Size", getSize());
    }

    /** True: Elevator, False: Chassis */
    public void setCamera(boolean elevatorCamera)
    {
        if (this.elevatorCamera != elevatorCamera)
        {
            final int index = (elevatorCamera) ? 0 : 1;
            chickenVision.getEntry("CameraIndex").setNumber(index);
            this.elevatorCamera = elevatorCamera; 
        }
        
    }

    public void setModeTape()
    {
        setMode(CAMERA_MODE.DETECT_TAPE);
    }

    public void setModeStream()
    {
        setMode(CAMERA_MODE.STREAM);
    }

    /** True: Elevator, False: Chassis */
    public boolean getCamera()
    {
        return elevatorCamera;
    }

    public double getError()
    {
        return cachedIO.error;
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

    public double getSize()
    {
        return cachedIO.size;
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
    private double _getTapeRadius()   {   return chickenVision.getEntry("tapeRadius").getDouble(0.0);    }
    public boolean _getDriveCamera() {   return chickenVision.getEntry("Driver").getBoolean(false);  }
    public boolean _getTapeCamera()  {   return chickenVision.getEntry("Tape").getBoolean(false);    }
    public boolean _getCargoCamera() {   return chickenVision.getEntry("Cargo").getBoolean(false);   }
    public double _getLastTimestamp(){   return chickenVision.getEntry("VideoTimestamp").getDouble(0.0); }


    private class CachedIO
    {
        public boolean inView = false;
        public double error = 0.0;
        public double size = 0.0;
    }

    protected boolean driverVision, 
                    tapeVision, 
                    cargoVision, 
                    cargoSeen, 
                    tapeSeen;
    protected NetworkTableEntry tapeDetected, 
                              cargoDetected,    
                              tapeYaw, 
                              cargoYaw,
                              videoTimestamp;
}
