package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.reuse.utilities.Testable.TestReport;
import org.wfrobotics.robot.commands.IntakeDefault;
import org.wfrobotics.robot.commands.LiftOpenLoop;
import org.wfrobotics.robot.commands.StopIntake;
import org.wfrobotics.robot.subsystems.IntakeMotor.SingletonHolder;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class IntakeMotor extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static IntakeMotor instance = new IntakeMotor();
    }

    public static IntakeMotor getInstance()
    {
        return SingletonHolder.instance;
    }
    
    public VictorSPX motor1;
    public VictorSPX motor2;
   
    public IntakeMotor()
    {
    	motor1 = new VictorSPX(17);
    	motor2 = TalonFactory.makeFollowerVictor(18, motor1);
    	motor2.setInverted(true);
    }
    
	public void setspeed(double speed) 
	{
    	motor1.set(ControlMode.PercentOutput, speed);
    }
    
    protected void initDefaultCommand()
    {
    	setDefaultCommand(new StopIntake());
    }

	@Override
	public void reportState() 
	{
	   	SmartDashboard.putString("Current Command Intake", this.getCurrentCommandName());
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