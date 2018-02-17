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
    /*
     * when the block is further away from the sensor the motors are at speed x
     * when it comes closer to the sensor we want to ramp down the motors to speed 2/3x
     * when distances is equal to 0, the motor speed is set to 0
     */
    public static RobotConfig config;

    protected final RobotState state = RobotState.getInstance();

    private final TalonSRX masterRight;
    private final TalonSRX followerLeft;
    private final DoubleSolenoid horizontalIntake;
    private final DoubleSolenoid vertIntake;
    private final SharpDistance uSensor;

    private boolean lastHorizontalState;
    private boolean lastVertState;

    private double lastHorizontalTime;
    private double lastVertTime;

    private double lastDistance;

    public IntakeSubsystem(RobotConfig Config)
    {
        config = Config;

        masterRight = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_RIGHT);
        followerLeft = TalonSRXFactory.makeFollowerTalon(RobotMap.CAN_INTAKE_LEFT, RobotMap.CAN_INTAKE_RIGHT);
        masterRight.setNeutralMode(NeutralMode.Brake);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(config.INTAKE_INVERT_RIGHT);
        followerLeft.setInverted(config.INTAKE_INVERT_LEFT);

        horizontalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);
        vertIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_VERTICAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_VERTICAL_REVERSE);

        uSensor = new SharpDistance(config.INTAKE_SENSOR);

        // Force defined states
        lastHorizontalTime = Timer.getFPGATimestamp() - config.INTAKE_WRIST_TIMEOUT_LENTH * 1.01;
        lastHorizontalState = true;
        setHorizontal(!lastHorizontalState);

        lastVertTime = Timer.getFPGATimestamp() - config.INTAKE_WRIST_TIMEOUT_LENTH * 1.01;
        lastVertState = true;
        setVert(!lastVertState);

        lastDistance = uSensor.getDistance();
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void onBackgroundUpdate()
    {
        double currentDistance = uSensor.getDistance();
        double centimeters = (currentDistance + lastDistance) / 2;  // TODO Get a better average? Circular buffer? Raw is jumpy when accelerating drivetrain
        lastDistance = currentDistance;

        SmartDashboard.putNumber("Cube", centimeters);
        state.updateIntakeSensor(centimeters);
        state.updateHasCube(uSensor.getDistance() < config.INTAKE_DISTANCE_TO_CUBE);
    }

    public void setMotor(double percentageOutward)
    {
        masterRight.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean getHorizontal()
    {
        return lastHorizontalState;
    }

    public boolean setHorizontal(boolean extended)
    {
        boolean delayedEnough = Timer.getFPGATimestamp() - lastHorizontalTime > config.INTAKE_WRIST_TIMEOUT_LENTH;
        boolean different = extended != lastHorizontalState;
        boolean stateChanged = false;

        SmartDashboard.putBoolean("Jaws Requested", extended);

        if (delayedEnough && different)
        {
            horizontalIntake.set(extended ? Value.kForward : Value.kReverse);
            lastHorizontalTime = Timer.getFPGATimestamp();
            lastHorizontalState = extended;
            stateChanged = true;
        }
        return stateChanged;
    }

    public boolean getVertical()
    {
        return lastVertState;
    }

    public boolean setVert(boolean extended)
    {
        boolean delayedEnough = Timer.getFPGATimestamp() - lastVertTime > config.INTAKE_WRIST_TIMEOUT_LENTH;
        boolean different = extended != lastVertState;
        boolean stateChanged = false;

        SmartDashboard.putBoolean("Wrist Requested", extended);

        if (delayedEnough && different)
        {
            vertIntake.set(extended ? Value.kForward : Value.kReverse);
            lastVertTime = Timer.getFPGATimestamp();
            lastVertState = extended;
            stateChanged = true;
        }
        return stateChanged;
    }
}

