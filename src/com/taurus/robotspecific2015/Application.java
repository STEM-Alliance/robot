package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.swerve.SwerveVector;
import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Application extends com.taurus.Application
{
    private Vision vision = new Vision();
    private SendableChooser autoChooser;
    
    private double AutoStateTime;
    private AUTO_STATE_MACHINE_L AutoStateL;
    private AUTO_STATE_MACHINE_FORWARD AutoStateForward;
    private AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE AutoStateFindAndGrabTote;
    private AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER AutoStateFindAndGrabContainer;
    
    private STATE_LIFT_ACTION CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
    
    private Lift lift;
    
    public Application()
    {
        super();

        lift = new Lift();
        vision = new Vision();
        
        vision.Start();
        
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Do nothing", Integer.valueOf(0));
        autoChooser.addObject("Push tote", Integer.valueOf(1));
        autoChooser.addObject("Grab tote", Integer.valueOf(2));
        autoChooser.addObject("Grab container", Integer.valueOf(3));
        autoChooser.addObject("Grab 2 totes moving left", Integer.valueOf(4));
        autoChooser.addObject("Grab 2 totes moving right", Integer.valueOf(5));
        autoChooser.addObject("Grab container, 2 totes middle", Integer.valueOf(6));
        autoChooser.addObject("Grab container 2 totes side", Integer.valueOf(7));
        autoChooser.addObject("Container + tote", Integer.valueOf(8));
        autoChooser.addObject("3 totes", Integer.valueOf(9));
        autoChooser.addObject("Container + 3 totes", Integer.valueOf(10));
        
        SmartDashboard.putData("Autonomous mode", autoChooser);
    }
    
    public void TeleopInitRobotSpecific()
    {

    }

    public void TeleopPeriodicRobotSpecific()
    {
        // TODO: Do we want a button to cancel the current routine?
        
        // if not currently running anything, try and find a button
        // do this first so we can run the action this run rather than
        // waiting until the next time this task is ran
        if(CurrentLiftAction == STATE_LIFT_ACTION.NO_ACTION)
        {
            // find if a button is pressed, then execute
            if (controller.getAddChuteTote())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_CHUTE_TOTE;
            }
            else if (controller.getAddFloorTote())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_FLOOR_TOTE;
            }
            else if (controller.getAddContainer())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_CONTAINER;
            }
            else if (controller.getEjectStack())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.EJECT_STACK;
            }
        }

        switch(CurrentLiftAction)
        {
            case ADD_CHUTE_TOTE:
                if(lift.AddChuteToteToStack())
                {
                    CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                }
                break;
                
            case ADD_FLOOR_TOTE:
                if(lift.AddFloorToteToStack())
                {
                    CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                }
                break;
                
            case ADD_CONTAINER:
                if(lift.AddContainerToStack())
                {
                    CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                }
                break;
                
            case EJECT_STACK:
                if(lift.EjectStack())
                {
                    CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                }
                break;
                
            case NO_ACTION:
            default:
                break;
        }
    }

    public void TeleopDeInitRobotSpecific()
    {

    }

    public void AutonomousInitRobotSpecific()
    {
        drive.ZeroGyro();
        drive.SetGyroZero(-90); // starting facing left, so fix offset

        AutoStateL = Constants.AUTO_STATE_MACHINE_L.DRIVE_FOR;
        AutoStateForward = Constants.AUTO_STATE_MACHINE_FORWARD.AUTO_START;
        AutoStateTime = Timer.getFPGATimestamp();
    }

    public void AutonomousPeriodicRobotSpecific()
    {
        int automode = ((Integer) autoChooser.getSelected()).intValue();
        
        switch (automode)
        {
            case 0:
                
                break;
                
            case 1:
                AutoGoToScoringZone();
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
     * Drive forward for a few seconds, then drive right for a few seconds
     */
    private void Drive_L()
    {
        switch (AutoStateL)
        {
            case DRIVE_FOR:
                drive.UpdateDrive(new SwerveVector(0, -1), 0, -1);
                if (Timer.getFPGATimestamp() - AutoStateTime > 2)
                {
                    AutoStateL = AUTO_STATE_MACHINE_L.DRIVE_STOP;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case DRIVE_STOP:
                drive.UpdateDrive(new SwerveVector(0, 0.001), 0, -1);
                if (Timer.getFPGATimestamp() - AutoStateTime > .5)
                {
                    AutoStateL = AUTO_STATE_MACHINE_L.DRIVE_RIGHT;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case DRIVE_RIGHT:
                drive.UpdateDrive(new SwerveVector(1, 0), 0, 270);
                if (Timer.getFPGATimestamp() - AutoStateTime > 2)
                {
                    AutoStateL = AUTO_STATE_MACHINE_L.AUTO_END;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case AUTO_END:
                drive.UpdateDrive(new SwerveVector(0, 0), 0, -1);

                break;

            default:
                AutoStateL = AUTO_STATE_MACHINE_L.AUTO_END;
                break;
        }
    }

    
    /**
     * Just drive forward for a few seconds
     */
    private void AutoGoToScoringZone()
    {
        AutoGoToScoringZone(-1);
    }
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
                AutoGoToScoringZone();
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
                AutoGoToScoringZone();
                
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
        double maxSlide = prefs.getDouble("MaxSlide", 1), slideP = prefs
                .getDouble("SlideP", 1), targetX = prefs.getDouble(
                "SlideTargetX", .5), forwardSpeed = prefs.getDouble(
                "AutoSpeed", .5);

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

    public void AutonomousDeInitRobotSpecific()
    {

    }
    
    public void TestModeInitRobotSpecific()
    {
    }

    public void TestModePeriodicRobotSpecific()
    {
        int testMode = 0;
        boolean button1 = controller.getRawButtion(1);
        boolean button2 = controller.getRawButtion(2);
        boolean button3 = controller.getRawButtion(3);
        boolean button4 = controller.getRawButtion(4);
        boolean button5 = controller.getRawButtion(5);
        boolean button6 = controller.getRawButtion(6);

        // test modes for cylinders and motors and features.
        switch (testMode)
        {
            case Constants.TEST_MODE_PNEUMATIC:
                PneumaticSubsystem testCylinders;

                if (button1)
                {
                    testCylinders = lift.GetCylindersRails();
                }
                else if (button2)
                {
                    testCylinders = lift.GetCylindersContainerCar();
                }
                else if (button3)
                {
                    testCylinders = lift.GetCylindersContainerFixed();
                }   
                else if (button4)
                {
                    testCylinders = lift.GetCylindersStackHolder();
                }
                else
                {
                    testCylinders = lift.GetCylindersRails();
                }

                // Toggle selected cylinders to opposite position
                if (testCylinders.IsExtended())
                {
                    testCylinders.Contract();
                }
                else
                {
                    testCylinders.Extend();
                }
                break;
            case Constants.TEST_MODE_MOTORS:
                if (button1)
                {
                    lift.GetCar().SetPosition(0);
                }
                else if (button2)
                {
                    lift.GetCar().SetPosition(1);
                }
                else if (button3)
                {
                    lift.GetCar().SetPosition(2);
                }
                else if (button4)
                {
                    lift.GetCar().SetPosition(3);
                }
                else if (button5)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button6)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_BACKWARD);
                }
                break;
            default:
                break;
        }
        
        // TODO: Get the value of one sensor and report that
        SmartDashboard.putBoolean("ToteIntakeSensor", lift.GetToteIntakeSensor().IsOn());
        SmartDashboard.putNumber("Car Height", lift.GetCar().GetHeight() );
    }

    public void TestModeDeInitRobotSpecific()
    {

    }

    public void DisabledInitRobotSpecific()
    {

    }

    public void DisabledPeriodicRobotSpecific()
    {

    }

    public void DisabledDeInitRobotSpecific()
    {

    }
}
