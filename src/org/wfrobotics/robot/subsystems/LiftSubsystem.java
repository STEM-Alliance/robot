package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.commands.LiftManual;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem implements BackgroundUpdate
{
    private final double kTicksPerRev = 4096;
    private final double kRevsPerInch = 1;  // TODO

    // TODO List of present heights
    // TODO Preset heights in configuration file

    private TalonSRX[] motors = new TalonSRX[2];

    private ControlMode desiredMode;
    private double desiredSetpoint;

    public double todoRemoveLast;

    public LiftSubsystem()
    {
        final int kTimeout = 10;
        final int kSlot = 0;
        final int[] addresses = {11, 10};
        final boolean[] inverted = {true, true};
        final boolean[] sensorPhase = {false, true};
        final LimitSwitchNormal[][] sensorNormally = {{LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed}, {LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed}};
        //final double kP = 0.1 * 1023.0 / 189.7 * 2 * 2 * 2;  // DRL also works if max velocity multiplied by .75 instead of .8
        final double kP = .1 * 1023.0 / 75 * 2 * 1.25;
        final double kI = 0;
        final double kD = kP * 10;
        final double kF = 1023.0/4950.0;
        final int kMaxVelocity = (int) (4950 * .8);
        final int kAcceleration = kMaxVelocity;
        // TODO Make into config file?
        // TODO Use talon software limit switches

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = TalonSRXFactory.makeConstAccelControlTalon(addresses[index], kP, kI, kD, kF, kSlot, kMaxVelocity, kAcceleration);
            if (index == 0)
            {
                motors[index].configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, sensorNormally[index][0], kTimeout);
                motors[index].configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, sensorNormally[index][1], kTimeout);
            }
            else
            {
                motors[index].configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, sensorNormally[index][0], addresses[0], kTimeout);
                motors[index].configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, sensorNormally[index][1], addresses[0], kTimeout);
            }
            motors[index].overrideLimitSwitchesEnable(true);
            motors[index].set(ControlMode.PercentOutput, 0);
            motors[index].setInverted(inverted[index]);
            motors[index].setSensorPhase(sensorPhase[index]);
            motors[index].setNeutralMode(NeutralMode.Brake);
            motors[index].setSelectedSensorPosition(0, kSlot, kTimeout);
        }
        desiredMode = ControlMode.PercentOutput;
        desiredSetpoint = 0;

        todoRemoveLast = Timer.getFPGATimestamp();
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftManual());
    }

    public synchronized void onBackgroundUpdate()
    {
        double todoRemoveNow = Timer.getFPGATimestamp();

        // TODO Zero if below bottom limit switch?

        set(desiredMode, desiredSetpoint);

        debug();
        SmartDashboard.putNumber("Background Period", (todoRemoveNow - todoRemoveLast) * 1000);
        todoRemoveLast = todoRemoveNow;
    }

    public synchronized void goToHeightInit(double heightInches)
    {
        for (int index = 0; index < motors.length; index++)
        {
            motors[index].setSelectedSensorPosition(0, 0, 0);
        }
        desiredMode = ControlMode.MotionMagic;
        desiredSetpoint = inchesToTicks(heightInches);
        //set(desiredMode, desiredSetpoint);
    }

    public synchronized void goToSpeedInit(double percent)
    {
        desiredMode = ControlMode.PercentOutput;
        desiredSetpoint = percent;
        //set(desiredMode, desiredSetpoint);
    }

    private void set(ControlMode mode, double val)
    {
        for (int index = 0; index < motors.length; index++)
        {
            motors[index].set(mode, val);
        }
    }

    private double inchesToTicks(double inches)
    {
        return inches * kRevsPerInch * kTicksPerRev;
    }

    private void debug()
    {
        TalonSRX motor = motors[0];
        double position = motor.getSelectedSensorPosition(0);

        SmartDashboard.putNumber("Position", position);
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putNumber("Trajectory Position", motor.getActiveTrajectoryPosition());
        SmartDashboard.putNumber("Trajectory Velocity", motor.getActiveTrajectoryVelocity());
        SmartDashboard.putNumber("TargetPosition", desiredSetpoint);
        SmartDashboard.putNumber("Height", inchesToTicks(position));
        SmartDashboard.putNumber("Error", motors[0].getClosedLoopError(0));
        SmartDashboard.putNumber("Error2", motors[1].getClosedLoopError(0));
        SmartDashboard.putBoolean("Forward Limit", motor.getSensorCollection().isFwdLimitSwitchClosed());
        SmartDashboard.putBoolean("Reverse Limit", motor.getSensorCollection().isRevLimitSwitchClosed());
    }











    //    public void goToPosition(double destination)
    //    {
    //        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches
    //        //        liftMotorL.setNeutralMode(NeutralMode.Coast);
    //
    //        update();
    //        //        liftMotorL.set(ControlMode.Position, (destination * 4096));
    //    }
    //
    //    public void setSpeed (double speed)
    //    {
    //        if(speed == 0)
    //        {
    //            //            liftMotorL.setNeutralMode(NeutralMode.Coast);
    //        }
    //
    //        if(isAtBottom() && speed < 0 || isAtTop() && speed > 0)
    //        {
    //        }
    //
    //        update();
    //        //        liftMotorL.set(ControlMode.PercentOutput, output);
    //    }
    //
    //    private void update()
    //    {
    //        zeroPositionIfNeeded();
    //        SmartDashboard.putNumber("LiftEncoder", Robot.liftSubsystem.getEncoder());
    //    }
    //
    //    public void zeroPositionIfNeeded()
    //    {
    //        if(Robot.liftSubsystem.isAtBottom())
    //        {
    //            //            Robot.liftSubsystem.liftMotorL.setSelectedSensorPosition(0, 0, 0);
    //        }
    //    }
    //
    //    public boolean isAtTop()
    //    {
    //        return sensorTop.get();
    //    }
    //
    //    public boolean isAtBottom()
    //    {
    //        return sensorBot.get();
    //    }
    //
    //    public int getEncoder()
    //    {
    //        //        return liftMotorL.getSelectedSensorPosition(0);
    //        return 0;
    //    }

    // TODO Report fommatted state to RobotState. Not the height, but instead something like what the Robot can do. Ex: isSafeToExhaustScale

    // TODO Automatically zero whenever we pass by that sensor(s)

    // TODO What's the most automatic way we can score on the first layer of cube (on scale/switch) vs the second? What are the easiest xbox controls for that?

    // TODO Beast mode - The fastest lift possible probably dynamically changes it's control strategy to get to it's destination fastest
    //                   This might mean a more aggressive PID (profile) on the way down
    //                   Could go as far as using both closed and open loop control modes
}
