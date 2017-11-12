package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.subsystems.swerve.wheel.SwerveWheel;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;

// TODO use two PID, one for each gear
// TODO rotation adjust to cap angle pid or something? Value always 1 now
// TODO tune drive PID (D term), remove chassis accel limit when okay

/**
 * Controls swerve drive at the robot/chassis level
 * @author Team 4818 WFRobotics
 */
public class Chassis
{
    Preferences prefs = Preferences.getInstance();
    RobotState state = RobotState.getInstance();
    HerdLogger log = new HerdLogger(Chassis.class);

    private final SwerveWheel[] wheels = new SwerveWheel[4];
    private final String[] wheelNames = {"WheelFR", "WheelFL", "WheelBR", "WheelBL"};

    private double lastVelocityTimestamp;

    public Chassis()
    {
        wheels[0] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[0], RobotMap.CAN_SWERVE_ANGLE_TALONS[0]);
        wheels[1] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[1], RobotMap.CAN_SWERVE_ANGLE_TALONS[1]);
        wheels[2] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[2], RobotMap.CAN_SWERVE_ANGLE_TALONS[2]);
        wheels[3] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[3], RobotMap.CAN_SWERVE_ANGLE_TALONS[3]);

        lastVelocityTimestamp = Timer.getFPGATimestamp();
    }

    public void update(HerdVector robotVelocity, double robotRotation)
    {
        HerdVector[] WheelsScaled = applyChassisEffects(robotVelocity, robotRotation);

        log.debug("Scaled FR", WheelsScaled[0]);
        log.debug("Scaled FL", WheelsScaled[1]);
        log.debug("Scaled BR", WheelsScaled[2]);
        log.debug("Scaled BL", WheelsScaled[3]);

        updateWheelSettings();
        wheels[0].set(WheelsScaled[0]);
        wheels[1].set(WheelsScaled[1]);
        wheels[2].set(WheelsScaled[2]);
        wheels[3].set(WheelsScaled[3]);

        log.info(wheelNames[0], wheels[0]);
        log.info(wheelNames[1], wheels[1]);
        log.info(wheelNames[2], wheels[2]);
        log.info(wheelNames[3], wheels[3]);
    }

    private HerdVector[] applyChassisEffects(HerdVector robotVelocity, double robotRotation)
    {
        double rotateAdjustMin = prefs.getDouble("DRIVE_MIN_ROTATION", Config.rotationAdjustMin);
        double maxAccel = prefs.getDouble("MAX_ACCELERATION", Config.accelerationMax);

        HerdVector velocity = robotVelocity;
        double spin = robotRotation;

        if (velocity.getMag() > 1)
        {
            log.error("Bad Chassis Command?", velocity);
            velocity = velocity.clampToRange(0, 1);
        }

        spin = applyRotationLimit(velocity, spin, rotateAdjustMin);
        velocity = applyAccelerationLimit(velocity, maxAccel);  // Cap mag accel - Better traction
        state.updateRobotVelocity(velocity);

        log.info("Chassis Final", String.format("V: %s, W: %.2f", velocity, spin));

        return chassisToWheelCommands(velocity, spin);
    }

    private HerdVector[] chassisToWheelCommands(HerdVector velocity, double spin)
    {
        HerdVector[] wheelCommands = new HerdVector[4];
        HerdVector v = new HerdVector(velocity);
        HerdVector w = new HerdVector(spin, 90);

        // v + w x r
        HerdVector wheelFR = v.add(w.cross(Config.rFR));
        HerdVector wheelFL = v.add(w.cross(Config.rFL));
        HerdVector wheelBR = v.add(w.cross(Config.rBR));
        HerdVector wheelBL = v.add(w.cross(Config.rBL));

        double maxMag = wheelFR.getMag();
        maxMag = (wheelFL.getMag() > maxMag) ? wheelFL.getMag(): maxMag;
        maxMag = (wheelBR.getMag() > maxMag) ? wheelBR.getMag(): maxMag;
        maxMag = (wheelBL.getMag() > maxMag) ? wheelBL.getMag(): maxMag;

        if (maxMag > 1)
        {
            wheelFR = wheelFR.scale(1 / maxMag);
            wheelFL = wheelFL.scale(1 / maxMag);
            wheelBR = wheelBR.scale(1 / maxMag);
            wheelBL = wheelBL.scale(1 / maxMag);
        }

        wheelCommands[0] = wheelFR;
        wheelCommands[1] = wheelFL;
        wheelCommands[2] = wheelBR;
        wheelCommands[3] = wheelBL;

        return wheelCommands;
    }

    /** Limit before slowing speed so it runs using the original values set limitations on rotation, so if driving full speed it doesn't take priority **/
    private double applyRotationLimit(HerdVector velocity, double spin, double rotateAdjustMin)
    {
        double RotationAdjust = Math.min(1 - velocity.getMag() + rotateAdjustMin, 1);

        log.info("Chassis RotationAdjust", RotationAdjust);

        //return Utilities.clampToRange(spin, -RotationAdjust, RotationAdjust);
        return spin * RotationAdjust;
    }

    private HerdVector applyAccelerationLimit(HerdVector velocity, double maxAccel)
    {
        double now = Timer.getFPGATimestamp();
        double dt = now - lastVelocityTimestamp;
        double lastMag = state.getRobotVelocity().getMag();
        double maxDelta = maxAccel * dt;
        double min = lastMag - maxDelta;
        double max = lastMag + maxDelta;

        lastVelocityTimestamp = now;

        return velocity.clampToRange(min, max);
    }

    public void updateBrake(boolean enable)
    {
        wheels[0].setBrake(enable);
        wheels[1].setBrake(enable);
        wheels[2].setBrake(enable);
        wheels[3].setBrake(enable);
    }

    public void updateWheelSettings()
    {
        double p = prefs.getDouble("WheelAngle_P", Config.WHEEL_ANGLE_P);
        double i = prefs.getDouble("WheelAngle_I", Config.WHEEL_ANGLE_I);
        double d = prefs.getDouble("WheelAngle_D", Config.WHEEL_ANGLE_D);
        double angleSpeedMax = Preferences.getInstance().getDouble("maxRotationSpeed", Config.WHEEL_ANGLE_SPEED_MAX);

        double angleOffsetCal0 = prefs.getDouble("OffsetFR", 0);
        double angleOffsetCal1 = prefs.getDouble("OffsetFL", 0);
        double angleOffsetCal2 = prefs.getDouble("OffsetBR", 0);
        double angleOffsetCal3 = prefs.getDouble("OffsetBL", 0);

        wheels[0].updateSettings(p, i, d, angleSpeedMax, angleOffsetCal0);
        wheels[1].updateSettings(p, i, d, angleSpeedMax, angleOffsetCal1);
        wheels[2].updateSettings(p, i, d, angleSpeedMax, angleOffsetCal2);
        wheels[3].updateSettings(p, i, d, angleSpeedMax, angleOffsetCal3);
    }
}
