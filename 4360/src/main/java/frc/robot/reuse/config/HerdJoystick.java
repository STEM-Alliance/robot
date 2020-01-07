package frc.robot.reuse.config;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

/**
 * @author Team 4818 WFRobotics
 */
public class HerdJoystick
{
    public static enum AXIS
    {
        X(0),
        Y(1),
        Z(2),
        TWIST(3),
        THROTTLE(4);

        public final int value;

        private AXIS(int value) { this.value = value; }
        public int get() { return value; }
    }

    public static enum HAT
    {
        NONE(-1),
        UP(0),
        UP_RIGHT(45),
        RIGHT(90),
        DOWN_RIGHT(135),
        DOWN(180),
        DOWN_LEFT(225),
        LEFT(270),
        UP_LEFT(315);

        public final int value;

        private HAT(int value) { this.value = value; }
        public int get() { return value; }
    }

    private static final double DEADBAND = .1;

    private final edu.wpi.first.wpilibj.Joystick hw;

    public HerdJoystick(int driveStationUSBPort)
    {
        hw = new edu.wpi.first.wpilibj.Joystick(driveStationUSBPort);
    }

    /**
     * Get value
     * @return value (-1 to 1)
     */
    private double getRawAxis(int axis)
    {
        return scaleForDeadband(hw.getRawAxis(axis));  // -1 to 1
    }

    
    private double scaleForDeadband(double value)
    {
        final double abs = Math.abs(value);

        if (abs < DEADBAND)
        {
            value = 0;
        }
        else
        {
            value = Math.signum(value) * ((abs - DEADBAND) / (1 - DEADBAND));
        }
        return value;
    }
    /**
     * Get value
     * @return value (-1 to 1)
     */
    public double getX()
    {
        return getRawAxis(AXIS.X.get());
    }

    /**
     * Get value
     * @return value (-1 to 1)
     */
    public double getY()
    {
        return getRawAxis(AXIS.Y.get());
    }

    public double getThrottle()
    {
        return hw.getThrottle();
    }

    public boolean getButtonPressed(int button)
    {
        return hw.getRawButton(button);
    }

    public boolean getButtonPressed(HAT direction)
    {
        return hw.getPOV(0) == direction.get();
    }

    public int getHat()
    {
        return hw.getPOV(0);
    }

    public void setRumble(Hand side, double value)
    {
        RumbleType r = (side == Hand.kLeft) ? RumbleType.kLeftRumble : RumbleType.kRightRumble;
        hw.setRumble(r, value);
    }
}