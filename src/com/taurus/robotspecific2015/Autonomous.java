package com.taurus.robotspecific2015;

import com.taurus.AutoAction;
import com.taurus.AutoParallel;
import com.taurus.AutoSequence;
import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.AUTO_MODE;
import com.taurus.robotspecific2015.Constants.AUTO_STATE;
import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;
import com.taurus.robotspecific2015.Constants.RAIL_CONTENTS;
import com.taurus.robotspecific2015.Constants.STATE_ADD_CONTAINER_TO_STACK;
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
    
    boolean grabContainerDone = false;
    boolean addContainerFinishedOnce = false;
    
    double ToZoneTime = 5.5;
    double ToLineupTime = .5;
    double LineupTaskTimer = 0;

    public Autonomous(SwerveChassis drive, Lift lift, Vision vision,
            AUTO_MODE automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;
        this.autoMode = automode;

        
        grabContainerDone = false;
        
        switch (autoMode)
        {
            case DO_NOTHING:
                this.autoState = AUTO_STATE.STOP;
                break;
                
            case GO_TO_ZONE:
                autoStateChangeTime = Timer.getFPGATimestamp();
                this.autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                break;

            case GRAB_CONTAINER_GO_TO_ZONE:
            case GRAB_CONTAINER_NO_MOVE:
            case GRAB_CONTAINER_RIGHT_CHUTE:
            case GRAB_CONTAINER_LEFT_CHUTE:
                this.autoState = AUTO_STATE.GRAB_CONTAINER;
                break;
                
            case MOVE_CONTAINER_OUT:
                this.autoState = AUTO_STATE.MOVE_CONTAINER_OUT;
                break;
        }
    }
    
    public void Run()
    {
        SmartDashboard.putString("autoMode", autoMode.toString());
        SmartDashboard.putString("autoState", autoState.toString());
        ToLineupTime = Application.prefs.getDouble("ToLineupTime", ToLineupTime);
        
        switch (autoState)
        {
            case GRAB_CONTAINER:
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                lift.SetAutonomousToteTriggered(true);
                
                if(lift.AddContainerToStack() ||
                        lift.GetStateAddContainerToStack().ordinal() >= STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR.ordinal())
                {
                    this.autoState = AUTO_STATE.GRAB_CONTAINER_WAIT;
                }
                break;
                
            case GRAB_CONTAINER_WAIT:
                if(addContainerFinishedOnce == false)
                {
                    addContainerFinishedOnce = lift.AddContainerToStack();
                }
                
                if (addContainerFinishedOnce || lift.GetContainerInStack() ||
                        lift.GetStateAddContainerToStack().ordinal() >= STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR.ordinal()/*&&
                        lift.GetCar().GetActuator().GetPositionRaw() > LIFT_POSITIONS_E.DESTACK.ordinal()*/)
                {
                    lift.SetAutonomousToteTriggered(false);
                    // wait until the container is 'in the stack' (ie in the top holders)
                    // then move on to the next step
                    switch (autoMode)
                    {
                        case GRAB_CONTAINER_GO_TO_ZONE:
                            // go ahead and start moving to the auto zone (danger zone)
                            autoStateChangeTime = Timer.getFPGATimestamp();
                            autoState = AUTO_STATE.DRIVE_TO_AUTO_ZONE;
                            grabContainerDone = false;
                            lift.SetAutonomousToteTriggered(false);
                            break;
                            
                        case GRAB_CONTAINER_RIGHT_CHUTE:
                        case GRAB_CONTAINER_LEFT_CHUTE:
                            // go ahead and start lining up
                            autoState = AUTO_STATE.LINE_UP;
                            LineupTaskTimer = Timer.getFPGATimestamp();
                            break;
                            
                        case GRAB_CONTAINER_NO_MOVE:
                            // since we're stopping after this,
                            // we need to wait for the container to be finished
                            if (addContainerFinishedOnce)
                            {
                                autoState = AUTO_STATE.STOP;
                                lift.SetAutonomousToteTriggered(false);
                            }
                            break;
                            
                        default:
                            // stop, hammertime
                            autoState = AUTO_STATE.STOP;
                            lift.SetAutonomousToteTriggered(false);
                            break;                        
                    }
                }
                break;
                
            case DRIVE_TO_AUTO_ZONE:
                ToZoneTime = Application.prefs.getDouble("AutoToZoneTime", ToZoneTime);
                if (Timer.getFPGATimestamp() - autoStateChangeTime > ToZoneTime)
                {
                    // stop moving after 4 seconds
                    drive.UpdateDrive(new SwerveVector(), 0, -1);
                    
                    if (grabContainerDone)
                    {
                        // wait until the container is fully done (ie lift at the bottom) before we
                        // stop, collaborate and listen
                        autoState = AUTO_STATE.STOP;
                    }
                    else
                    {
                        grabContainerDone = lift.AddContainerToStack();
                    }
                }
                else
                {
                    switch(autoMode)
                    {
                        case GRAB_CONTAINER_GO_TO_ZONE:
                            // keep adding the container to the stack
                            if(!grabContainerDone)
                            {
                                // but only once, we don't want to add multiple containers
                                // to the same stack
                                grabContainerDone = lift.AddContainerToStack();
                            }
                            break;
                            
                        default:
                            grabContainerDone = true;
                            break;
                    }
                    // and driving to danger zone
                    drive.setCrawlMode(.5);
                    drive.UpdateDrive(SwerveVector.NewFromMagAngle(1, 280), 0, -85);
                    
                }
                break;
                

            case LINE_UP:
                if(!grabContainerDone)
                {
                    // keep grabbing the container if we need to
                    switch (autoMode)
                    {
                        case GRAB_CONTAINER_RIGHT_CHUTE:
                        case GRAB_CONTAINER_LEFT_CHUTE:
                            // if we're grabbing a container, be sure to wait for it to finish
                            grabContainerDone = lift.AddContainerToStack();
                            break;
                            
                        default:
                            grabContainerDone = true;
                            break;
                    }
                }

                double desiredAngle = 0;
                double move = 0;
                boolean moveDone = false;
                
                switch (autoMode)
                {
                    case GRAB_CONTAINER_RIGHT_CHUTE:
                        desiredAngle = -45;
                        move = 1;
                        break;
                        
                    case GRAB_CONTAINER_LEFT_CHUTE:
                        desiredAngle = 45;
                        move = -1;
                        break;
                        
                    default:
                        break;
                }
                
                if(Timer.getFPGATimestamp() - LineupTaskTimer > ToLineupTime)
                {
                    move = 0;
                    moveDone = true;
                }

                // try to get close to the 45
                drive.UpdateDrive(new SwerveVector(move,0), 0, desiredAngle);
                
                // check if we're close
                double gyro = Utilities.wrapToRange(drive.getGyro().getYaw(), -180, 180);
                
                if(Math.abs(gyro - desiredAngle) < 10)
                {
                    if(grabContainerDone && moveDone)
                    {
                        // k, go finish line up now
                        autoState = AUTO_STATE.LINE_UP_ULTRA;
                    }
                }
                
                break;
                
            case LINE_UP_ULTRA:
                // this assumes we're between 12 and 48 inches to the wall
                // and roughly at a 45
                
                // so now tell the drive to line that up
                ((UltraSonicDrive)drive).setUltrasonic(true, -1);
                ((UltraSonicDrive)drive).run();

                // go to chute tote mode
                lift.AddChuteToteToStack(5);
                
                //TODO wait in here?  be done? idk
                
                break;

            case MOVE_CONTAINER_OUT:
                // is this enough?
                ((UltraSonicDrive)drive).setUltrasonic(true, -1);
                ((UltraSonicDrive)drive).run();

                // go to chute tote mode
                lift.AddChuteToteToStack(5);
                
                //TODO idk, something
                
                break;

            default:
            case STOP:
                // go to chute tote mode
                lift.AddChuteToteToStack(5);
                
                // stop driving
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                //lift.GetCar().GetActuator().SetSpeedRaw(0);
                break;
            
        }
    }
}