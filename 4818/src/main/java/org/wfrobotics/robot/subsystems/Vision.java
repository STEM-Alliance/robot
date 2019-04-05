package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.SmartVision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.CircularBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Raspberry Pi running modified Chicken Vision */
public final class Vision extends EnhancedSubsystem
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
    private double timestampLast = Double.MAX_VALUE;
    private boolean elevatorCamera;

    public Vision()
    {
        elevatorCamera = true;  // Intentionally opposite to force update
        setCamera(!elevatorCamera);
        setModeTape();
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new SmartVision());
    }
    
    public void cacheSensors(boolean isDisabled)
    {
        if (!isDisabled)
        {
            final double timestampNow = _getLastTimestamp();

            modeWhenCaching = modeActual;
            
            switch (modeWhenCaching)
            {
                case DETECT_TAPE:
                    cachedIO.inView = _getTapeInView();
                    cachedIO.error = (cachedIO.inView) ? _getTapeYaw() : 0.0;
                    cachedIO.size = _getTapeRadius();
                    break;
                case DETECT_CARGO:
                    cachedIO.inView = _getCargoInView();
                    cachedIO.error = (cachedIO.inView) ? _getCargoYaw() : 0.0;
                    cachedIO.size = 0.0;
                    break;
                default:
                    cachedIO = new CachedIO();
                    break;
            }
            cachedIO.connected = timestampNow - timestampLast > 0.000001;  // Changed more than 1ms?
            
            timestampLast = timestampNow;
        }
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Vision Camera Elevator", getCamera());
        SmartDashboard.putBoolean("Vision Mode Tape", getModeTape());
        SmartDashboard.putBoolean("Vision Connected", getConnected());
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

    public void setModeCargo()
    {
        setMode(CAMERA_MODE.DETECT_CARGO);
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

    /** Based on if timestamps are updating */
    public boolean getConnected()
    {
        return cachedIO.connected;
    }

    public double getError()
    {
        return cachedIO.error;
    }

    public boolean getInView()
    {
        return cachedIO.inView;
    }

    public boolean getModeCargo()
    {
        return modeWhenCaching == CAMERA_MODE.DETECT_CARGO;
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
                case DETECT_CARGO:
                    chickenVision.getEntry("Cargo").setBoolean(true);
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
    private boolean _getCargoInView() {   return chickenVision.getEntry("cargoDetected").getBoolean(false);   }
    private double _getTapeYaw()      {   return chickenVision.getEntry("tapeYaw").getDouble(0.0);    }
    private double _getCargoYaw()     {   return chickenVision.getEntry("cargoYaw").getDouble(0.0);   }
    private double _getTapeRadius()   {   return chickenVision.getEntry("tapeRadius").getDouble(0.0);    }
    public boolean _getDriveCamera() {   return chickenVision.getEntry("Driver").getBoolean(false);  }
    public boolean _getTapeCamera()  {   return chickenVision.getEntry("Tape").getBoolean(false);    }
    public boolean _getCargoCamera() {   return chickenVision.getEntry("Cargo").getBoolean(false);   }
    public double _getLastTimestamp(){   return chickenVision.getEntry("VideoTimestamp").getDouble(0.0); }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        // TODO - Can we detect that both cameras are plugged in and okay?

        return report;
    }

    private class CachedIO
    {
        public boolean connected = false;
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
