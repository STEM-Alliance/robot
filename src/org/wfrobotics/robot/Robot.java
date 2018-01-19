package org.wfrobotics.robot;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSubsystem;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ClimbSubsystem;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LifterSubsystem;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
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
    private final MatchState matchState = MatchState.getInstance();
    
    public static SwerveSubsystem driveSubsystem;
    
    public static ClimbSubsystem climbSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static LifterSubsystem lifterSubsystem;
    
    public static DashboardView dashboardView;
    
    public static IO controls;
    
    Command autonomousCommand;
    double lastPeriodicTime = 0;

    public void robotInit()
    {
        driveSubsystem = new SwerveSubsystem();
        dashboardView = new DashboardView();
        
        climbSubsystem = new ClimbSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        lifterSubsystem = new LifterSubsystem();

        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized

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
        autonomousCommand =  Autonomous.setupSelectedMode();
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
            driveSubsystem.zeroGyro();
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
        log.info("Drive", driveSubsystem);
        log.info("Battery", m_ds.getBatteryVoltage());
        state.logState();

        double start = Timer.getFPGATimestamp();
        scheduler.run();
        //log.debug("Periodic Time", getPeriodicTime(start));
        SmartDashboard.putNumber("Periodic Time ", Timer.getFPGATimestamp() - start);
    }

    /** Should be <= 20ms, the rate the driver station pings with IO updates. This assumes using closed loop CANTalon's or sensors/PID are all on our fast service thread to prevent latency */
    @SuppressWarnings("unused")
    private String getPeriodicTime(double start)
    {
        return String.format("%.1f ms", (Timer.getFPGATimestamp() - start) * 1000);
    }
}
