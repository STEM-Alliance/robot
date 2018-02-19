package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem extends Subsystem implements BackgroundUpdate
{
    private final double kTimeoutHorizontal;
    private final double kTimeoutVertical;

    private final RobotState state = RobotState.getInstance();

    private final TalonSRX masterRight;
    private final TalonSRX followerLeft;
    private final DoubleSolenoid horizontalIntake;
    private final DoubleSolenoid verticalIntake;
    private final SharpDistance distanceSensor;

    private boolean lastHorizontalState;
    private boolean lastVerticalState;
    private double lastHorizontalTime;
    private double lastVerticalTime;
    private double lastDistance;
    private double distanceCurrent;

    public IntakeSubsystem(RobotConfig config)
    {
        masterRight = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_RIGHT);
        followerLeft = TalonSRXFactory.makeFollowerTalon(RobotMap.CAN_INTAKE_LEFT, RobotMap.CAN_INTAKE_RIGHT);
        masterRight.setNeutralMode(NeutralMode.Brake);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(config.INTAKE_INVERT_RIGHT);
        followerLeft.setInverted(config.INTAKE_INVERT_LEFT);

        horizontalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);
        verticalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_VERTICAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_VERTICAL_REVERSE);

        distanceSensor = new SharpDistance(config.INTAKE_SENSOR);

        kTimeoutHorizontal = config.INTAKE_TIMEOUT_WRIST;
        kTimeoutVertical = config.INTAKE_TIMEOUT_WRIST;

        // Force defined states
        lastHorizontalTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastHorizontalState = true;
        setHorizontal(!lastHorizontalState);

        lastVerticalTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastVerticalState = false;
        setVertical(lastVerticalState);

        lastDistance = distanceSensor.getDistance();
    }

    // ----------------------------------------- Interfaces ----------------------------------------

    public void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void onBackgroundUpdate()
    {
        final double rawCentimeters = distanceSensor.getDistance();
        final double centimeters = (rawCentimeters + lastDistance) / 2;  // TODO Get a better average? Circular buffer? Raw is jumpy when accelerating drivetrain
        lastDistance = rawCentimeters;
        distanceCurrent = centimeters;
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

    public void setMotor(double percentageOutward)
    {
        masterRight.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean setHorizontal(boolean extendedOpen)
    {
        final boolean delayedEnough = Timer.getFPGATimestamp() - lastHorizontalTime > kTimeoutHorizontal;
        final boolean different = extendedOpen != lastHorizontalState;
        boolean stateChanged = false;

        SmartDashboard.putBoolean("Jaws Requested", extendedOpen);

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

        SmartDashboard.putBoolean("Wrist Requested", contractedUpward);

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
        SmartDashboard.putNumber("Cube", distanceCurrent);
        state.updateIntakeSensor(distanceCurrent);
    }

    // ----------------------------------------- Private ------------------------------------------
}

