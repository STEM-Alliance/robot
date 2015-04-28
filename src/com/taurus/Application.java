package com.taurus;

import edu.wpi.first.wpilibj.Preferences;

public interface Application {    
    public static Preferences prefs = Preferences.getInstance();     
    
    public abstract void TeleopInit();

    public abstract void TeleopPeriodic();

    public abstract void TeleopDeInit();

    public abstract void AutonomousInit();

    public abstract void AutonomousPeriodic();

    public abstract void AutonomousDeInit();

    public abstract void TestModeInit();

    public abstract void TestModePeriodic();

    public abstract void TestModeDeInit();

    public abstract void DisabledInit();

    public abstract void DisabledPeriodic();

    public abstract void DisabledDeInit();
}
