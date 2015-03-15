package com.taurus.robotspecific2015;

import com.taurus.UltrasonicMaxBotix;
import com.taurus.Utilities;
import com.taurus.controller.Controller;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class UltraSonicDrive extends SwerveChassis {
    private UltrasonicMaxBotix ultraAR;
    private UltrasonicMaxBotix ultraAL;
    private UltrasonicMaxBotix ultraBL;
    private UltrasonicMaxBotix ultraBR;
    
    private double driveRate = .6;
    
    private static boolean UltraSonic;
    private double distance;
    private static boolean left;
    
    private static final double DISTANCE_FROM_WALL = 24.0;

    public UltraSonicDrive(Controller controller)
    {
        super(controller);
        ultraAR =
                new UltrasonicMaxBotix(
                        Constants.ULTRASONIC_SENSOR_ANGLE_RIGHT[0],
                        Constants.ULTRASONIC_SENSOR_ANGLE_RIGHT[1]);
        ultraAL =
                new UltrasonicMaxBotix(
                        Constants.ULTRASONIC_SENSOR_ANGLE_LEFT[0],
                        Constants.ULTRASONIC_SENSOR_ANGLE_LEFT[1]);
        ultraBL =
                new UltrasonicMaxBotix(
                        Constants.ULTRASONIC_SENSOR_BACK_LEFT[0],
                        Constants.ULTRASONIC_SENSOR_BACK_LEFT[1]);
        ultraBR =
                new UltrasonicMaxBotix(
                        Constants.ULTRASONIC_SENSOR_BACK_RIGHT[0],
                        Constants.ULTRASONIC_SENSOR_BACK_RIGHT[1]);

        ultraAR.setAutomaticMode(true);
        ultraAL.setAutomaticMode(true);
        ultraBL.setAutomaticMode(true);
        ultraBR.setAutomaticMode(true);
    }
    
    public void setUltrasonic(boolean x, boolean l)
    {
        UltraSonic = x;
        left = l;
    }

    public void run()
    {
        SmartDashboard.putNumber("Back Left Distance Sensor", ultraBL.getRangeInches());
        SmartDashboard.putNumber("Back Right Distance Sensor", ultraBR.getRangeInches());

        distance = (ultraBR.getRangeInches() + ultraBL.getRangeInches()) / 2;
        SmartDashboard.putNumber("UltraSonicSensor Back Left", ultraBL.getRangeInches());
        SmartDashboard.putNumber("UltraSonicSensor Back Right", ultraBR.getRangeInches());
        SmartDashboard.putNumber("UltraSonicSensor Angle Left", ultraAL.getRangeInches());
        SmartDashboard.putNumber("UltraSonicSensor Angle Right", ultraAR.getRangeInches());

        if (UltraSonic/*controller.getUltrasonicLineup()*/ && (distance < 48))
        {
            SmartDashboard.putBoolean("Lineup", true);
            this.UltrasonicLineUp(true);  // Drive based on sensors
        }
        else
        {
            SmartDashboard.putBoolean("Lineup", false);
            super.run();  // Normal swerve drive
        }
    }

    public void UltrasonicLineUp(boolean left)
    {
        this.setFieldRelative(false);
        double angleError = setParallelToWall();
        double yvel = 0;
        double xvel = 0;
        if (Math.abs(angleError) < 10)
        {
            double distanceFromDesired = (distance - DISTANCE_FROM_WALL);
            yvel = Utilities.clampToRange(distanceFromDesired, -1, 1) * driveRate;
            //xvel = alignToChute(left);
        }
        
        SmartDashboard.putNumber("LineupY", yvel);
        SmartDashboard.putNumber("LineupAngle", angleError);

        SwerveVector vec = new SwerveVector(xvel, yvel);
        this.UpdateDrive(vec, angleError / 60.0, -1);
    }

    public double setParallelToWall()
    {
        double error =
                Math.atan((ultraBR.getRangeInches() - ultraBL.getRangeInches())
                          / SwerveConstants.ChassisWidth);
        return Math.toDegrees(error);
    }

    public double alignToChute(boolean left)
    {
        double xvel = 0;
        double xerror = 0;
        if (left)
        {
            xerror = ultraAR.getRangeInches() * Math.sin(Math.PI / 4);
            xvel = (xerror - 12) * driveRate;
        }
        else
        {
            xerror = ultraAL.getRangeInches() * Math.sin(Math.PI / 4);
            xvel = -(xerror - 12) * driveRate;
        }
        return xvel;
    }

}
