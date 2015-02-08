package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.*;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous 
{
    
    private SwerveChassis drive;
    private Lift lift;
    private Vision vision;
    
    private int automode;
    
//    private double AutoStateTime;
//    
//    private AUTO_STATE_MACHINE_MOVE_TO_SCORING_ZONE AutoStateForward;
//    private AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE AutoStateFindAndGrabTote;
//    private AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER AutoStateFindAndGrabContainer;
    
    Command autoCommand;
    
    public Autonomous(SwerveChassis drive, Lift lift, Vision vision, int automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;
        this.automode = automode;
        
        drive.ZeroGyro();

//        AutoStateTime = Timer.getFPGATimestamp();
        
//        AutoStateForward = Constants.AUTO_STATE_MACHINE_MOVE_TO_SCORING_ZONE.START;
//        AutoStateFindAndGrabTote = AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE.START;
        
        switch (automode)
        {
            default:
            case 0:                
                break;
                
            case 1:
                autoCommand = new DriveToAutoScoringZone();
                break;
            
            case 2:
                autoCommand = new GrabToteAndDriveToAutoScoringZone();
                break;
                
            case 3:
                autoCommand = new GrabContainerAndDriveToAutoScoringZone();
                break;
                
//            case 4:
//                AutoGrab2TotesMovingLeftAndGoToScoringZone();
//                break;
        }
        
        autoCommand.start();
    }
    
    public void Run()
    {
        Scheduler.getInstance().run();
    }
    
//    private void AutoFindTote()
//    {
//        double maxSlide = Application.prefs.getDouble("MaxSlide", 1);
//        double slideP = Application.prefs.getDouble("SlideP", 1);
//        double targetX = Application.prefs.getDouble("SlideTargetX", .5);
//        double forwardSpeed = Application.prefs.getDouble("AutoSpeed", .5);
//
//        SwerveVector Velocity;
//
//        if (vision.getToteSeen())
//        {
//            Velocity = new SwerveVector(-forwardSpeed, Utilities.clampToRange(
//                    (vision.getResultX() - targetX) * slideP, -maxSlide,
//                    maxSlide));
//        }
//        else
//        {
//            Velocity = new SwerveVector(-forwardSpeed, 0);
//        }
//
//        double Rotation = 0;
//        double Heading = 270;
//
//        SmartDashboard.putNumber("Velocity X", Velocity.getX());
//        SmartDashboard.putNumber("Velocity Y", Velocity.getY());
//
//        drive.setGearHigh(false);
//        drive.UpdateDrive(Velocity, Rotation, Heading);
//    }
    
    private class DriveToAutoScoringZone extends CommandGroup
    {
        public DriveToAutoScoringZone()
        {
            // TODO: numbers
            addSequential(new DriveForTime(new SwerveVector(0, 1), 0, -1, 2));
        }
    }
    
    
    private class GrabToteAndDriveToAutoScoringZone extends CommandGroup
    {
        public GrabToteAndDriveToAutoScoringZone()
        {
            // TODO: numbers
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0, 270));
            addSequential(new PickupFloorTotes(0));
            addSequential(new DriveToAutoScoringZone());
        }
    }
    
    private class GrabContainerAndDriveToAutoScoringZone extends CommandGroup
    {
        public GrabContainerAndDriveToAutoScoringZone()
        {
            addSequential(new PickupContainer());
            addSequential(new DriveToAutoScoringZone());
        }
    }
    
    private class Grab2TotesMovingLeftAndDriveToScoringZone extends CommandGroup
    {
        public Grab2TotesMovingLeftAndDriveToScoringZone()
        {
            // TODO: numbers
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0, 270));
            addParallel(new PickupFloorTotes(1));
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DriveToAutoScoringZone());
        }
    }
    
    private class NavigateAroundContainerToPickUpTote extends CommandGroup
    {
        public NavigateAroundContainerToPickUpTote()
        {
            addSequential(new DriveForTime(new SwerveVector(0, 1), 0, 270, 1));
            // TODO do shit
        }
    }
    
    private class DropToteStack extends Command
    {
        private boolean Finished = false;        

        @Override
        protected void initialize()
        {
            requires(lift);
            this.Finished = false;
        }

        @Override
        protected void execute()
        {
            this.Finished = lift.DropStack();
        }

        @Override
        protected boolean isFinished()
        {
            return this.Finished;
        }

        @Override
        protected void end()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void interrupted()
        {
            end();
        }
        
    }
    
    private class PickupContainer extends Command
    {
        private boolean Finished = false;

        @Override
        protected void initialize()
        {
            requires(lift);
            this.Finished = false;
        }

        @Override
        protected void execute()
        {
            this.Finished = lift.AddContainerToStack();
        }

        @Override
        protected boolean isFinished()
        {
            return this.Finished;
        }

        @Override
        protected void end()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void interrupted()
        {
            end();
        }
        
    }
    
    private class PickupFloorTotes extends Command
    {
        private int MaxTotesInStack;
        private boolean Finished;
        
        public PickupFloorTotes(int MaxTotesInStack)
        {
            this.MaxTotesInStack = MaxTotesInStack;
            this.Finished = false;
        }
        
        @Override
        protected void initialize()
        {
            requires(lift);
            Finished = false;
        }

        @Override
        protected void execute()
        {
            this.Finished = lift.AddFloorToteToStack(MaxTotesInStack);
        }

        @Override
        protected boolean isFinished()
        {
            return this.Finished;
        }

        @Override
        protected void end()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected void interrupted()
        {
            end();
        }
        
    }
    
    private class DriveForTime extends Command
    {
        private SwerveVector Velocity;
        private double Rotation;
        private double Heading;
        private double EndTime;
        
        public DriveForTime(SwerveVector velocity, double rotation, double heading, double TimeDelta)
        {
            this.Velocity = velocity;
            this.Rotation = rotation;
            this.Heading = heading;
            this.EndTime = Timer.getFPGATimestamp() + TimeDelta;
        }

        @Override
        protected void initialize()
        {
            requires(drive);
        }

        @Override
        protected void execute()
        {
            drive.UpdateDrive(Velocity, Rotation, Heading);
        }

        @Override
        protected boolean isFinished()
        {
            return Timer.getFPGATimestamp() > this.EndTime;
        }

        @Override
        protected void end()
        {
            drive.UpdateDrive(new SwerveVector(), 0, -1);
        }

        @Override
        protected void interrupted()
        {
            end();
        }
    }
    
    private class DriveUntilToteSensed extends Command
    {
        private SwerveVector Velocity;
        private double Rotation;
        private double Heading;
        
        public DriveUntilToteSensed(SwerveVector velocity, double rotation, double heading)
        {
            this.Velocity = velocity;
            this.Rotation = rotation;
            this.Heading = heading;
        }

        @Override
        protected void initialize()
        {
            requires(drive);
        }

        @Override
        protected void execute()
        {
            drive.UpdateDrive(Velocity, Rotation, Heading);
        }

        @Override
        protected boolean isFinished()
        {
            return lift.GetToteIntakeSensor().IsOn();
        }

        @Override
        protected void end()
        {
            drive.UpdateDrive(new SwerveVector(), 0, -1);
        }

        @Override
        protected void interrupted()
        {
            end();
        }
    }
    
}
