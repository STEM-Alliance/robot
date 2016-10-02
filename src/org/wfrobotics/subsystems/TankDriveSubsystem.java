package org.wfrobotics.subsystems;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveTank;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.robot.RobotMap;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TankDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = 0;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.CAN_TANK_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.CAN_TANK_TALONS_RIGHT.length];
    private Gyro navxMXP;  // Expander board, contains gyro
    
    private double desiredHeading;
    private PIDController headingPID;
    
    private final double HEADING_TOLERANCE = 1.5;
    
    /**
     * Constructor
     */
    public TankDriveSubsystem()
    {
        
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.CAN_TANK_TALONS_LEFT[i]);
        }
        
        // setup right side motors
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i] = new CANTalon(RobotMap.CAN_TANK_TALONS_RIGHT[i]);

            // since the right side rotation is inverted from the left, set that in the controller
            motorsR[i].setInverted(true);
        }
        
        // Setup gyro
        try {
            /* Communicate w/navX MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
            navxMXP = Gyro.getInstance();
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        
        headingPID = new PIDController(.2, 0, 0, .5);
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveTank(false));
    }
    
    /**
     * Update the PID values if they changed from Preferences
     */
    private void updatePID()
    {
        headingPID.setP(Preferences.getInstance().getDouble("HeadingPID_P", .2));
        headingPID.setI(Preferences.getInstance().getDouble("HeadingPID_I", 0));
        headingPID.setD(Preferences.getInstance().getDouble("HeadingPID_D", 0));
        headingPID.setMin(Preferences.getInstance().getDouble("HeadingPID_Min", .1));
    }

    /**
     * Raw drive, controlling each wheel separately
     * @param right value, -1 to 1
     * @param left value, -1 to 1
     * @param enables traction control
     */
    public void driveRaw(double right, double left)
    {
        
        printSensors();
        right = scaleForDeadband(right);
        right = Math.min(Math.max(right, -1), 1);  // ensure value between -1 and 1
        
        
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i].set(right);
        }
        
        left = scaleForDeadband(left);
        left = Math.min(Math.max(left, -1), 1);  // ensure value between -1 and 1
        
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i].set(left);   
        }
        SmartDashboard.putNumber("MotorL", left);
        SmartDashboard.putNumber("MotorR", right);
        
    }
    

    /**
     * turn to an angle, offset from current heading
     * @param angle
     * @return true if done
     */
    public boolean turnToAngleOffset(double angle)
    {
        return turnToAngle(navxMXP.getYaw() + angle);
    }
    
    /**
     * turn to a desired angle
     * @param angle
     * @return true if done
     */
    public boolean turnToAngle(double angle)
    {
        return turnToAngle(angle, false, false);
    }

    
    /**
     * turn to a desired angle, with option to use only one side and only forward or backward
     * @param angle
     * @return true if done
     */
    public boolean turnToAngle(double angle, boolean singleSide, boolean forward)
    {
        double output = 0;
        double error = Utilities.wrapToRange(angle - navxMXP.getYaw(), -180, 180);
        
        updatePID();
        
        if(Math.abs(error) > HEADING_TOLERANCE)
        {
            // get the suggested motor output
            output = headingPID.update(error);
            
            // tell it to turn
            if(error < 3 && singleSide)
            {
                //TODO need brake mode enabled?
                
                // small error, so only try and turn one side
                if(output > 0)
                {
                    // spin clockwise
                    if(forward)
                        // set left side forward, not right
                        driveRaw(0,output);
                    else
                        // set right side backward, not left
                        driveRaw(-output,0);
                }
                else
                {
                    // spin counter clockwise
                    if(forward)
                        // set right side forward, not left
                        driveRaw(-output,0);
                    else
                        // set left side backward, not right
                        driveRaw(0,output);
                }
            }
            else
            {
                driveRaw(-output,output);
            }
        }
        else
        {
            driveRaw(0,0);
        }
        
        // save off angle in case you need to use it
        desiredHeading = angle;
        
        // we're done if we're at the angle, and we're no longer turning
        return Math.abs(error) < HEADING_TOLERANCE;// && Math.abs(output) < .1;
    }
        
    public void zeroGyro(double angle)
    {
        navxMXP.setZero(angle);
    }

    /**
     * Apply a deadband.
     * 
     * The value returned is zero or scaled relative to the deadband 
     * to retain sensitivity.
     * @param value -1 to 1
     * @return value with deadband applied or scaled
     */
    private double scaleForDeadband(double value)
    {
        double abs = Math.abs(value);
        
        if (abs < DEADBAND)
        {
            value = 0;
        }
        else
        {
            value = Math.signum(value) * ((abs - DEADBAND) / (1 - DEADBAND));
        }
        
        return value;
    }    

    public void printSensors()
    {
        SmartDashboard.putNumber("Robot Heading", navxMXP.getYaw());
        SmartDashboard.putNumber("Desired Heading", desiredHeading);

        SmartDashboard.putString("RobotPositionInfo", navxMXP.getYaw() +"," + motorsL[0].get()  +"," + motorsR[0].get());
    }

    public double getYaw()
    {
        return navxMXP.getYaw();
    }
    
    public void zeroYaw(double angle)
    {
        navxMXP.setZero(angle);
    }
    
    public void setBrakeMode(boolean enable)
    {
        for (int index = 0; index < motorsL.length; index++)
        {
            motorsL[index].enableBrakeMode(enable);
        }
        
        for (int index = 0; index < motorsR.length; index++)
        {
            motorsR[index].enableBrakeMode(enable);
        }
    }
}
            
            
    

