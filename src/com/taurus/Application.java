package com.taurus;

public abstract class Application {
    public abstract void TeleopInitRobotSpecific();

    public abstract void TeleopPeriodicRobotSpecific();

    public abstract void TeleopDeInitRobotSpecific();

    public abstract void AutonomousInitRobotSpecific();

    public abstract void AutonomousPeriodicRobotSpecific();

    public abstract void AutonomousDeInitRobotSpecific();

    public abstract void TestModeInitRobotSpecific();

    public abstract void TestModePeriodicRobotSpecific();

    public abstract void TestModeDeInitRobotSpecific();

    public abstract void DisabledInitRobotSpecific();

    public abstract void DisabledPeriodicRobotSpecific();

    public abstract void DisabledDeInitRobotSpecific();

    public void teleopInit()
    {
        // TODO: Put common routine here

        TeleopInitRobotSpecific();
    }

    public void teleopPeriodic()
    {
        // TODO: Put common routine here

        TeleopPeriodicRobotSpecific();
    }

    public void teleopDeInit()
    {
        // TODO: Put common routine here

        TeleopDeInitRobotSpecific();
    }

    public void AuntonomousInit()
    {
        // TODO: Put common routine here

        AutonomousInitRobotSpecific();
    }

    public void AuntonomousDeInit()
    {
        // TODO: Put common routine here

        AutonomousDeInitRobotSpecific();
    }

    public void AutnomousPeriodic()
    {
        // TODO: Put common routine here

        AutonomousPeriodicRobotSpecific();
    }

    public void AutnomousDeInit()
    {
        // TODO: Put common routine here

        AutonomousDeInitRobotSpecific();
    }

    public void TestModeInit()
    {
        // TODO: Put common routine here

        TestModeInitRobotSpecific();
    }

    public void TestModePeriodic()
    {
        // TODO: Put common routine here

        TestModePeriodicRobotSpecific();
    }

    public void TestModeDeInit()
    {
        // TODO: Put common routine here

        TestModeDeInitRobotSpecific();
    }

    public void DisableInit()
    {
        // TODO: Put common routine here

        DisabledInitRobotSpecific();
    }

    public void DisbalePeriodic()
    {
        // TODO: Put common routine here

        DisabledPeriodicRobotSpecific();
    }

    public void DisableDeInit()
    {
        // TODO: Put common routine here

        DisabledDeInitRobotSpecific();
    }
}
