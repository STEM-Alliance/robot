package frc.robot.reuse.hardware;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifierFaults;
import com.ctre.phoenix.CANifierStatusFrame;
import com.ctre.phoenix.CANifierStickyFaults;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;

/** @author STEM Alliance of Fargo Moorhead */
public class Canifier implements LEDs
{
    public static class RGB
    {
        public final int r;
        public final int g;
        public final int b;

        public RGB(int r, int g, int b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    private static final RGB kRed = new RGB(255, 0, 0);
    private static final RGB kBlue = new RGB(0, 0, 255);
    private static final RGB kSignalHuman = kRed;
    private static final RGB kTestSuccess = new RGB(0, 255, 0);
    private static final RGB kTestFail = kRed;

    private final RGB kDriveTeam;
    private final RGB kTeleop;
    private final CANifier hardware;
    private CachedIO cachedIO = new CachedIO();
    private RGB kAlliance = kBlue;
    private boolean isAuto = false;

    public Canifier(int address, RGB teamColor)
    {
        this(address, teamColor, new RGB(255, 255, 255));
    }

    public Canifier(int address, RGB teamColor, RGB driveTeamColor)
    {
        hardware = new CANifier(address);
        kTeleop = teamColor;
        kDriveTeam = driveTeamColor;

        final int timeout = TalonFactory.kTimeoutMs;  // Status frame default rates at bottom of class
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_1_General, 1000, timeout);
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_2_General, 10, timeout);
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_3_PwmInputs0, 1000, timeout);
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_4_PwmInputs1, 1000, timeout);
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_5_PwmInputs2, 1000, timeout);
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_6_PwmInputs3, 1000, timeout);
    }

    public void cacheSensors(boolean isDisabled)
    {
        if (!isDisabled)
        {
            final CANifier.PinValues pins = new CANifier.PinValues();
            hardware.getGeneralInputs(pins);
            cachedIO.limitSwitchB = !pins.LIMR;
            cachedIO.limitSwitchF = !pins.LIMF;
            cachedIO.pwm0 = !pins.SPI_CLK_PWM0;
            cachedIO.pwm1 = !pins.SPI_MOSI_PWM1;
        }
    }

    public void off()
    {
        setLEDs(new RGB(0, 0, 0));
    }

    public void signalDriveTeam()
    {
        setLEDs((isAuto) ? kAlliance : kDriveTeam);  // Disabled in autonomous
    }

    public void signalHumanPlayer()
    {
        setLEDs(kSignalHuman);
    }

    public void setForAuto(Alliance team)
    {
        kAlliance = (team == Alliance.Red) ? kRed : kBlue;
        isAuto = true;
        useRobotModeColor();
    }

    public void setForTeleop()
    {
        isAuto = false;
        useRobotModeColor();
    }

    public void useRobotModeColor()
    {
        setLEDs((isAuto) ? kAlliance : kTeleop);
    }

    /** How fast CANifier sends out digital values to RIO over CAN bus */
    public void setDigitalInputFramePeriod(int ms)
    {
        hardware.setStatusFramePeriod(CANifierStatusFrame.Status_2_General, ms, 100);
    }

    private void setLEDs(RGB color)
    {
        hardware.setLEDOutput(color.r, CANifier.LEDChannel.LEDChannelA);
        hardware.setLEDOutput(color.g, CANifier.LEDChannel.LEDChannelB);
        hardware.setLEDOutput(color.b, CANifier.LEDChannel.LEDChannelC);
    }

    public int getDeviceID()
    {
        return hardware.getDeviceID();
    }

    public boolean getLimitSwitchF()
    {
        return cachedIO.limitSwitchF;
        //return !hardware.getGeneralInput(GeneralPin.LIMF);  // TODO Remove after testing cachedIO correct
    }

    public boolean getLimitSwitchR()
    {
        return cachedIO.limitSwitchB;
        // return !hardware.getGeneralInput(GeneralPin.LIMR);
    }

    public boolean getPWM0()
    {
        return cachedIO.pwm0;
        // return !hardware.getGeneralInput(GeneralPin.SPI_CLK_PWM0P);
    }

    public boolean getPWM1()
    {
        return cachedIO.pwm1;
        // return !hardware.getGeneralInput(GeneralPin.SPI_MOSI_PWM1P);
    }

    public boolean testRobotSpecificColors()
    {
        double secondsBetweenColors = 2.0;

        signalHumanPlayer();
        Timer.delay(secondsBetweenColors);
        signalDriveTeam();
        Timer.delay(secondsBetweenColors);
        useRobotModeColor();
        Timer.delay(secondsBetweenColors);
        setForAuto(Alliance.Red);
        Timer.delay(secondsBetweenColors);
        setForAuto(Alliance.Blue);
        return true;
    }

    public boolean testScrollAll()
    {
        return false;
    }

    public void signalFunctionalTestResult(boolean testsPassed)
    {
        setLEDs((testsPassed) ? kTestSuccess : kTestFail);
    }

    public boolean testFault()
    {
        CANifierFaults toFill = new CANifierFaults();
        hardware.getFaults(toFill);
        boolean result = toFill.hasAnyFault();
        System.out.println(String.format("Canifier is %s showing faults", (result) ? "not" : ""));
        return result;
    }

    public boolean testStickyFault()
    {
        CANifierStickyFaults toFill = new CANifierStickyFaults();
        hardware.getStickyFaults(toFill);
        boolean result = toFill.hasAnyFault();
        System.out.println(String.format("Canifier is %s showing sticky faults", (result) ? "not" : ""));
        return result;
    }

    private class CachedIO
    {
        public boolean limitSwitchB = false;
        public boolean limitSwitchF = false;
        public boolean pwm0 = false;
        public boolean pwm1 = false;
    }

    // Status Frame Default Period (ms) Signals
    //
    // Status 1     100                 LED Duty Cycles, Battery Voltage, Faults
    // Status 2     10                  Quadrature, Digital Inputs
    // Status 3     100                 PWM Channel 0 measurement
    // Status 4     100                 PWM Channel 1 measurement
    // Status 5     100                 PWM Channel 2 measurement
    // Status 6     100                 PWM Channel 3 measurement
}
