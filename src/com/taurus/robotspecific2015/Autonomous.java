package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER;
import com.taurus.robotspecific2015.Constants.*;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous 
{
    private SwerveChassis drive;
    private Lift lift;
    private Vision vision;
    private int automode;
    
    private double AutoStateTime;
    
    private AUTO_STATE_MACHINE_FORWARD AutoStateForward;
    private AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE AutoStateFindAndGrabTote;
    private AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER AutoStateFindAndGrabContainer;
    
    public Autonomous(SwerveChassis drive, Lift lift, Vision vision, int automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;
        this.automode = automode;
        
        drive.ZeroGyro();
        drive.SetGyroZero(-90); // starting facing left, so fix offset

        AutoStateTime = Timer.getFPGATimestamp();
        
        AutoStateForward = Constants.AUTO_STATE_MACHINE_FORWARD.AUTO_START;
        AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.AUTO_START;
    }
    
    public void Run()
    {
        switch (automode)
        {
            default:
            case 0:                
                break;
                
            case 1:
                AutoGoToScoringZone(-1);
                break;
            
            case 2:
                AutoGrab1ToteAndGoToScoringZone();
                break;
                
            case 3:
                AutoGrabContainerAndGoToScoringZone();
                break;
                
            case 4:
                AutoGrab2TotesMovingLeftAndGoToScoringZone();
                break;
        }
    }
    
    /**
     * Just drive forward for a few seconds
     */
    private void AutoGoToScoringZone(double Heading)
    {
        double DriveTime = 2; //TODO find exact numbers to get into scoring zone

        switch (AutoStateForward)
        {
            case AUTO_START:
                AutoStateTime = Timer.getFPGATimestamp();
                AutoStateForward = AUTO_STATE_MACHINE_FORWARD.AUTO_GO;
                break;

            case AUTO_GO:
                SwerveVector Velocity = new SwerveVector(0, 1);

                // TODO pick values
                drive.setGearHigh(true);
                drive.UpdateDrive(Velocity, 0, Heading);
                if (Timer.getFPGATimestamp() > AutoStateTime + DriveTime)
                {
                    AutoStateForward = AUTO_STATE_MACHINE_FORWARD.AUTO_END;
                }
                break;

            case AUTO_END:
            default:
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                break;
        }

    }

    /**
     * Use the vision to find the tote and drive towards it
     */
    private void AutoGrab1ToteAndGoToScoringZone()
    {
        switch (AutoStateFindAndGrabTote)
        {
            case AUTO_START:
                AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.AUTO_FIND;
                drive.SetGyroZero(-90);  // starting facing left
                break;
                
            case AUTO_FIND:
                AutoFindTote();
                lift.AddFloorToteToStack();
                
                if (lift.GetToteIntakeSensor().IsOn())
                {
                    AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.AUTO_MOVE_TO_SCORING_AREA;
                }
                break;
                
            case AUTO_MOVE_TO_SCORING_AREA:
                AutoGoToScoringZone(270);
                lift.AddFloorToteToStack();
                break;
        }

    }
    
    private void AutoGrabContainerAndGoToScoringZone()
    {
        switch (AutoStateFindAndGrabContainer)
        {
            case AUTO_START:
                AutoStateFindAndGrabContainer = AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER.AUTO_FIND;
                drive.SetGyroZero(90);  // starting facing right
                break;
                
            case AUTO_FIND:
                // TODO: choose velocity
                drive.UpdateDrive(new SwerveVector(.5, 0), 0, 90);
                
                lift.AddContainerToStack();
                
                if (lift.GetToteIntakeSensor().IsOn())
                {
                    AutoStateFindAndGrabContainer = AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER.AUTO_GRABBED;
                }
                break;
                
            case AUTO_GRABBED:
                AutoGoToScoringZone(90);
                
                lift.AddContainerToStack();
                break;
        }
    }
    
    private void AutoGrab2TotesMovingLeftAndGoToScoringZone()
    {
        switch (AutoStateFindAndGrabTote)
        {
            case AUTO_START:
                AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.AUTO_FIND;
                drive.SetGyroZero(-90);   // starting facing left
                break;
                
            case AUTO_FIND:
                AutoFindTote();
                lift.AddFloorToteToStack();
                
                if (lift.GetToteIntakeSensor().IsOn())
                {
                    AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.AUTO_MOVE_TO_SCORING_AREA;
                }
                break;
                
            case AUTO_MOVE_TO_SCORING_AREA:
                SwerveVector Velocity = new SwerveVector(); // TODO
                drive.UpdateDrive(Velocity, 0, 270);
                lift.AddFloorToteToStack();
                break;
        }
    }
    
    private void AutoFindTote()
    {
        double maxSlide = Application.prefs.getDouble("MaxSlide", 1);
        double slideP = Application.prefs.getDouble("SlideP", 1);
        double targetX = Application.prefs.getDouble("SlideTargetX", .5);
        double forwardSpeed = Application.prefs.getDouble("AutoSpeed", .5);

        SwerveVector Velocity;

        if (vision.getToteSeen())
        {
            Velocity = new SwerveVector(-forwardSpeed, Utilities.clampToRange(
                    (vision.getResultX() - targetX) * slideP, -maxSlide,
                    maxSlide));
        }
        else
        {
            Velocity = new SwerveVector(-forwardSpeed, 0);
        }

        double Rotation = 0;
        double Heading = 270;

        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());

        drive.setGearHigh(false);
        drive.UpdateDrive(Velocity, Rotation, Heading);
    }

    
}
