package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Subsystem
{
    private class ShooterMotor
    {
        private final String name;
        private final double invert;  // Account for inverted motor when asking for speed (Inverted: 1, Not-inverted: -1)

        private CANTalon motor;
        private double lastSpeedCommanded;

        public ShooterMotor(String name, int address, double p, double i, double d, double f, double ramp, boolean invert)
        {
            this.name = name;
            this.invert = (invert) ? -1:1;

            motor = new CANTalon(address);

            motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            motor.changeControlMode(TalonControlMode.Speed);
            motor.setPID(p, i, d, f, 0, ramp, 0);  // f is 100% of total feed forward / native counts per 100ms @ 4000rpm
            motor.reverseSensor(invert);
            motor.setInverted(invert);
            
            lastSpeedCommanded = -.000000001;  // Unlikely default last value such that we do something in the first call to set()
        }
        
        public double get()
        {
            return invert * motor.getSpeed();
        }
        
        public void set(double rpm)
        {
            motor.set(rpm);
            
            if (rpm != lastSpeedCommanded)  // Not enabling/disabling each iteration in case this impacts performance
            {
                if (rpm != 0)
                {
                    motor.enable();
                }
                else
                {
                    motor.disable();
                }
            }
            lastSpeedCommanded = rpm;
        }

        public boolean atSpeed(double tolerance)
        {
            boolean reached = Math.abs(speedDesired - get()) <= tolerance;

            SmartDashboard.putNumber("SpeedDesired", speedDesired);
            SmartDashboard.putNumber(name + "SpeedDiff",speedDesired - get());
            SmartDashboard.putBoolean(name + "SpeedReached", reached);

            return reached;
        }
    }

    //private final ShooterMotor motorT;
    private final ShooterMotor motorB;

    private double speedDesired;
    private int counts = 0;

    public Shooter()
    {
        //motorT = new ShooterMotor("Top", RobotMap.SHOOTER_MOTOR_SRX, .11, .0001, .00, .0, .01, true);
        motorB = new ShooterMotor("Bottom", RobotMap.FEEDER_MOTOR_SRX, .4, .0001, .00, 0, .01, false);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Rev(Rev.MODE.OFF));
    }

    public void forceOff()
    {
        //motorT.set(0);
        motorB.set(0);
    }
    
    /**
     * Test to see if the top motor has REVed to a certain speed then starts to spin the bottom flywheel to get it up to speed
     * @param rpm to set flywheels to
     * @param tolerance tolerance in rpm used to turn on wheels in sequence
     * @return if the desired speed is reached
     */
    public boolean topThenBottom(double rpm, double tolerance)
    {
        boolean atSpeed = false;
        printDash();

        speedDesired = rpm;

        if (rpm != 0)
        {
            //motorT.set(rpm);
            motorB.set(rpm);
            
            //if(motorT.atSpeed(tolerance))
            {

                if(motorB.atSpeed(tolerance))
                {
                    atSpeed = true;
                }
            }
        }
        else
        {
            motorB.set(0);

            // Bottom flywheel is below a threshold before turning off the top one
            //if (motorB.atSpeed(100))
            {
                //motorT.set(0);
            }
        }


        if(atSpeed)
        {
            counts++;
        }
        else
        {
            counts = 0;
        }
        
        return atSpeed && counts > 50;
    }

    public boolean inTolerance(double tolerance)
    {
        return /*motorT.atSpeed(tolerance) &&*/ motorB.atSpeed((tolerance*2));
    }

    public void printDash()
    {
        SmartDashboard.putNumber("ShooterSpeedDesired", speedDesired);
        //SmartDashboard.putNumber("FlywheelTopSpeedActual", motorT.get());
        SmartDashboard.putNumber("FlywheelBottomSpeedActual", motorB.get());
    }

    public boolean isRunning()
    {
        return Math.abs(speedDesired) > 10;
    }
}
