package org.wfrobotics.prototype.subsystems;

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
        left = new TalonSRX(0);
        left2 = new TalonSRX(1);
        right = new TalonSRX(2);
        right2 = new TalonSRX(6);
        right.setInverted(true);
        right2.setInverted(true);
        left2.set(ControlMode.Follower, 0);
        right2.set(ControlMode.Follower, 2);
    }

    protected void initDefaultCommand()
    {
        // TODO pick which Command runs whenever buttons are not being pressed
        setDefaultCommand(new DriveRocketLeague());
    }

    // TODO rename me (right click -> refactor -> rename)
    public void setSpeed(double x, double y)  // TODO pass any parameters needed to each method
    {
        // TODO create any methods needed to control this Subsystem, called by Commands
        left.set(ControlMode.PercentOutput, y-x); // -1x, 0y = 1=left, -1=right; -1x, 1y = 1=left, .5=right; 0x, 1y = 1=left, 1=right;
        right.set(ControlMode.PercentOutput, y+x); // x = -left, right; y = left, right;
    }
}