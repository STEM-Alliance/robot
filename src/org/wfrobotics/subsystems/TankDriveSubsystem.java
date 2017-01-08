package org.wfrobotics.subsystems;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TankDriveSubsystem extends DriveSubsystem 
{
    private final double DEADBAND = 0;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.CAN_TANK_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.CAN_TANK_TALONS_RIGHT.length];
    
    private double m_lastHeading;
    private PIDController headingPID;
    
    private final double HEADING_TOLERANCE = 1.5;
    
    /**
     * Constructor
     */
    public TankDriveSubsystem()
    {
        super();
        
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.CAN_TANK_TALONS_LEFT[i]);
            motorsL[i].setInverted(true);
        }
        
        // setup right side motors
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i] = new CANTalon(RobotMap.CAN_TANK_TALONS_RIGHT[i]);
            motorsR[i].setInverted(false);
        }
                
        headingPID = new PIDController(.2, 0, 0, .5);
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveTankArcade());
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
    @Override
    public void driveTank(double right, double left)
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

    @Override
    public void driveVector(double magnitude, double angle, double rotation)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void driveVector(Vector velocity, double rotation)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void driveXY(double x, double y, double rotation)
    {
        // TODO Auto-generated method stub
        
    }
    

    /**
     * turn to an angle, offset from current heading
     * @param angle
     * @return true if done
     */
    public boolean turnToAngleOffset(double angle)
    {
        return turnToAngle(m_gyro.getYaw() + angle);
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
        double error = Utilities.wrapToRange(angle - m_gyro.getYaw(), -180, 180);
        
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
                        driveTank(0,output);
                    else
                        // set right side backward, not left
                        driveTank(-output,0);
                }
                else
                {
                    // spin counter clockwise
                    if(forward)
                        // set right side forward, not left
                        driveTank(-output,0);
                    else
                        // set left side backward, not right
                        driveTank(0,output);
                }
            }
            else
            {
                driveTank(-output,output);
            }
        }
        else
        {
            driveTank(0,0);
        }
        
        // save off angle in case you need to use it
        m_lastHeading = angle;
        
        // we're done if we're at the angle, and we're no longer turning
        return Math.abs(error) < HEADING_TOLERANCE;// && Math.abs(output) < .1;
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
        SmartDashboard.putNumber("Robot Heading", m_gyro.getYaw());
        SmartDashboard.putNumber("Desired Heading", m_lastHeading);

        SmartDashboard.putString("RobotPositionInfo", m_gyro.getYaw() +"," + motorsL[0].get()  +"," + motorsR[0].get());
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
            
            
    

