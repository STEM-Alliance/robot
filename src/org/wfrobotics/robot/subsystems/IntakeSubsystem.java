package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.CyborgIntake;
import org.wfrobotics.robot.config.RobotMap;

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
    public static final double kDistanceHasCube = 7; // (in centmeters)

    private final double kTimeUntilNextSolenoidHorizontal = .5;

    protected final RobotState state = RobotState.getInstance();
    private final TalonSRX masterRight;
    private final TalonSRX followerLeft;
    private final DoubleSolenoid horizontalIntake;
    private final DoubleSolenoid vertIntake;
    private final SharpDistance uSensor;

    private boolean lastHorizontalState;
    private double lastHorizontalTime;
    private double lastDistance;

    public IntakeSubsystem()
    {
        masterRight = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_RIGHT);
        followerLeft = TalonSRXFactory.makeFollowerTalon(RobotMap.CAN_INTAKE_LEFT, RobotMap.CAN_INTAKE_RIGHT);
        masterRight.setNeutralMode(NeutralMode.Brake);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(false);
        followerLeft.setInverted(true);

        horizontalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);
        vertIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_VERTICAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_VERTICAL_REVERSE);

        uSensor = new SharpDistance(RobotMap.ANALOG_INTAKE_DISTANCE);

        // Force defined states
        lastHorizontalTime = Timer.getFPGATimestamp() - kTimeUntilNextSolenoidHorizontal * 1.01;
        lastHorizontalState = true;
        setHorizontal(!lastHorizontalState);

        lastDistance = uSensor.getDistance();
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new CyborgIntake());
    }

    public boolean getHorizontal()
    {
        return lastHorizontalState;
    }

    public void onBackgroundUpdate()
    {
        double currentDistance = uSensor.getDistance();
        double centimeters = (currentDistance + lastDistance) / 2;  // TODO Get a better average? Circular buffer? Raw is jumpy when accelerating drivetrain
        lastDistance = currentDistance;

        SmartDashboard.putNumber("Cube", centimeters);
        state.updateIntakeSensor(centimeters);
        state.updateHasCube(uSensor.getDistance() < kDistanceHasCube);
    }

    public void setMotor(double percentageOutward)
    {
        masterRight.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean setHorizontal(boolean extended)
    {
        boolean delayedEnough = Timer.getFPGATimestamp() - lastHorizontalTime > kTimeUntilNextSolenoidHorizontal;
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

    public void setVert(boolean high)
    {
        SmartDashboard.putBoolean("Wrist Requested", high);

        vertIntake.set(high ? Value.kForward : Value.kReverse);
    }
}

