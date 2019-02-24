package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.CanifierDefault;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CanifierTest extends EnhancedSubsystem
{
    private final RGB kYellow = new RGB(255, 255, 0);
    public Canifier canifier = new Canifier(6, kYellow);

    @Override
    public void reportState() {
        SmartDashboard.putString("Command", this.getCurrentCommandName());
        SmartDashboard.putBoolean("Limit F", canifier.getLimitSwitchF());
        SmartDashboard.putBoolean("Limit R", canifier.getLimitSwitchR());
        SmartDashboard.putBoolean("PWM 0", canifier.getPWM0());
        SmartDashboard.putBoolean("PWM 1", canifier.getPWM1());
        SmartDashboard.putBoolean("PWM 2", canifier.getPWM2());
    }

    @Override
    public TestReport runFunctionalTest() {
        return null;
    }

    @Override
    public void cacheSensors(boolean isDisabled) {

    }

    @Override
    protected void initDefaultCommand() {
        this.setDefaultCommand(new CanifierDefault());
    }

    private static CanifierTest instance = new CanifierTest();
    public static CanifierTest getInstance()
    {
        return instance;
    }
}