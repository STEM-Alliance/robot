package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.LiftOpenLoop;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift extends EnhancedSubsystem
{
    private static class SingletonHolder
    {
        static Lift instance = new Lift();
    }

    public static Lift getInstance()
    {
        return SingletonHolder.instance;
    }
    
	final double kTicksPerDegree = 4096.0 / 360.0;
	double desiredSpeed;
	double desiredDegrees;
	public VictorSPX master;
	public VictorSPX follower;
    double radius = 2;
    
    public Lift()
    {
    	int kWristVelocityMax = 3500;
    	int kWristVelocityCruise = (int) (kWristVelocityMax * .9);
        int kWristAcceleration = (int) (kWristVelocityMax * .9);
        
        ClosedLoopConfig kClosedLoop = new ClosedLoopConfig("prototype", new MasterConfig[] 
        {
            new MasterConfig(14, false, false),
        },  new Gains[] {
            new Gains("Motion Magic", 0, 1.0, 0.004, 0.0, 1023.0 / kWristVelocityMax, 0, kWristVelocityCruise, kWristAcceleration),
        });

    //  master = TalonFactory.makeClosedLoopVictor(kClosedLoop).get(18);  
    //  master.setSelectedSensorPosition(0, 0, 100);
        master = new VictorSPX(19);
        follower = TalonFactory.makeFollowerVictor(20, master);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LiftOpenLoop());
    }

    public void setSpeed(double speed)
    {
    	master.set(ControlMode.PercentOutput, speed);
    	this.desiredSpeed = speed;
    }

    public void setHeight(double height) 
    {
    	master.set(ControlMode.MotionMagic, positionToNative(heightToDegrees(height)));
    	this.desiredDegrees = heightToDegrees(height);
    }

    public void reportState()
    {
    	SmartDashboard.putNumber("Position Native", getPositionNative());
    	SmartDashboard.putNumber("Position Degrees",positionToNative(desiredDegrees));
    	SmartDashboard.putNumber("Speed Native", getSpeedNative());
    	SmartDashboard.putNumber("Speed Set", desiredSpeed);
    	SmartDashboard.putNumber("Degrees Set", desiredDegrees);
    	SmartDashboard.putString("Current Command", this.getCurrentCommandName());
    }
    
    public double getSpeedNative()
    {
    	return master.getSelectedSensorVelocity(0);
    }
    	
    public double getPositionNative()
    {
    	return master.getSelectedSensorPosition(0);
    }
    
    public double getPosition()
    {
    	return nativeToPosition(getPositionNative());
    }
    
    public double getHeight()
    {
    	return nativeToPosition(getPositionNative())*Math.PI*radius/180;
    }
    
    public double positionToNative(double degrees)
    {
    	return degrees * kTicksPerDegree;
    }
    
    public double heightToDegrees(double height) 
    {
    	return height*180/(Math.PI*radius);
    }
    
    public double degreesToHeight(double degrees) 
    {
    	return degrees*Math.PI*radius/180;
    }
    
    public double nativeToPosition(double ticks) 
    {
    	return ticks / kTicksPerDegree;
    }

	@Override
    public TestReport runFunctionalTest() 
    {
		return null;
	}

	@Override
    public void cacheSensors(boolean isDisabled) 
    {		
	}
}