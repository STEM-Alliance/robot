package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.subsystems.swerve.wheel.AngleMotorMagPot;
import org.wfrobotics.reuse.subsystems.swerve.wheel.SwerveWheel;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;

/**
 * Controls swerve drive at the robot/chassis level
 * @author Team 4818 WFRobotics
 */
public class Chassis
{
    RobotState state = RobotState.getInstance();
    HerdLogger log = new HerdLogger(Chassis.class);

    private final SwerveWheel[] wheels = new SwerveWheel[4];
    private final String[] wheelNames = {"WheelFR", "WheelFL", "WheelBR", "WheelBL"};
    private final Shifter shifters;

    private double lastVelocityTimestamp;

    public Chassis()
    {
        wheels[0] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[0], new AngleMotorMagPot(wheelNames[0] + ".Angle", RobotMap.CAN_SWERVE_ANGLE_TALONS[0]));
        wheels[1] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[1], new AngleMotorMagPot(wheelNames[1] + ".Angle", RobotMap.CAN_SWERVE_ANGLE_TALONS[1]));
        wheels[2] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[2], new AngleMotorMagPot(wheelNames[2] + ".Angle", RobotMap.CAN_SWERVE_ANGLE_TALONS[2]));
        wheels[3] = new SwerveWheel(RobotMap.CAN_SWERVE_DRIVE_TALONS[3], new AngleMotorMagPot(wheelNames[3] + ".Angle", RobotMap.CAN_SWERVE_ANGLE_TALONS[3]));
        shifters = new Shifter();

        lastVelocityTimestamp = Timer.getFPGATimestamp();
    }

    public void update(HerdVector robotVelocity, double robotRotation, boolean gear, boolean brake)
    {
        HerdVector[] WheelsScaled = applyChassisEffects(robotVelocity, robotRotation);

        double angleOffsetCal0 = Preferences.getInstance().getDouble(wheelNames[0] + ".Angle.Offset", 0);
        double angleOffsetCal1 = Preferences.getInstance().getDouble(wheelNames[1] + ".Angle.Offset", 0);
        double angleOffsetCal2 = Preferences.getInstance().getDouble(wheelNames[2] + ".Angle.Offset", 0);
        double angleOffsetCal3 = Preferences.getInstance().getDouble(wheelNames[3] + ".Angle.Offset", 0);

        log.debug("Scaled FR", WheelsScaled[0]);
        log.debug("Scaled FL", WheelsScaled[1]);
        log.debug("Scaled BR", WheelsScaled[2]);
        log.debug("Scaled BL", WheelsScaled[3]);
        
        wheels[0].set(WheelsScaled[0], angleOffsetCal0);
        wheels[1].set(WheelsScaled[1], angleOffsetCal1);
        wheels[2].set(WheelsScaled[2], angleOffsetCal2);
        wheels[3].set(WheelsScaled[3], angleOffsetCal3);

        wheels[0].setBrake(brake);
        wheels[1].setBrake(brake);
        wheels[2].setBrake(brake);
        wheels[3].setBrake(brake);

        log.info(wheelNames[0], wheels[0]);
        log.info(wheelNames[1], wheels[1]);
        log.info(wheelNames[2], wheels[2]);
        log.info(wheelNames[3], wheels[3]);

        shifters.setGear(gear);
        state.updateRobotGear(shifters.isHighGear());
    }

    private HerdVector[] applyChassisEffects(HerdVector robotVelocity, double robotRotation)
    {
        double rotateAdjustMin = Preferences.getInstance().getDouble("DRIVE_MIN_ROTATION", Config.rotationAdjustMin);
        double maxAccel = Preferences.getInstance().getDouble("MAX_ACCELERATION", Config.accelerationMax);

        HerdVector velocity = robotVelocity;
        double spin = robotRotation;

        if (velocity.getMag() > 1)
        {
            log.error("Bad Chassis Command", velocity);
            velocity = velocity.clampToRange(0, 1);             // Clamp mag - Start with range 0 to 1
        }

        velocity = velocity.scale(velocity);                    // Square mag - Finer control at low speed
        spin = (Config.ENABLE_ROTATION_LIMIT) ? applyRotationLimit(velocity, spin, rotateAdjustMin) : spin;
        velocity = applyAccelerationLimit(velocity, maxAccel);  // Cap mag accel - Better traction
        // TODO Scale velocity to range 0 to 1?
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

        wheelCommands[0] = wheelFR.scale(1 / maxMag);
        wheelCommands[1] = wheelFL.scale(1 / maxMag);
        wheelCommands[2] = wheelBR.scale(1 / maxMag);
        wheelCommands[3] = wheelBL.scale(1 / maxMag);

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
}
