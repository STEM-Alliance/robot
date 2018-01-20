package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveArcade3DPro;
import org.wfrobotics.prototype.commands.DriveRocketLeague;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/** TODO: comment what this Subsystem does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleSubsystem2 extends Subsystem
{
    // TODO create any hardware this Subsystem needs to control the robot
    
    TalonSRX motor;
    
    public ExampleSubsystem2()
    
    {
        // TODO setup any hardware this Subsystem will need
        motor = new TalonSRX(10);
    }

    protected void initDefaultCommand()
    {
        // TODO pick which Command runs whenever buttons are not being pressed
        setDefaultCommand(new DriveArcade3DPro());
    }

    // TODO rename me (right click -> refactor -> rename)
    public void setSpeed(double x, double y)  // TODO pass any parameters needed to each method
    {
        // TODO create any methods needed to control this Subsystem, called by Commands
        motor.set(ControlMode.PercentOutput, x);
    }
}