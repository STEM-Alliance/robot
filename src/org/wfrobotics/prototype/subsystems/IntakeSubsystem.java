package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.BoxIn;
import org.wfrobotics.prototype.commands.BoxOut;
import org.wfrobotics.prototype.commands.ExampleForwardCommand;
import org.wfrobotics.prototype.commands.Wait;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
     CANTalon RightIntake = new CANTalon(1);
     CANTalon LeftIntake = new CANTalon(2);

    public void initDefaultCommand() {
        setDefaultCommand(new Wait());
        
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    public void rightSetSpeed(double rpm)
    {
       RightIntake.set(ControlMode.PercentOutput, rpm);
    }
    public void leftSetSpeed(double rpm)
    {
       LeftIntake.set(ControlMode.PercentOutput, rpm);
    }
    public static Xbox controller = new Xbox(0);

    public static Button X = ButtonFactory.makeButton(controller, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new BoxIn());
    public static Button Y = ButtonFactory.makeButton(controller, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new BoxOut());
}

