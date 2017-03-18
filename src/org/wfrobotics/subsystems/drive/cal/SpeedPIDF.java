package org.wfrobotics.subsystems.drive.cal;

import java.util.List;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SpeedPIDF 
{
    private static final double MAG_ENC_NU_PER_REV = 4096;
    private static final double MAG_ENC_SAMPLE_TIME = .10;
    private enum CAL_PARAM {OUTPUT, RPM, ERROR};
    
    private List<CANTalon> motors;
    
    public SpeedPIDF(int[] addresses)
    {
        for(int index = 0; index < addresses.length; index++)
        {
            CANTalon t = new CANTalon(addresses[index]);
            t.setP(0);
            t.setI(0);
            t.setD(0);
            t.setF(0);
            t.changeControlMode(TalonControlMode.Speed);
            t.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            t.configNominalOutputVoltage(0, 0);
            t.configPeakOutputVoltage(12, -12);
            
            motors.add(t);
        }
    }
    
    public void set(double input)
    {
        //SmartDashboard.putNumber("CalOutput", value)
    }
    
    public boolean inPhase(double input)
    {
        boolean result = true;
        
        for(int index = 0; index < motors.size(); index++)
        {
            if (getParam(index, CAL_PARAM.OUTPUT) <= 0 ||
                getParam(index, CAL_PARAM.ERROR) <= 0 ||
                input <= 0
                )
            {
                result = false;
            }
        }
        
        return result;
    }
    
    private double getAverage(CAL_PARAM type)
    {
        double sum = 0;
        
        for(int index = 0; index < motors.size(); index++)
        {
            sum += getParam(index, type);
        }
        
        return sum / motors.size();
    }
    
    private double getParam(int motor, CAL_PARAM type)
    {
        double result;
        
        switch (type)
        {
            case OUTPUT:
                result = motors.get(motor).getOutputVoltage()/motors.get(motor).getBusVoltage();
                break;
            case RPM:
                result = motors.get(motor).getSpeed();
            case ERROR:
                result = motors.get(motor).getClosedLoopError();
            default:
                result = 0;
                break;
        }
        
        return result;
    }
}
