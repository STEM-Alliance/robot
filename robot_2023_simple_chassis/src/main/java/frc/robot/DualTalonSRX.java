package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

import javax.lang.model.util.ElementScanner14;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class DualTalonSRX implements MotorController {
    private TalonSRX _motor1;
    private TalonSRX _motor2;
    private TalonSRXControlMode _defaultMode;
    
    public DualTalonSRX(int motor1, int motor2, TalonSRXControlMode defaultMode, boolean invert)
    {
        /*
         * Currently it is about 25,500 quad counts to move 10 feet.
         */
        _motor1 = new TalonSRX(motor1);
        _motor2 = new TalonSRX(motor2);
        _defaultMode = defaultMode;
        setInverted(invert);
        SensorCollection sensors1 = _motor1.getSensorCollection();
        sensors1.setQuadraturePosition(0, 1000);
        SensorCollection sensors2 = _motor2.getSensorCollection();
        sensors2.setQuadraturePosition(0, 1000);
        
    }

    public void updateControlMode(TalonSRXControlMode mode)
    {
        _defaultMode = mode;
    }

    public void set(double speed)
    {
        _motor1.set(_defaultMode, speed);
        _motor2.set(_defaultMode, speed);
    }
    public double get()
    {
        return _motor1.getMotorOutputPercent();
    }

    public void setInverted(boolean isInverted)
    {
        _motor1.setInverted(isInverted);
        _motor2.setInverted(isInverted);
    }
    
    public boolean getInverted() 
    {
        return _motor1.getInverted();
    }
    
    public void disable()
    {
        stopMotor();
    }
    
    public void stopMotor()
    {
        _motor1.set(_defaultMode, 0);
        _motor2.set(_defaultMode, 0);
    }

    public SensorCollection getSensors(int motor)
    {
        if (motor == 1)
            return _motor1.getSensorCollection();
        return _motor2.getSensorCollection();
    }

    // public int getPosition(int motor)
    // {
    //     if (motor == 1)
    //         SensorCollection sensors = _motor1.getSensorCollection();
    //     else 
    //         SensorCollection sensors = _motor2.getSensorCollection();
            
    //     return _savedPosition - sensors.getQuadraturePosition();
    // }
}
