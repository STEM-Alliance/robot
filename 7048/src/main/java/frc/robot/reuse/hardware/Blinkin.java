package frc.robot.reuse.hardware;


import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.reuse.hardware.lowleveldriver.BlinkinPatterns;
import frc.robot.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

/** @author STEM Alliance of Fargo Moorhead */
public class Blinkin implements LEDs
{
    private final Spark led;
    private final PatternName kTeleop;
    private final PatternName kDriveTeam;
    private PatternName kAlliance = PatternName.Blue;
    private boolean isAuto = false;

    public Blinkin(int pwmChannel, PatternName teamColor)
    {
        this(pwmChannel, teamColor, PatternName.Strobe_White);
    }

    public Blinkin(int pwmChannel, PatternName teamColor, PatternName driveTeamColor)
    {
        led = new Spark(pwmChannel);
        kTeleop = teamColor;
        kDriveTeam = driveTeamColor;
    }

    public void off()
    {
        led.set(BlinkinPatterns.getValue(PatternName.Black));
    }

    public void signalDriveTeam()
    {
        led.set(BlinkinPatterns.getValue((isAuto) ? kAlliance : kDriveTeam));  // Override in auto
    }

    public void signalHumanPlayer()
    {
        led.set(BlinkinPatterns.getValue(PatternName.Strobe_Red));
    }

    public void setForAuto(Alliance team)
    {
        kAlliance = (team == Alliance.Red) ? PatternName.Red : PatternName.Blue;
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
        led.set(BlinkinPatterns.getValue((isAuto) ? kAlliance : kTeleop));
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

    public boolean testScrollAll(PatternName... vectors)
    {
        double secondsBetweenColors = 2.0;

        for (PatternName color : vectors)
        {
            led.set(BlinkinPatterns.getValue(color));
            Timer.delay(secondsBetweenColors);
            if (DriverStation.getInstance().isDisabled())
            {
                break;  // Otherwise reset robot or stuck scrolling
            }
        }
        return true;
    }

    public void signalFunctionalTestResult(boolean testsPassed)
    {
        led.set(BlinkinPatterns.getValue((testsPassed) ? PatternName.Rainbow_with_Glitter : PatternName.Breath_Red));  // Easter egg - show functional test result
    }

    @Override
    public boolean testScrollAll() {
        // TODO Auto-generated method stub
        return false;
    }
}
