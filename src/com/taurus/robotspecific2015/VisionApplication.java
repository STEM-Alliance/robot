package com.taurus.robotspecific2015;

import com.taurus.Application;
import com.taurus.Utilities;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionApplication extends Application {

    Vision vision = new Vision();
    SendableChooser autoChooser;
    Autostate autostate;
    double StartTime = 0;

    public VisionApplication()
    {
        super();

        vision.Start();

        autoChooser = new SendableChooser();
        autoChooser.addDefault("Drive forward", Integer.valueOf(0));
        autoChooser.addObject("Go to tote", Integer.valueOf(1));
        SmartDashboard.putData("Autonomous mode", autoChooser);
    }

    @Override
    public void TeleopInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousInitRobotSpecific()
    {
        autostate = Autostate.start;
    }

    @Override
    public void AutonomousPeriodicRobotSpecific()
    {
        int automode = ((Integer) autoChooser.getSelected()).intValue();

        switch (automode)
        {
            case 0:
                DriveForwardAuto();
                break;

            case 1:
                GotoToteAuto();
                break;
        }

    }

    enum Autostate {
        start, go, stop

    }

    private void DriveForwardAuto()
    {

        double DriveTime = 5;

        switch (autostate)
        {
            case start:
                StartTime = Timer.getFPGATimestamp();
                autostate = Autostate.go;
                break;

            case go:
                SwerveVector Velocity = new SwerveVector(0, 1);

                drive.setGearHigh(false);
                drive.UpdateDrive(Velocity, 0, 0);
                if (Timer.getFPGATimestamp() > StartTime + DriveTime)
                {
                    autostate = Autostate.stop;

                }
                break;

            case stop:
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                break;
        }

    }

    private void GotoToteAuto()
    {
        double maxSlide = prefs.getDouble("MaxSlide", 1), slideP = prefs
                .getDouble("SlideP", 1), targetX = prefs.getDouble(
                "SlideTargetX", .5), forwardSpeed = prefs.getDouble(
                "AutoSpeed", .5);

        SwerveVector Velocity;

        if (vision.getToteSeen())
        {
            Velocity = new SwerveVector(-forwardSpeed, Utilities.clampToRange(
                    (vision.getResultX() - targetX) * slideP, -maxSlide,
                    maxSlide));
        }
        else
        {
            Velocity = new SwerveVector(-forwardSpeed, 0);
        }

        double Rotation = 0;
        double Heading = 270;

        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());

        drive.setGearHigh(false);
        drive.UpdateDrive(Velocity, Rotation, Heading);
    }

    @Override
    public void AutonomousDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModePeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

}
