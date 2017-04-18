package org.wfrobotics.reuse.hardware;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.SD540;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;

public class HerdMotor implements SpeedController {

    public enum TYPES
    {
        TALON_SRX_CAN,
        TALON_SRX_PWM,
        TALON_SR_PWM,
        VICTOR_PWM,
        VICTOR_SP_PWM,
        SD540_PWM,
        SPARK_PWM;
        
        public SpeedController get(int port)
        {
            switch(this)
            {
                case TALON_SRX_PWM:          return new TalonSRX(port);
                case TALON_SR_PWM:           return new Talon(port);
                case VICTOR_PWM:             return new Victor(port);
                case VICTOR_SP_PWM:          return new VictorSP(port);
                case SD540_PWM:              return new SD540(port);
                case SPARK_PWM:              return new Spark(port);
                case TALON_SRX_CAN: default: return new CANTalon(port);
            }
        }
    }
    
    
    
    @Override
    public void pidWrite(double output)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public double get()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void set(double speed)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInverted(boolean isInverted)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getInverted()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disable()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopMotor()
    {
        // TODO Auto-generated method stub
        
    }

}
