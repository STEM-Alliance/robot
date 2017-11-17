package org.wfrobotics.robot.subsystems;

import java.util.logging.Level;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.commands.Lift;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Lifter extends Subsystem
{
    public static enum POSITION {TOP, TRANSPORT, BOTTOM}

    HerdLogger log = new HerdLogger(Lifter.class);
    //    private final double TOP_LIMIT = .0775;
    //    private final double BOTTOM_LIMIT = 50;
    private double SETPOINT_TOP = -520;
    private double SETPOINT_TRANSPORT = -650;
    private double SETPOINT_BOTTOM = -730;
    private final double P = 5;
    private final double I = 0.0005;
    private final double D = 0;
    private final double F = 0;

    private final CANTalon motor;
    private final DigitalInput senseGear;

    public Lifter(boolean reverseSensor)
    {
        motor = CANTalonFactory.makeTalon(RobotMap.LIFTER_MOTOR);
        motor.setFeedbackDevice(FeedbackDevice.AnalogPot);
        motor.changeControlMode(TalonControlMode.Position);
        //        motor.setForwardSoftLimit(TOP_LIMIT);
        //        motor.setReverseSoftLimit(BOTTOM_LIMIT);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(12, -12);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.setPID(P, I, D, F, 0, 120, 0);
        motor.setAllowableClosedLoopErr(0);

        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableLimitSwitch(true, true);
        motor.reverseOutput(true);
        motor.setInverted(false);
        motor.reverseSensor(true);

        motor.enableBrakeMode(false);
        motor.enableControl();
        motor.set(motor.getPosition());  // Recommended for closed loop control

        senseGear = new DigitalInput(RobotMap.LIFTER_SENSOR_GEAR);
        log.setLevel(Level.FINE);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new Lift());
    }

    public void reset()
    {
        motor.disableControl();
        motor.setP(Preferences.getInstance().getDouble("LifterPID_P", P));
        motor.setI(Preferences.getInstance().getDouble("LifterPID_I", I));
        motor.setD(Preferences.getInstance().getDouble("LifterPID_D", D));
        SETPOINT_TOP = Preferences.getInstance().getDouble("LifterTop", SETPOINT_TOP);
        SETPOINT_TRANSPORT = Preferences.getInstance().getDouble("LifterTransport", SETPOINT_TRANSPORT);
        SETPOINT_BOTTOM = Preferences.getInstance().getDouble("LifterBot", SETPOINT_BOTTOM);
        //      motor.setForwardSoftLimit(Preferences.getInstance().getDouble("LifterTopL", TOP_LIMIT));
        //      motor.setReverseSoftLimit(Preferences.getInstance().getDouble("LifterBottomL", BOTTOM_LIMIT));
        motor.enableControl();
        motor.set(motor.getPosition());  // Recommended for closed loop control

        log.debug("LifterAngle", motor.getPosition());
    }

    public boolean hasGear()
    {
        boolean hasGear = !senseGear.get();

        log.debug("LifterGear", hasGear);

        return hasGear;
    }

    public void set(POSITION position)
    {
        double desired;

        switch(position)
        {
            case TOP:
                desired = SETPOINT_TOP;
                break;
            case TRANSPORT:
                desired = SETPOINT_TRANSPORT;
                break;
            case BOTTOM:
            default:
                desired = SETPOINT_BOTTOM;
                break;
        }

        log.debug("LifterDesired", desired);
        printDash();

        if (Math.abs(desired - motor.getPosition()) < 5)
        {
            motor.ClearIaccum();  // Prevent I wind-up when close enough
        }

        motor.set(desired);
    }

    private void printDash()
    {
        double angle = motor.getPosition();
        String position;

        if (Math.abs(angle - SETPOINT_TOP) < 10 || motor.isRevLimitSwitchClosed())
        {
            position = "Top";
        }
        else if (Math.abs(angle - SETPOINT_TRANSPORT) < 10)
        {
            position = "Transport";
        }
        else if (Math.abs(angle - SETPOINT_BOTTOM) < 10 || motor.isFwdLimitSwitchClosed())
        {
            position = "Bottom";
        }
        else
        {
            position = "Moving";
        }

        log.debug("LifterState", position);
        log.debug("LifterAngle", angle);
        log.debug("LifterError", motor.getClosedLoopError());
        log.debug("LifterIAccum", motor.GetIaccum());
    }

    public boolean atBottom()
    {
        return Math.abs(motor.getPosition() - SETPOINT_BOTTOM) < .02 || motor.isFwdLimitSwitchClosed();
    }

    public boolean atTransport()
    {
        return Math.abs(motor.getPosition() - SETPOINT_TRANSPORT) < .02;
    }

    public boolean atTop()
    {
        return Math.abs(motor.getPosition() - SETPOINT_TOP) < .02 || motor.isRevLimitSwitchClosed();
    }
}
