package com.taurus;

public interface IApplication {
    // Runs when robot is disabled
    public void disabledPeriodic();

    // Runs when robot is disabled
    public void disabledInit();

    // Runs when operator mode is enabled.
    public void teleopInit();

    // Runs during operator control
    public void teleopPeriodic();

    // Called at start of autonomous mode
    public void autonomousInit();

    // Called periodically during autonomous mode
    public void autonomousPeriodic();
}
