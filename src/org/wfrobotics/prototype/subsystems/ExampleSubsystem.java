package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveArcade;
import org.wfrobotics.prototype.commands.DriveArcade3DPro;
import org.wfrobotics.prototype.commands.DriveRocketLeague;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/** TODO: comment what this Subsystem does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleSubsystem extends Subsystem
{
    // TODO create any hardware this Subsystem needs to control the robot
    
    TalonSRX left;
    TalonSRX left2;
    TalonSRX right;
    TalonSRX right2;
    
    public ExampleSubsystem()
    
    {
        // TODO setup any hardware this Subsystem will need
        left = new TalonSRX(10);
        left2 = new TalonSRX(11);
        right = new TalonSRX(12);
        right2 = new TalonSRX(13);
        right.setInverted(true);
        right2.setInverted(true);
        left2.set(ControlMode.Follower, 10);
        right2.set(ControlMode.Follower, 12);
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
        left.set(ControlMode.PercentOutput, y+x); // x-1 go left, left=back, right=forward
        right.set(ControlMode.PercentOutput, y-x); // invert x
    }
}