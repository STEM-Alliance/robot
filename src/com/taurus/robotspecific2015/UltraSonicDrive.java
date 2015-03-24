package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.controller.Controller;
import com.taurus.sensors.MaxBotixAnalog;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class UltraSonicDrive extends SwerveChassis {
//    private MaxBotixDigital ultraAR;
//    private MaxBotixDigital ultraAL;
    private MaxBotixAnalog ultraBL;
    private MaxBotixAnalog ultraBR;
    
    private double driveRate = .6;
    
    private static boolean leftSide;
    private static boolean rightSide;
    private static double dpad;
    
    private double DISTANCE_FROM_WALL = 24.0;
    
    private double DISTANCE_PRECISION = 1.0; 
    
    private double ANGLE_PRECISION = 10.0;

    public UltraSonicDrive(Controller controller)
    {
        super(controller);
//        ultraAR =
//                new MaxBotixDigital(
//                        Constants.ULTRASONIC_SENSOR_ANGLE_RIGHT[0],
//                        Constants.ULTRASONIC_SENSOR_ANGLE_RIGHT[1]);
//        ultraAL =
//                new MaxBotixDigital(
//                        Constants.ULTRASONIC_SENSOR_ANGLE_LEFT[0],
//                        Constants.ULTRASONIC_SENSOR_ANGLE_LEFT[1]);

        
        ultraBL = new MaxBotixAnalog(Constants.ULTRASONIC_SENSOR_BACK_LEFT[0],
                Constants.ULTRASONIC_SENSOR_BACK_LEFT[1]);
        
        ultraBR = new MaxBotixAnalog(Constants.ULTRASONIC_SENSOR_BACK_RIGHT[0],
                Constants.ULTRASONIC_SENSOR_BACK_RIGHT[1]);
                
                
//        ultraAR.setAutomaticMode(true);
//        ultraAL.setAutomaticMode(true);
        ultraBL.setAutomaticMode(true);
        ultraBR.setAutomaticMode(true);
    }
    
    /**
     * Set drive mode to ultrasonic lineup vs normal drive mode
     * @param leftSide
     * @param rightSide
     * @param dpad
     */
    public void setUltrasonic(boolean leftSide, boolean rightSide, double dpad)
    {
        UltraSonicDrive.rightSide = rightSide;
        UltraSonicDrive.leftSide = leftSide;
        UltraSonicDrive.dpad = dpad;
    }

    /**
     *  Call to service the drive
     */
    public void run()
    {
        double AngleError = getAngleToWall();
        
        double AverageDistanceFromWall = (ultraBR.getRangeInches() + ultraBL.getRangeInches()) / 2;
        
        SmartDashboard.putNumber("Lineup Angle", AngleError);
        
        SmartDashboard.putNumber("Lineup Back Left", ultraBL.getRangeInches());
        SmartDashboard.putNumber("Lineup Back Right", ultraBR.getRangeInches());
        
//        SmartDashboard.putNumber("UltraSonicSensor Angle Left", ultraAL.getRangeInches());
//        SmartDashboard.putNumber("UltraSonicSensor Angle Right", ultraAR.getRangeInches());
        

        if ((leftSide || rightSide) && (AverageDistanceFromWall < 48))
        {
            SmartDashboard.putBoolean("Lineup", true);
            
            this.UltrasonicLineUp(AngleError, AverageDistanceFromWall - DISTANCE_FROM_WALL);  // Drive based on sensors
        }
        else
        {
            SmartDashboard.putBoolean("Lineup", false);
            super.run();  // Normal swerve drive
        }
    }

    /**
     * Determine how to update swerve drive
     * @param left
     */
    public void UltrasonicLineUp(double AngleError, double DistanceError)
    {
        
        double yvel = 0;
        double xvel = 0;
        double rotation = 0;
        
        //TODO convert to using a PID controller
        if (Math.abs(AngleError) < ANGLE_PRECISION)
        {
            // we're close to the correct angle
            if(Math.abs(DistanceError) > DISTANCE_PRECISION)
            {
                yvel = Utilities.clampToRange(DistanceError, -1, 1) * driveRate;
            }
            
            // enable if using the angle sensors to move left and right
            //xvel = alignToChute(left);
            
            // we're close to the wall, do we need to keep adjusting angle?
            //rotation = AngleError / 60.0;
        }
        else
        {
            // too far off of the angle, so change rotation
            rotation = (AngleError / 60.0);
        }
        
        // only move in the x direction if the dpad is being pressed
        if(dpad == 90)
        {
            xvel = 1 * driveRate;
        }
        else if(dpad == 270)
        {
            xvel = -1 * driveRate;
        }

        SmartDashboard.putNumber("Lineup Y", yvel);
        SmartDashboard.putNumber("Lineup X", xvel);
        SmartDashboard.putNumber("Lineup Rotation", rotation);

        SwerveVector vec = new SwerveVector(xvel, yvel);
        
        rotation = Utilities.clampToRange(rotation, -1, 1);
        
        this.setFieldRelative(false);
        this.UpdateDrive(vec, rotation, -1);
        this.setFieldRelative(true);
    }

    public double getAngleToWall()
    {
        // use inverse tangent here
        double error = Math.atan((ultraBR.getRangeInches() - ultraBL.getRangeInches())
                          / SwerveConstants.ChassisWidth);
        return Math.toDegrees(error);
    }

    public double alignToChute(boolean left)
    {
        double xvel = 0;
        double xerror = 0;
        if (left)
        {
            //xerror = ultraAR.getRangeInches() * Math.sin(Math.PI / 4);
            xvel = (xerror - 12) * driveRate;
        }
        else
        {
            //xerror = ultraAL.getRangeInches() * Math.sin(Math.PI / 4);
            xvel = -(xerror - 12) * driveRate;
        }
        return xvel;
    }

}
