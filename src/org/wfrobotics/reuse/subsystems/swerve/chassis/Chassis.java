package org.wfrobotics.reuse.subsystems.swerve.chassis;

import org.wfrobotics.reuse.subsystems.swerve.Shifter;
import org.wfrobotics.reuse.subsystems.swerve.wheel.SwerveWheel;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class controls swerve drive at the robot/chassis level
 * @author Team 4818 WFRobotics
 */
public class Chassis
{
    RobotState state = RobotState.getInstance();
    HerdLogger log = new HerdLogger(Chassis.class);
    private SwerveWheel[] wheels = new SwerveWheel[4];  // TODO test this as static for performance
    private Shifter shifters;

    private double lastVelocityTimestamp;

    double maxWheelMagnitudeLast;  // TODO use in interpolation of distance traveled autonomous routines

    public Chassis()
    {
        wheels[0] = new SwerveWheel("WheelBR", RobotMap.CAN_SWERVE_DRIVE_TALONS[0], RobotMap.CAN_SWERVE_ANGLE_TALONS[0]);
        wheels[1] = new SwerveWheel("WheelFR", RobotMap.CAN_SWERVE_DRIVE_TALONS[1], RobotMap.CAN_SWERVE_ANGLE_TALONS[1]);
        wheels[2] = new SwerveWheel("WheelFL", RobotMap.CAN_SWERVE_DRIVE_TALONS[2], RobotMap.CAN_SWERVE_ANGLE_TALONS[2]);
        wheels[3] = new SwerveWheel("WheelBL", RobotMap.CAN_SWERVE_DRIVE_TALONS[3], RobotMap.CAN_SWERVE_ANGLE_TALONS[3]);
        shifters = new Shifter();

        lastVelocityTimestamp = Timer.getFPGATimestamp();
    }

    // TODO nice toString()?

    public synchronized void updateWheelVectors(HerdVector robotVelocity, double robotRotation, boolean gear, boolean brake)
    {
        setWheelVectors(robotVelocity, robotRotation, gear, brake);
        shifters.setGear(gear);
        state.updateRobotGear(shifters.isHighGear());
    }

    /** Scale the wheel vectors, then set individual wheel hardware */
    public void setWheelVectors(HerdVector robotVelocity, double robotRotation, boolean gear, boolean brake)
    {
        ChassisSignal command = new ChassisSignal(robotVelocity, robotRotation);
        HerdVector[] WheelsScaled;

        command = applyClampVelocity(command);
        command = (Config.ENABLE_SQUARE_MAGNITUDE) ? applyMagnitudeSquare(command) : command;
        command = (Config.ENABLE_ROTATION_LIMIT) ? applyRotationLimit(command) : command;
        command = (Config.CRAWL_MODE_ENABLE) ? applyCrawlMode(command) : command;
        command = (Config.ENABLE_ACCELERATION_LIMIT) ? applyAccelerationLimit(command) : command;
        state.updateRobotVelocity(command.velocity);

        log.info("Chassis Command", command.toString());

        WheelsScaled = chassisToWheelCommands(command);
        wheels[0].set(WheelsScaled[0], brake);
        wheels[1].set(WheelsScaled[1], brake);
        wheels[2].set(WheelsScaled[2], brake);
        wheels[3].set(WheelsScaled[3], brake);
    }

