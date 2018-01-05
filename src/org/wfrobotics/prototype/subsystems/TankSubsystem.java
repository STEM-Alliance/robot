package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveRocketLeague;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class TankSubsystem extends Subsystem {

    public CANTalon RightFront;
    public CANTalon RightBack;
    public CANTalon LeftFront;
    public CANTalon LeftBack;

    public TankSubsystem(){
        this.RightFront = new CANTalon(10);
        this.RightBack = new CANTalon(11);
        this.LeftFront= new CANTalon(12);
        this.LeftBack = new CANTalon(13);

        RightBack.changeControlMode(CANTalon.TalonControlMode.Follower);
        RightBack.set(10);

        LeftBack.changeControlMode(CANTalon.TalonControlMode.Follower);
        LeftBack.set(12);
        double rampRate = 40;

        LeftFront.setVoltageRampRate(rampRate);
        RightFront.setVoltageRampRate(rampRate);
        LeftBack.setVoltageRampRate(rampRate);
        RightBack.setVoltageRampRate(rampRate);

        LeftFront.setInverted(true);
        LeftBack.setInverted(true);
        // TODO Auto-generated constructor stub
    }

    public void setSpeedRight(double speed)
    {
       RightFront.set(speed);
    }

    public void setSpeedLeft(double speed)
    {
        LeftFront.set(speed);
    }




    @Override
    protected void initDefaultCommand()
    {
        //TODO make this change with a button,
        //for now it can just change by changing what you call here
        setDefaultCommand(new DriveRocketLeague());

    }

}
