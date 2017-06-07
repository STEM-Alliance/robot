package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.NetworkTableCamera;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraServos extends NetworkTableCamera {
    
    Servo servoX,
          servoY;
    
    
    public CameraServos()
    {
        super("Target", 0, 1);
        
        servoX = new Servo(7);
        servoY = new Servo(8);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void run()
    {
        
        getUpdatedData();
        
        TargetCount = data.size();
        SmartDashboard.putNumber("GearTargets", TargetCount);
        
        if(TargetCount == 1)
        {
            TargetData validData = data.get(0);
            //validData.
            
        }
        servoX.set(xCalc());
        xCalc();
        yCalc();
        
    }
    public double xCalc()
    {
        
        
        return 0.0;  
    }
    public double yCalc()
    {
        
        
        return 0.0;
    }
    
    public void setX(double val)
    {
        servoX.set(val);
    }
    public void setY(double val)
    {
        servoY.set(val);
    }

}
