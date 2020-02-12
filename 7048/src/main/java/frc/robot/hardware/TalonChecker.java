package frc.robot.hardware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import frc.robot.config.TalonConfig.ClosedLoopConfig;
import frc.robot.utilities.ConsoleLogger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;

/**
 * Verifies all of the TalonSRX's
 * @author STEM Alliance of Fargo Moorhead
 */
public abstract class TalonChecker
{
    private enum CRITERIA
    {
        NO_FASTER_THAN,  // Ex: 2ms
        NO_SLOWER_THAN,  // Ex: 1000ms
    }

    public static boolean checkClosedLoopConfig(ClosedLoopConfig c)
    {
        boolean result = true;
        final int masters = c.masters.size();
        final int gains = c.gains.size();
        String output;

        output = String.format("Number masters in %s config: %d", c.name, masters);
        if (masters > 0)
        {
            System.out.println(output);
        }
        else
        {
            result &= false;
            ConsoleLogger.error(output);
        }

        output = String.format("Number gains in %s config: %d", c.name, gains);
        if (gains > 0)
        {
            System.out.println(output);
        }
        else
        {
            result &= false;
            ConsoleLogger.error(output);
        }

        if (result)
        {
            System.out.println(String.format("Config %s okay", c.name));
        }
        return result;
    }

    /** Check if the encoder is plugged in */
    public static boolean checkEncoder(TalonSRX... motors)
    {
        boolean result = true;

        for (TalonSRX t : motors)
        {
            boolean isPresent = t.getSensorCollection().getPulseWidthRiseToRiseUs() != 0;

            if (isPresent)
            {
                System.out.println(String.format("Motor %d encoder present", t.getDeviceID()));
            }
            else
            {
                ConsoleLogger.error(String.format("Encoder not present for talon %d", t.getDeviceID()));
            }
            result &= isPresent;
        }
        return result;
    }

    /** Check if motor controller needs a software update */
    public static boolean checkFirmware(BaseMotorController... motors)
    {
        final int expectedFirmwares = 0x0417;  // VictorSPX firmware always same version
        boolean result = true;

        for (BaseMotorController t : motors)
        {
            final int version = t.getFirmwareVersion();
            final boolean isUpToDate = version == expectedFirmwares;
            final int address = t.getDeviceID();

            System.out.println(String.format("Motor %d firmware %s", address, (isUpToDate) ? "okay" : "too old"));
            result &= isUpToDate;
        }
        return result;
    }

    /** Check if motor controller needs a software update */
    public static boolean checkFirmware(List<BaseMotorController> motors1, BaseMotorController... motors2)
    {
        List<BaseMotorController> all = new ArrayList<BaseMotorController>(motors1);
        all.addAll(Arrays.asList(motors2));

        return checkFirmware(all);
    }

    /** Check speed CAN messages are sent. Visually able to see potential performance improvements/issues. */
    public static boolean checkFrameRates(TalonSRX t)  // TODO pass in criteria (ex: CurrentLimits)?
    {
        boolean result = true;
        List<FrameCriteria> expectations = Arrays.asList(new FrameCriteria[] {
            new FrameCriteria(StatusFrameEnhanced.Status_1_General, 5, CRITERIA.NO_FASTER_THAN),
            new FrameCriteria(StatusFrameEnhanced.Status_1_General, 20, CRITERIA.NO_SLOWER_THAN),
            new FrameCriteria(StatusFrameEnhanced.Status_2_Feedback0, 5, CRITERIA.NO_FASTER_THAN),
            new FrameCriteria(StatusFrameEnhanced.Status_10_MotionMagic, 10, CRITERIA.NO_FASTER_THAN),
            new FrameCriteria(StatusFrameEnhanced.Status_13_Base_PIDF0, 100, CRITERIA.NO_FASTER_THAN),
        });

        ConsoleLogger.info(String.format("Motor %d Frames:", t.getDeviceID()));
        for (FrameCriteria criteria : expectations)
        {
            int actual = t.getStatusFramePeriod(criteria.frame, 0);
            ConsoleLogger.info(String.format("%s: %d ms", criteria.frame, actual));
            result &= criteria.verify(actual);
        }
        System.out.println(String.format("Motor %d frames %s", t.getDeviceID(), (result) ? "okay" : "failed"));
        return result;
    }

    /** Check the encoder reads positive position changes for positive commanded values, used in closed loop */
    public static boolean checkSensorPhase(double percentVoltage, TalonSRX... motors)
    {
        double movementDuration = 0.25;
        List<TalonSRX> motorsUnderTest = Arrays.asList(motors);  // Must set all at once to safely move subsystems
        List<Integer> initialPositions = new ArrayList<Integer>();

        for (TalonSRX motor : motorsUnderTest)
        {
            initialPositions.add(motor.getSelectedSensorPosition(0));
        }
        setMotors(motorsUnderTest, percentVoltage);
        Timer.delay(movementDuration);
        setMotors(motorsUnderTest, 0.0);
        return verifySensorPhasePositive(motorsUnderTest, initialPositions);
    }

    private static void setMotors(Iterable<TalonSRX> motors, double percentVoltage)
    {
        for (BaseMotorController motor : motors)
        {
            motor.set(ControlMode.PercentOutput, percentVoltage);
        }
    }

    private static boolean verifySensorPhasePositive(List<TalonSRX> motors, List<Integer> initialPositions)
    {
        Iterator<TalonSRX> itMotors = motors.iterator();
        Iterator<Integer> itInitialPositions = initialPositions.iterator();
        boolean result = true;

        while (itMotors.hasNext() && itInitialPositions.hasNext())
        {
            BaseMotorController motor = itMotors.next();
            int deltaticks = motor.getSelectedSensorPosition(0) - itInitialPositions.next();

            if (Math.abs(deltaticks) < 4096 * 0.01)
            {
                System.out.println(String.format("WARNING: Motor %d sensor phase undeterministic, only moved %d ticks", motor.getDeviceID(), deltaticks));
                result = false;
            }
            else if (deltaticks < 0)
            {
                System.out.println(String.format("ERROR: Motor %d out of phase, moved %d ticks", motor.getDeviceID(), deltaticks));
                result = false;
            }
            else
            {
                System.out.println(String.format("Motor %d in phase, moved %d ticks", motor.getDeviceID(), deltaticks));
            }
        }
        return result;
    }

    protected static class FrameCriteria
    {
        public final StatusFrameEnhanced frame;
        public final int rate;
        public final CRITERIA type;

        public FrameCriteria(StatusFrameEnhanced frame, int rate, CRITERIA type)
        {
            this.frame = frame;
            this.rate = rate;
            this.type = type;
        }

        public boolean verify(int actual)
        {
            boolean result;

            switch (type)
            {
                case NO_FASTER_THAN:
                    result = actual >= rate;
                    if (!result)
                    {
                        ConsoleLogger.warning(String.format("Expected %s no faster than %d ms, got %d ms", frame, rate, actual));
                    }
                    break;
                case NO_SLOWER_THAN:
                    result = actual <= rate;
                    if (!result)
                    {
                        ConsoleLogger.warning(String.format("Expected %s no faster than %d ms, got %d ms", frame, rate, actual));
                    }
                    break;
                default:
                    result = true;
            }
            return result;
        }
    }
}
