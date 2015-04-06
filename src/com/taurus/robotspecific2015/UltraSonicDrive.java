package com.taurus.robotspecific2015;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.controller.Controller;
import com.taurus.sensors.MaxBotixAnalog;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class UltraSonicDrive extends SwerveChassis {
//    private MaxBotixDigital ultraAR;
//    private MaxBotixDigital ultraAL;
    private MaxBotixAnalog ultraBL;
    private MaxBotixAnalog ultraBR;
    
    private PIDController distancePID;
    private double distanceP = .25;
    private double distanceI = .3;
    private double distanceD = 0.0;
    
    private double driveRate = .6;
    
    private static boolean lineup;
    private static double dpad;
    
    private double DISTANCE_FROM_WALL = 22.5;
    
    private double DISTANCE_PRECISION = 2.0; 
    
    private double ANGLE_PRECISION = 20.0;

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
        
        distancePID = new PIDController(distanceP, distanceI, distanceD, 1);
    }
    
    /**
     * Set drive mode to ultrasonic lineup vs normal drive mode
     * @param leftSide
     * @param rightSide
     * @param dpad
     */
    public void setUltrasonic(boolean lineup, double dpad)
    {
        UltraSonicDrive.lineup = lineup;
        UltraSonicDrive.dpad = dpad;
    }

    /**
     *  Call to service the drive
     */
    public void run()
    {
        double AngleError = getAngleToWall();
        
        double AverageDistanceFromWall = (ultraBR.getRangeInchesAverage() + ultraBL.getRangeInchesAverage()) / 2;
        
//        SmartDashboard.putNumber("Lineup Angle", AngleError);
//
//        SmartDashboard.putNumber("Lineup Back Left", ultraBL.getRangeInchesAverage());
//        SmartDashboard.putNumber("Lineup Back Right", ultraBR.getRangeInchesAverage());
//        SmartDashboard.putNumber("Lineup Back Left Raw", ultraBL.getRangeInches());
//        SmartDashboard.putNumber("Lineup Back Right Raw", ultraBR.getRangeInches());
        
//        SmartDashboard.putNumber("UltraSonicSensor Angle Left", ultraAL.getRangeInches());
//        SmartDashboard.putNumber("UltraSonicSensor Angle Right", ultraAR.getRangeInches());
        

        if (lineup && (AverageDistanceFromWall < 48))
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
        double heading = -1;
        
        //TODO convert to using a PID controller
        if (Math.abs(AngleError) < ANGLE_PRECISION)
        {
            // we're close to the correct angle
            if(Math.abs(DistanceError) > DISTANCE_PRECISION)
            {
                yvel = distancePID.update(DistanceError, Timer.getFPGATimestamp());
                //yvel = Utilities.clampToRange(DistanceError, -1, 1) * driveRate;
            }
            
            // enable if using the angle sensors to move left and right
            //xvel = alignToChute(left);
            
            // we're close to the wall, do we need to keep adjusting angle?
            //rotation = AngleError / 100.0;
        }
        else
        {
            // too far off of the angle, so change rotation
            //rotation = (AngleError / 60.0);
        }
        
        //rotationPID.update(AngleError, Timer.getFPGATimestamp());
        
        // only move in the x direction if the dpad is being pressed
        if(dpad == 90)
        {
            xvel = 1;
        }
        else if(dpad == 270)
        {
            xvel = -1;
        }

        yvel *= driveRate;
        
//        SmartDashboard.putNumber("Lineup Y", yvel);
//        SmartDashboard.putNumber("Lineup X", xvel);
//        SmartDashboard.putNumber("Lineup Rotation", rotation);

        SwerveVector vec = new SwerveVector(xvel, yvel);
        
        rotation = Utilities.clampToRange(rotation, -1, 1);

        heading = super.getGyro().getYaw() + AngleError;
        
//        SmartDashboard.putNumber("Lineup Heading", heading);
        
        this.setFieldRelative(false);
        
        //this.CrawlMode = 1.0;
        this.RotationRateAdjust = 2.5;
        
        this.UpdateDrive(vec, rotation, heading);
        this.setFieldRelative(true);
        
        this.RotationRateAdjust = 1;
    }

    public double getAngleToWall()
    {
        // use inverse tangent here
        double error = Math.atan((ultraBR.getRangeInchesAverage() - ultraBL.getRangeInchesAverage())
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
