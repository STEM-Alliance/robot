package org.wfrobotics.robot;

import org.wfrobotics.reuse.subsystems.tank.TankService;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.IntakeSolenoidSubsystem;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot
{
    private final HerdLogger log = new HerdLogger(Robot.class);
    private final Scheduler scheduler = Scheduler.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();

    public static TankService driveService;

    public static LiftSubsystem liftSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static IntakeSolenoidSubsystem intakeSolenoidSubsystem;

    public static DashboardView dashboardView;

    public static IO controls;

    Command autonomousCommand;
    double lastPeriodicTime = 0;

    public void robotInit()
    {

        driveService = TankService.getInstance();
        controls = IO.getInstance();

        //        liftSubsystem = new LiftSubsystem();
        //        intakeSubsystem = new IntakeSubsystem();
        //        intakeSolenoidSubsystem = new IntakeSolenoidSubsystem();

        // uncomment if using USB camera to stream video from roboRio
        //dashboardView = new DashboardView();



        // TODO default config?
        //CameraServer.getInstance();
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        while (isOperatorControl() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void autonomous()
    {
        if(!matchState.update())
        {
            // something went wrong, and we didn't get the match info data
            // TODO error?
        }

        autonomousCommand =  Autonomous.setupAndReturnSelectedMode();
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void disabled()
    {

        while (isDisabled())
        {
            driveService.zeroGyro();
            log.info("TeamColor", (m_ds.getAlliance() == Alliance.Red) ? "Red" : "Blue");

            allPeriodic();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled())
        {
            allPeriodic();
        }
    }

    private void allPeriodic()
    {

        SmartDashboard.putNumber("Battery V", RobotController.getInputVoltage());
        SmartDashboard.putNumber("Battery A", RobotController.getInputCurrent());

        state.logState();

        double start = Timer.getFPGATimestamp();
        scheduler.run();
        //log.debug("Periodic Time", getPeriodicTime(start));
        SmartDashboard.putNumber("Periodic Time ", Timer.getFPGATimestamp() - start);
    }
}
