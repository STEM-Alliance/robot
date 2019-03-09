package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.IntakeDefault;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends EnhancedSubsystem
{
    private static class SingletonHolder
    {
        static Intake instance = new Intake();
    }

    public static Intake getInstance()
    {
        return SingletonHolder.instance;
    }
    
	DoubleSolenoid exampleDouble = new DoubleSolenoid(0,1, 0);
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public void Forward()  
    {
		SmartDashboard.putBoolean("command", true);
		SmartDashboard.putString("Current Command Intake", this.getCurrentCommandName());
		exampleDouble.set(DoubleSolenoid.Value.kForward);
    }
	
	public void Reverse() 
    {
		SmartDashboard.putBoolean("command", false);
		SmartDashboard.putString("Current Command Intake", this.getCurrentCommandName());
		exampleDouble.set(DoubleSolenoid.Value.kReverse);
    }
	
	public void Off()  
    {
		SmartDashboard.putBoolean("command", true);
		exampleDouble.set(DoubleSolenoid.Value.kOff);
    }
	
	public void set(boolean open)
    {
		SmartDashboard.putBoolean("command", open);
		DoubleSolenoid.Value state = (open) ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse;
		exampleDouble.set(state);
    }
	
    public void initDefaultCommand()
    {
        setDefaultCommand(new IntakeDefault());
    }
	
    @Override
	public void reportState() 
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public TestReport runFunctionalTest() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cacheSensors(boolean isDisabled)
	{
	}
}