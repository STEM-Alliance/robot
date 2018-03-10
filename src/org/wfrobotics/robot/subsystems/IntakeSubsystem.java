package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.CircularBuffer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem extends Subsystem implements BackgroundUpdate
{
    private final double bufferSize = 3;
    private final double kDistanceMaxIn;
    private final double kTimeoutHorizontal;
    private final double kTimeoutVertical;

    private final RobotState state = RobotState.getInstance();

    private final TalonSRX masterRight;
    private final TalonSRX followerLeft;

    private final TalonSRX intakeLift;

    private final DoubleSolenoid horizontalIntake;
    private final DoubleSolenoid verticalIntake;
    private final SharpDistance distanceSensorR;
    private CircularBuffer buffer;

    private boolean lastHorizontalState;
    private boolean lastVerticalState;
    private double lastHorizontalTime;
    private double lastVerticalTime;

    public IntakeSubsystem(RobotConfig config)
    {
        masterRight = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_RIGHT);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1000, 10);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1000, 10);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 1000, 10);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 1000, 10);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 1000, 10);
        masterRight.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 1000, 10);
        masterRight.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(config.INTAKE_INVERT_RIGHT);

        followerLeft = TalonSRXFactory.makeFollowerTalon(RobotMap.CAN_INTAKE_LEFT, RobotMap.CAN_INTAKE_RIGHT);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        followerLeft.setInverted(config.INTAKE_INVERT_LEFT);

        intakeLift = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_LIFT);
        intakeLift.set(ControlMode.PercentOutput, 0);
        intakeLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
        intakeLift.configForwardSoftLimitThreshold(4600, 10);
        intakeLift.configReverseSoftLimitThreshold(0, 10);
        intakeLift.configForwardSoftLimitEnable(true, 10);
        intakeLift.configReverseSoftLimitEnable(true, 10);

        horizontalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);
        verticalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_VERTICAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_VERTICAL_REVERSE);

        distanceSensorR = new SharpDistance(config.INTAKE_SENSOR_R);
        buffer = new CircularBuffer((int) bufferSize);
        for (int index = 0; index < bufferSize; index++)
        {
            buffer.addFirst(9999);  // Filled with zeros by default, which is bad because it's a valid value
        }

        kDistanceMaxIn = config.INTAKE_DISTANCE_TO_CUBE;
        kTimeoutHorizontal = config.INTAKE_TIMEOUT_WRIST;
        kTimeoutVertical = config.INTAKE_TIMEOUT_WRIST;

        // Force defined states
        lastHorizontalTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastHorizontalState = true;
        setHorizontal(!lastHorizontalState);

        lastVerticalTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastVerticalState = false;
        setVertical(lastVerticalState);
    }

    // ----------------------------------------- Interfaces ----------------------------------------

    public void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void onBackgroundUpdate()
    {
        buffer.addFirst(distanceSensorR.getDistance() - kDistanceMaxIn);
    }

    // ----------------------------------------- Public -------------------------------------------

    public boolean getHorizontal()
    {
        return lastHorizontalState;
    }

    public boolean getVertical()
    {
        return lastVerticalState;
    }

    public void setIntake(double percentageOutward)
    {
        masterRight.set(ControlMode.PercentOutput, percentageOutward);
    }

    public void setIntakeLift(double percentageUp)
    {
        //        if(!((percentageUp > 0) && intakeLiftAtTop()) || (!((percentageUp < 0) && intakeLiftAtBottom())))
        {
            intakeLift.set(ControlMode.PercentOutput, percentageUp);
        }
    }
    public void ZeroIntakeEncoder()
    {
        intakeLift.setSelectedSensorPosition(0, 0, 10);
    }
    //    public boolean intakeLiftAtTop()
    //    {
    //        return(intakeLift.getSelectedSensorPosition(0) == RobotMap.INTAKE_LIFT_FORWARD_LIMIT);
    //    }
    //    public boolean intakeLiftAtBottom()
    //    {
    //        return(intakeLift.getSelectedSensorPosition(0) == RobotMap.INTAKE_LIFT_REVERSE_LIMIT);
    //    }

    public boolean setHorizontal(boolean extendedOpen)
    {
        final boolean delayedEnough = Timer.getFPGATimestamp() - lastHorizontalTime > kTimeoutHorizontal;
        final boolean different = extendedOpen != lastHorizontalState;
        boolean stateChanged = false;

        if (delayedEnough && different)
        {
            horizontalIntake.set(extendedOpen ? Value.kForward : Value.kReverse);
            lastHorizontalTime = Timer.getFPGATimestamp();
            lastHorizontalState = extendedOpen;
            stateChanged = true;
        }
        return stateChanged;
    }

    public boolean setVertical(boolean contractedUpward)
    {
        final boolean delayedEnough = Timer.getFPGATimestamp() - lastVerticalTime > kTimeoutVertical;
        final boolean different = contractedUpward != lastVerticalState;
        boolean stateChanged = false;

        if (delayedEnough && different)
        {
            verticalIntake.set(contractedUpward ? Value.kReverse : Value.kForward);
            lastVerticalTime = Timer.getFPGATimestamp();
            lastVerticalState = contractedUpward;
            stateChanged = true;
        }
        return stateChanged;
    }

    public void reportState()
    {
        double sum = 0;
        for (int index = 0; index < bufferSize; index++)
        {
            sum += buffer.get(index);
        }
        SmartDashboard.putNumber("Cube R", sum / bufferSize);
        SmartDashboard.putNumber("Intake Lift Encoder", intakeLift.getSelectedSensorPosition(0));
        state.updateIntakeSensor(sum / bufferSize);
    }

    // ----------------------------------------- Private ------------------------------------------
}

