package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Elevate;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem
{
    // TODO List of present heights
    // TODO Preset heights in configuration file

    public TalonSRX LiftMotor;
    public DigitalInput BottomSensor;
    public DigitalInput TopSensor;
    public int encoderValue;

    public LiftSubsystem()
    {
        final int kTimeoutMs = 10;

        // TODO Use Talon factory. If not position control, at least makeTalon()
        LiftMotor = new TalonSRX(RobotMap.CAN_LIFT);
        BottomSensor = new DigitalInput(RobotMap.DIGITAL_LIFT_LIMIT_BOTTOM);
        TopSensor = new DigitalInput(RobotMap.DIGITAL_LIFT_LIMIT_TOP);

        //LiftMotor.setNeutralMode(NeutralMode.Brake);
        LiftMotor.setInverted(true);

        LiftMotor.getSelectedSensorPosition(10);

        // LiftMotor.setSelectedSensorPosition(absolutePosition, 0, kTimeoutMs);

        // TODO Figure out what settings are ideal

        LiftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, kTimeoutMs);
        LiftMotor.setSensorPhase(true);

        // TODO Poofs used brake mode on drive postion control. Why didn't ours work?

        // TODO Double check we aren't setting irrelevant frame types super as fast - we need the bandwidth for lift/drive important frames

        // TODO Can we get away with follower mode or do we need to two that try to adjust if we slip a geartooth? Ask mechanical what to do.
        LiftMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 5, kTimeoutMs);
        LiftMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 5, kTimeoutMs);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 1 , kTimeoutMs);
        //LiftMotor.setControlFramePeriod(ControlFrame.Control_3_General, kTimeoutMs);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 5, kTimeoutMs);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, 5, kTimeoutMs);

        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches
        //LiftMotor.setNeutralMode(NeutralMode.Brake);

        LiftMotor.configAllowableClosedloopError(5, 0, kTimeoutMs);
        LiftMotor.config_kF(0, 0.0, kTimeoutMs);
        LiftMotor.config_kP(0, 20, kTimeoutMs);//20
        LiftMotor.config_kI(0, 0, kTimeoutMs);
        LiftMotor.config_kD(0, 125, kTimeoutMs);
        LiftMotor.configNeutralDeadband(.05, kTimeoutMs);
        LiftMotor.config_IntegralZone(0, 1, kTimeoutMs);
    }

    // TODO There's a "state pattern" that can help us if the rules for going to/from each state gets too complex
    public void goToPosition(double destination)
    {
        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches
        LiftMotor.setNeutralMode(NeutralMode.Coast);

        update();
        LiftMotor.set(ControlMode.Position, (destination * 4096));
    }

    // TODO Lift needs to hold position by default
    //      Beast mode - Can (or should we even) automatically go the height based on if we have a cube or some IO to tell our intended preset to score on?
    public void initDefaultCommand()
    {
        setDefaultCommand(new Elevate(0));
    }

    public void setSpeed (double speed)
    {
        LiftMotor.setNeutralMode(NeutralMode.Brake);
        double output = speed;

        if(speed == 0)
        {
            LiftMotor.setNeutralMode(NeutralMode.Coast);
        }

        if(isAtBottom() && speed < 0 || isAtTop() && speed > 0)
        {
            output = 0;
        }

        update();
        LiftMotor.set(ControlMode.PercentOutput, output);
    }

    private void update()
    {
        zeroPositionIfNeeded();
        SmartDashboard.putNumber("LiftEncoder", Robot.liftSubsystem.getEncoder());
    }

    public void zeroPositionIfNeeded()
    {
        if(Robot.liftSubsystem.isAtBottom())
        {
            Robot.liftSubsystem.LiftMotor.setSelectedSensorPosition(0, 0, 0);
        }
    }

    public boolean isAtTop()
    {
        return TopSensor.get();
    }

    public boolean isAtBottom()
    {
        return BottomSensor.get();
    }

    public int getEncoder()
    {
        return LiftMotor.getSelectedSensorPosition(0);
    }

    // TODO Report fommatted state to RobotState. Not the height, but instead something like what the Robot can do. Ex: isSafeToExhaustScale

    // TODO Automatically zero whenever we pass by that sensor(s)

    // TODO What's the most automatic way we can score on the first layer of cube (on scale/switch) vs the second? What are the easiest xbox controls for that?

    // TODO Beast mode - The fastest lift possible probably dynamically changes it's control strategy to get to it's destination fastest
    //                   This might mean a more aggressive PID (profile) on the way down
    //                   Could go as far as using both closed and open loop control modes
}
