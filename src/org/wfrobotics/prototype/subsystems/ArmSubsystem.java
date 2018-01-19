package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.ArmPivotBase;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmSubsystem extends Subsystem {

    public DigitalInput CWSwitch;
    public DigitalInput CCWSwitch;
    public DigitalInput UpSwitch;
    public DigitalInput DownSwitch;
    public TalonSRX baseMotor;
    public TalonSRX elbowMotor1; //possibly change addresses
    public TalonSRX elbowMotor2;

    boolean elbow;
    boolean rotate;
    double up; //up is positive
    double clockwise; //clockwise is positive

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public ArmSubsystem()
    {

        this.baseMotor = new TalonSRX(14);
        this.elbowMotor1 = new TalonSRX(15); //possibly change addresses
        this.elbowMotor2 = new TalonSRX(16);
        this.CWSwitch = new DigitalInput(0);
        this.CCWSwitch = new DigitalInput(1);
        this.DownSwitch = new DigitalInput(2);
        this.UpSwitch = new DigitalInput(3);


        baseMotor.setNeutralMode(NeutralMode.Brake);
        elbowMotor1.setNeutralMode(NeutralMode.Brake);
        elbowMotor2.setNeutralMode(NeutralMode.Brake);

        elbowMotor2.set(ControlMode.Follower, 15);

        elbowMotor1.setInverted(true);
    }

    public Boolean isAtTop()
    {
        return UpSwitch.get();
    }

    public Boolean isAtBottom()
    {
        return DownSwitch.get();

    }
    public Boolean isAtBaseCW()
    {
        return CWSwitch.get();

    }
    public Boolean isAtBaseCCW()
    {
        return CCWSwitch.get();
    }


        public void initDefaultCommand()
        {

            setDefaultCommand(new ArmPivotBase());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

}