    private HerdVector[] chassisToWheelCommands(ChassisSignal robot)
    {
        HerdVector[] wheelCommands = new HerdVector[4];
        HerdVector v = new HerdVector(robot.velocity);
        HerdVector w = new HerdVector(robot.spin, 90);

        // V + W x R
        // Add a amount (W x R) to each wheel command so the chassis rotates (by W) while moving (in vector V)
        HerdVector wheelBR = v.add(w.cross(Config.rBR));
        HerdVector wheelFR = v.add(w.cross(Config.rFR));
        HerdVector wheelFL = v.add(w.cross(Config.rFL));
        HerdVector wheelBL = v.add(w.cross(Config.rBL));

        // Scale magnitudes to range 0 - 1 for wheel
        double maxMag = wheelBR.getMag();
        maxMag = (wheelFR.getMag() > maxMag) ? wheelFR.getMag() : maxMag;
        maxMag = (wheelFL.getMag() > maxMag) ? wheelFL.getMag() : maxMag;
        maxMag = (wheelBL.getMag() > maxMag) ? wheelBL.getMag() : maxMag;

        if (maxMag > 1)
        {
            wheelBR = wheelBR.scale(1 / maxMag);
            wheelFR = wheelFR.scale(1 / maxMag);
            wheelFL = wheelFL.scale(1 / maxMag);
            wheelBL = wheelBL.scale(1 / maxMag);
            maxWheelMagnitudeLast = maxMag;
        }
        else
        {
            maxWheelMagnitudeLast = 1;
        }

        // Mirroring Y is purely to match our old swerve. Seems like an extra step beyond what math says we need
        // TODO What's inverted in the real robot such that the Y's need to be mirrored?
        wheelCommands[0] = new HerdVector(wheelBR.getMag(), -wheelBR.getAngle());
        wheelCommands[1] = new HerdVector(wheelFR.getMag(), -wheelFR.getAngle());
        wheelCommands[2] = new HerdVector(wheelFL.getMag(), -wheelFL.getAngle());
        wheelCommands[3] = new HerdVector(wheelBL.getMag(), -wheelBL.getAngle());

        return wheelCommands;
    }

    private ChassisSignal applyClampVelocity(ChassisSignal robot)
    {
        double RobotVelocityClamped = (robot.velocity.getMag() > 1.0) ? 1 : robot.velocity.getMag();
        HerdVector clamped = new HerdVector(RobotVelocityClamped, robot.velocity.getAngle());

        return new ChassisSignal(clamped, robot.spin);
    }

    private ChassisSignal applyMagnitudeSquare(ChassisSignal robot)  // Finer control at low speed
    {
        robot.velocity.scale(robot.velocity);

        return robot;
    }

    //Limit before slowing speed so it runs using the original values set limitations on rotation, so if driving full speed it doesn't take priority
    private ChassisSignal applyRotationLimit(ChassisSignal robot)
    {
        Config.rotationAdjustMin = Preferences.getInstance().getDouble("DRIVE_MIN_ROTATION", Config.rotationAdjustMin);
        double RotationAdjust = Math.min(1 - robot.velocity.getMag() + Config.rotationAdjustMin, 1);

        //robot.spin = Utilities.clampToRange(robot.spin, -RotationAdjust, RotationAdjust);
        robot.spin *= RotationAdjust;
        SmartDashboard.putNumber("SwerveRotationAdjust", RotationAdjust);

        return robot;
    }

    private ChassisSignal applyCrawlMode(ChassisSignal robot)  // Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
    {
        double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", Config.DRIVE_SPEED_CRAWL);
        double crawlMag = (Config.CRAWL_MODE_DEFAULT_HIGH) ? 1 - Config.crawlModeMagnitude : Config.crawlModeMagnitude;
        double scalingFactor = Utilities.scaleToRange(crawlMag, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1

        robot.spin *= scalingFactor;
        robot.velocity.scale(scalingFactor);

        return robot;
    }

    // Returns the velocity restricted by the maximum acceleration
    // A low MAX_ACCELERATION value will slow the speed down  more than a high value
    // TODO: this should be replaced by a PID controller, probably...
    private ChassisSignal applyAccelerationLimit(ChassisSignal robot)
    {
        double now = Timer.getFPGATimestamp();
        double dt = now - lastVelocityTimestamp;
        HerdVector delta = robot.velocity.sub(state.getRobotVelocity());

        lastVelocityTimestamp = now;
        Config.accelerationMax = Preferences.getInstance().getDouble("MAX_ACCELERATION", Config.accelerationMax);

        // Accelerating/decelerating too fast? Slow down
        if (Math.abs(delta.getMag()) > Config.accelerationMax * dt)
        {
            HerdVector correctionFactor = new HerdVector(Config.accelerationMax * dt, 0);
            robot.velocity = state.getRobotVelocity().add(correctionFactor);  // TODO We should set, not add?
        }

        return robot;
    }
}
