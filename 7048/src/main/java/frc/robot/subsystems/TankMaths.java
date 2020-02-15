package frc.robot.subsystems;

import frc.robot.config.TankConfig;
import frc.robot.utilities.ConsoleLogger;
import frc.robot.EnhancedRobot;

public final class TankMaths
{
    private static final double kTicksPerRev = 4096.0;
    private static final double kScrub;
    private static final double kWheelCircumference;
    private static final double kWheelToEncoderGearRatio;
    private static final double kWheelFromCenter;
    public static final double kVelocityMax;
    public static final double kVelocityMaxInchesPerSecond;

    static
    {
        final TankConfig config = EnhancedRobot.getConfig().getTankConfig();
        kScrub = config.SCRUB;
        kWheelCircumference = config.WHEEL_DIAMETER * Math.PI;
        kWheelFromCenter = config.WIDTH / 2.0;
        kWheelToEncoderGearRatio = config.GEAR_RATIO_LOW;
        kVelocityMax = config.VELOCITY_MAX;

        kVelocityMaxInchesPerSecond = ticksToInchesPerSecond(kVelocityMax);
        
        ConsoleLogger.info(String.format("Tank Max Velocity:     %.2f\"/s", kVelocityMaxInchesPerSecond));
        ConsoleLogger.info(String.format("Wheel Circumference:   %.2f\"", kWheelCircumference));
        ConsoleLogger.info(String.format("Wheel to Center:       %.2f\"", kWheelFromCenter));
        ConsoleLogger.info(String.format("Wheel to Encoder:      %.2f:1", kWheelToEncoderGearRatio));
    }

    private TankMaths() { /** No instances */ };

    public static double fpsToTicks(double fps)
    {
        return fps / 10.0 * kTicksPerRev * kWheelToEncoderGearRatio / kWheelCircumference * 12.0;
    }

    public static double inchesToTicks(double inchesForward)
    {
        return inchesForward / kWheelCircumference * kTicksPerRev * kWheelToEncoderGearRatio;
    }

    public static double inchesPerSecondToTicks(double inchesForward)
    {
        return inchesToTicks(inchesForward) / 10.0;
    }

    public static double inchesOfWheelTurning(double deltaAngle)
    {
        return deltaAngle / 360.0 * Math.PI * 2.0 * kWheelFromCenter / kScrub;
    }

    public static double ticksToFPS(double ticksForward)
    {
        return ticksForward * 10.0 / kTicksPerRev / kWheelToEncoderGearRatio * kWheelCircumference / 12.0;
    }

    public static double ticksToInches(double ticksForward)
    {
        return (ticksForward * kWheelCircumference) / (kTicksPerRev * kWheelToEncoderGearRatio);
    }

    public static double ticksToInchesPerSecond(double ticksForward)
    {
        return ticksToInches(ticksForward) * 10.0;
    }

    public static double ticksToPercent(double ticksForward)
    {
        return ticksForward / kVelocityMax;
    }
}
