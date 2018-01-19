package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveRocketLeague;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class TankSubsystem extends Subsystem {

    public TalonSRX RightFront;
    public TalonSRX RightBack;
    public TalonSRX LeftFront;
    public TalonSRX LeftBack;

    public TankSubsystem(){
        this.RightFront = new TalonSRX(10);
        this.RightBack = new TalonSRX(11);
        this.LeftFront= new TalonSRX(12);
        this.LeftBack = new TalonSRX(13);

        RightBack.set(ControlMode.Follower, 10);
        LeftBack.set(ControlMode.Follower, 12);
        double rampRate = .5;

        LeftFront.configOpenloopRamp(rampRate, 0);
        RightFront.configOpenloopRamp(rampRate, 0);
        LeftBack.configOpenloopRamp(rampRate, 0);
        RightBack.configOpenloopRamp(rampRate, 0);

        LeftFront.setInverted(true);
        LeftBack.setInverted(true);
        // TODO Auto-generated constructor stub
    }

    public void setSpeedRight(double speed)
    {
       RightFront.set(ControlMode.Current, speed);
    }

    public void setSpeedLeft(double speed)
    {
        LeftFront.set(ControlMode.Current, speed);
    }




    @Override
    protected void initDefaultCommand()
    {
        //TODO make this change with a button,
        //for now it can just change by changing what you call here
        setDefaultCommand(new DriveRocketLeague());

    }

}
