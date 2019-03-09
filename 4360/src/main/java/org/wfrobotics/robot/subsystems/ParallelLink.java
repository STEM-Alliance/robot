package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.reuse.utilities.Testable.TestReport;
import org.wfrobotics.robot.commands.IntakeDefault;
import org.wfrobotics.robot.commands.LiftOpenLoop;
import org.wfrobotics.robot.commands.StopParallelLink;
import org.wfrobotics.robot.subsystems.IntakeMotor.SingletonHolder;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ParallelLink extends EnhancedSubsystem 
{
    static class SingletonHolder
    {
        static ParallelLink instance = new ParallelLink();
    }

    public static ParallelLink getInstance()
    {
        return SingletonHolder.instance;
    }
    
	public VictorSPX LinkMotor1;
	
    public ParallelLink()
    {
    	LinkMotor1 = new VictorSPX(16);
    }
    
	public void setlink(double speed) 
	{
    	LinkMotor1.set(ControlMode.PercentOutput, speed);
	}
	
	protected void initDefaultCommand()
	{
	    setDefaultCommand(new StopParallelLink());
	}

	@Override
	public void reportState() 
	{
	   	SmartDashboard.putString("Current Command Link", this.getCurrentCommandName());
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