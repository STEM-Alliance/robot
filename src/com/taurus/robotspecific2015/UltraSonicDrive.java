package com.taurus.robotspecific2015;

import com.taurus.UltrasonicMaxBotix;
import com.taurus.controller.Controller;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

public class UltraSonicDrive extends SwerveChassis {
    private UltrasonicMaxBotix ultraAR;
    private UltrasonicMaxBotix ultraAL;
    private UltrasonicMaxBotix ultraBL;
    private UltrasonicMaxBotix ultraBR;
    private double driveRate = .1;
    private static boolean UltraSonic;
    private double distance;
    private static boolean left;

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

    public static void setUltrasonic(boolean x, boolean l)
    {
        UltraSonic = x;
        left = l;
    }

    public void run()
    {
        distance = (ultraBR.getRangeInches() + ultraBL.getRangeInches()) / 2;
        if (UltraSonic && (distance < 48))
        {
            this.UltrasonicLineUp(left);

        }
        else
        {
            super.run();

        }

    }

    public void UltrasonicLineUp(boolean left)
    {
        this.setFieldRelative(false);
        double angleError = setParallelToWall();
        double yvel = 0;
        double xvel = 0;
        if (Math.abs(angleError) < 15)
        {
            yvel = -(distance - 30) * driveRate;
            xvel = alignToChute(left);
        }

        SwerveVector vec = new SwerveVector(xvel, yvel);
        this.UpdateDrive(vec, angleError / 100.0, -1);
    }

    public double setParallelToWall()
    {
        double error =
                Math.atan((ultraBR.getRangeInches() - ultraBL.getRangeInches())
                          / SwerveConstants.ChassisWidth);
        return error;
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
