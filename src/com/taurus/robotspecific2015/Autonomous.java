package com.taurus.robotspecific2015;

import com.taurus.AutoAction;
import com.taurus.AutoParallel;
import com.taurus.AutoSequence;
import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.AUTO_MODE;
import com.taurus.robotspecific2015.Constants.AUTO_STATE;
import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;
import com.taurus.robotspecific2015.Constants.RAIL_CONTENTS;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

    private SwerveChassis drive;
    private Lift lift;
    private Vision vision;

    AUTO_MODE autoMode;
    AUTO_STATE autoState;

    boolean dropSmallStack = true;
    private double autoStateChangeTime;

    public Autonomous(SwerveChassis drive, Lift lift, Vision vision,
            AUTO_MODE automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;
        this.autoMode = automode;

        drive.ZeroGyro();
        
        switch (autoMode)
        {
            case DO_NOTHING:
                this.autoState = AUTO_STATE.STOP;
                break;
                
            case GO_TO_ZONE:
                autoStateChangeTime = Timer.getMatchTime();
                this.autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                break;
                
            case GRAB_CONTAINER:
            case GRAB_CONTAINER_AND_LINE_UP:
            case GRAB_CONTAINER_AND_1_TOTE:
            case GRAB_CONTAINER_AND_2_TOTES:
                drive.SetGyroZero(90);
                this.autoState = AUTO_STATE.GRAB_CONTAINER;
                break;
        }
    }
    
    public void Run()
    {
        switch (autoState)
        {
            case GRAB_CONTAINER:
                drive.UpdateDrive(new SwerveVector(), 0, 90);
                lift.AddContainerToStack();
                
                if (lift.GetContainerInStack())
                {
                    switch (autoMode)
                    {
                        case GRAB_CONTAINER:
                            autoStateChangeTime = Timer.getMatchTime();
                            autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                            break;
                            
                        case GRAB_CONTAINER_AND_1_TOTE:
                        case GRAB_CONTAINER_AND_2_TOTES:
                            autoStateChangeTime = Timer.getMatchTime();
                            autoState = AUTO_STATE.DRIVE_RIGHT_TO_GRAB_TOTE;
                            break;
                            
                        case GRAB_CONTAINER_AND_LINE_UP:
                            autoState = AUTO_STATE.LINE_UP;
                            break;
                            
                        default:
                            autoState = AUTO_STATE.STOP;
                            break;                        
                    }
                }
                break;
                
            case DRIVE_RIGHT_TO_GRAB_TOTE:
                if (Timer.getMatchTime() - autoStateChangeTime > 1)
                {
                    autoState = AUTO_STATE.GRAB_RIGHT_SIDE_TOTE;
                }
                else
                {
                    drive.UpdateDrive(new SwerveVector(1, 0), 0, 90);
                    lift.AddFloorToteToStack(0);
                }
                break;
                
            case GRAB_RIGHT_SIDE_TOTE:
                lift.SetAutonomousToteTriggered(true);
                drive.UpdateDrive(new SwerveVector(), 0, 90);

                if (autoMode == AUTO_MODE.GRAB_CONTAINER_AND_1_TOTE)
                {
                    if (lift.AddFloorToteToStack(0))
                    {
                        lift.SetAutonomousToteTriggered(false);
                        autoStateChangeTime = Timer.getMatchTime();
                        autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                    }   
                }
                else if (autoMode == AUTO_MODE.GRAB_CONTAINER_AND_2_TOTES)
                {
                    lift.AddFloorToteToStack(1);
                    
                    if (lift.GetTotesInStack() > 0 
                            && lift.GetCar().GetActuator().GetPositionRaw() <= LIFT_POSITIONS_E.DESTACK.ordinal())
                    {
                        lift.SetAutonomousToteTriggered(false);
                        autoStateChangeTime = Timer.getMatchTime();
                        autoState = AUTO_STATE.DRIVE_LEFT_TO_GRAB_TOTE;
                    }
                }
                else
                {
                    autoState = AUTO_STATE.STOP;
                }
                break;
                
            case DRIVE_LEFT_TO_GRAB_TOTE:
                if (Timer.getMatchTime() - autoStateChangeTime > 1)
                {
                    autoState = AUTO_STATE.GRAB_LEFT_SIDE_TOTE;
                }
                else
                {
                    drive.UpdateDrive(new SwerveVector(-1, 0), 0, 180);
                    lift.AddFloorToteToStack(1);
                }
                break;
                
            case GRAB_LEFT_SIDE_TOTE:
                lift.SetAutonomousToteTriggered(true);
                drive.UpdateDrive(new SwerveVector(), 0, 180);
                    
                if (lift.AddFloorToteToStack(1))
                {
                    lift.SetAutonomousToteTriggered(false);
                    autoStateChangeTime = Timer.getMatchTime();
                    autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                }
                break;

            case DRIVE_TO_AUTO_ZONE:
                if (Timer.getMatchTime() - autoStateChangeTime > 2)
                {
                    autoState = AUTO_STATE.STOP;
                }
                else
                {
                    drive.UpdateDrive(new SwerveVector(0, 1), 0, -1);
                }
                break;
                
            case LOWER_TOTES:
                autoState = AUTO_STATE.STOP;
                break;
                
            case DROP_TOTES:
                autoState = AUTO_STATE.STOP;
                break;
                
            case BACK_UP:
                autoState = AUTO_STATE.STOP;
                break;

            case LINE_UP:
                drive.UpdateDrive(new SwerveVector(), 0, 45);
                lift.AddChuteToteToStack(0);
                break;
                
            default:
            case STOP:
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                lift.GetCar().GetActuator().SetSpeedRaw(0);
                break;
            
        }
    }

//    private class DriveBackwardsToLineUpTote implements AutoAction {
//
//        @Override
//        public boolean execute()
//        {
//            SmartDashboard.putString("AutoState", "DriveBackwardsToLineUpTote");
//
//            double maxSlide = Application.prefs.getDouble("MaxSlide", 1);
//            double slideP = Application.prefs.getDouble("SlideP", 2);
//            double targetX = Application.prefs.getDouble("SlideTargetX", .55);
//            double xErrorThreshold =
//                    Application.prefs.getDouble("SlideTargetXError", .05);
//
//            SwerveVector Velocity;
//            boolean Finished = false;
//
//            if (vision.getToteSeen())
//            {
//                double xError = vision.getResultX() - targetX;
//
//                if (Math.abs(xError) < xErrorThreshold)
//                {
//                    Finished = true;
//                }
//
//                double slideVelocity =
//                        Utilities.clampToRange(xError * slideP, -maxSlide,
//                                maxSlide);
//
//                Velocity = new SwerveVector(0, slideVelocity);
//
//            }
//            else
//            {
//                Velocity = new SwerveVector(0, -maxSlide);
//            }
//
//            drive.UpdateDrive(Velocity, 0, 270);
//
//            return Finished;
//        }
//    }

}
