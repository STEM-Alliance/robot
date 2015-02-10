package com.taurus.robotspecific2015;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.taurus.Utilities;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Autonomous 
{
    
    private SwerveChassis drive;
    private Lift lift;
    private Vision vision;
    
    Command autoCommand;
    
    public Autonomous(SwerveChassis drive, Lift lift, Vision vision, int automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;
        
        drive.ZeroGyro();
        
        switch (automode)
        {
            default:
            case 0:                
                break;
                
            case 1:
                autoCommand = new DriveToAutoScoringZone();
                break;
            
            case 2:
                drive.SetGyroZero(270);
                autoCommand = new GrabToteAndDriveToAutoScoringZone();
                break;
                
            case 3:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainerAndDriveToAutoScoringZone();
                break;
                
            case 4:
                drive.SetGyroZero(270);
                autoCommand = new Grab2TotesMovingLeftAndDriveToScoringZone();
                break;
            
            case 6:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer2TotesMiddleScoringZone();
                break;
                
            case 8:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer1ToteAndGotoScoringZone();
                break;
                
            case 9:
                drive.SetGyroZero(270);
                autoCommand = new Grab3TotesGoToScoringZone();
                break;
                
            case 10:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer3TotesMiddleScoringZone();
                break;
        }
        
        autoCommand.start();
    }
    
    public void Run()
    {
        Scheduler.getInstance().run();
    }
        
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
            addSequential(new DriveForTime(new SwerveVector(-1, 0), 0, 270, 1));
            addSequential(new DriveTowardToteWithVision());
        }
    }

    private class Grab3TotesGoToScoringZone extends CommandGroup
    {
        public Grab3TotesGoToScoringZone()
        {
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0, 270));
            addParallel(new PickupFloorTotes(2));
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DriveAndDrop());
        }
    }
    
    private class GrabContainer1ToteAndGotoScoringZone extends CommandGroup
    {
        public GrabContainer1ToteAndGotoScoringZone()
        {
            addSequential(new PickupContainer());
            addSequential(new DriveUntilToteSensed(new SwerveVector(1, 0), 0, 90));
            addParallel(new PickupFloorTotes(0));
            addSequential(new DriveToAutoScoringZone());
        }
    }
    
    private class GrabContainer2TotesMiddleScoringZone extends CommandGroup
    {
        public GrabContainer2TotesMiddleScoringZone()
        {
            addSequential(new PickupContainer());
            addSequential(new DriveUntilToteSensed(new SwerveVector(1, 0), 0, 90));
            addParallel(new PickupFloorTotes(1));
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0, 270));   //TODO adjust speed to match rotation
            addSequential(new DriveToAutoScoringZone());
        }
    }
    
    private class GrabContainer3TotesMiddleScoringZone extends CommandGroup
    {
        public GrabContainer3TotesMiddleScoringZone()
        {
            addSequential(new PickupContainer());
            addSequential(new DriveUntilToteSensed(new SwerveVector(1, 0), 0, 90));
            addParallel(new PickupFloorTotes(2));
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0, 270));   //TODO adjust speed to match rotation
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DriveAndDrop());
        }
    }
    
    private class DriveAndDrop extends CommandGroup
    {
        public DriveAndDrop()
        {
            addSequential(new DriveToAutoScoringZone());
            addSequential(new DropToteStack());
            addSequential(new DriveForTime(new SwerveVector(1, 0), 0, 270, 1));
        }
    }
        
    private class DriveTowardToteWithVision extends Command
    {
        double lastDistance = Double.POSITIVE_INFINITY;        

        @Override
        protected void initialize()
        {
            requires(drive);            
        }

        @Override
        protected void execute()
        {
            double maxSlide = Application.prefs.getDouble("MaxSlide", 1);
            double slideP = Application.prefs.getDouble("SlideP", 1);
            double targetX = Application.prefs.getDouble("SlideTargetX", .5);
            double forwardSpeed = Application.prefs.getDouble("AutoSpeed", .5);
            double lastPosition = Application.prefs.getDouble("LastDistance", .5);
            
            SwerveVector Velocity;
            double Heading;
            
            if (vision.getToteSeen())
            {
                lastDistance = vision.getResultY();
            }
            
            // TODO: numbers
            if (lastDistance > lastPosition)
            {
                Heading = 225;
            }
            else
            {
                Heading = 270;
            }
                
            if (vision.getToteSeen())
            {
                Velocity = new SwerveVector(
                        -forwardSpeed, 
                        Utilities.clampToRange((vision.getResultX() - targetX) * slideP, 
                            -maxSlide, maxSlide));
            }
            else
            {
                Velocity = SwerveVector.NewFromMagAngle(forwardSpeed, Heading);
            }
            
            drive.setGearHigh(false);
            drive.UpdateDrive(Velocity, 0, Heading);
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
