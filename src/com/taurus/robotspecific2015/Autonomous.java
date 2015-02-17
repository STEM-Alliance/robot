package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.AUTO_MODE;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Autonomous {

    private SwerveChassis drive;
    private Lift lift;
    private Vision vision;

    Command autoCommand = null;

    boolean dropSmallStack = true;

    public Autonomous(SwerveChassis drive, Lift lift, Vision vision,
            AUTO_MODE automode)
    {
        this.drive = drive;
        this.lift = lift;
        this.vision = vision;

        drive.ZeroGyro();

        switch (automode)
        {
            default:
            case DO_NOTHING:
                break;

            case GO_TO_ZONE:
                autoCommand = new DriveToAutoScoringZone();
                break;

            case GRAB_1_TOTE:
                drive.SetGyroZero(270);
                autoCommand = new GrabTote();
                break;

            case GRAB_2_TOTES:
                drive.SetGyroZero(270);
                autoCommand = new Grab2Totes();
                break;

            case GRAB_2_TOTES_NO_CONTAINER:
                break;
                
            case GRAB_3_TOTES:
                drive.SetGyroZero(270);
                autoCommand = new Grab3Totes();
                break;

            case GRAB_3_TOTES_NO_LEFT_CONTAINER:
                drive.SetGyroZero(270);
                autoCommand = new Grab3TotesNoLeftContainer();
                break;
                
            case GRAB_3_TOTES_NO_RIGHT_CONTAINER:
                drive.SetGyroZero(270);
                autoCommand = new Grab3TotesNoRightContainer();
                break;
                
            case GRAB_3_TOTES_NO_CONTAINERS:
                drive.SetGyroZero(270);
                autoCommand = new Grab3TotesNoContainers();
                break;
                
            case GRAB_CONTAINER:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer();
                break;

            case GRAB_CONTAINER_AND_1_TOTE:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer1Tote();
                break;

            case GRAB_CONTAINER_AND_2_TOTES:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer2Totes();
                break;

            case GRAB_CONTAINER_AND_3_TOTES:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer3Totes();
                break;
                
            case GRAB_CONTAINER_AND_3_TOTES_NO_LEFT_CONTAINER:
                drive.SetGyroZero(90);
                autoCommand = new GrabContainer3TotesNoLeftContainer();
                break;
        }

        if (autoCommand != null)
        {
            autoCommand.start();
        }
    }

    public void Run()
    {
        Scheduler.getInstance().run();
    }

    private class DriveLeftToGrabTote extends CommandGroup {
        public DriveLeftToGrabTote()
        {
            // TODO: speed and timeout 
            addSequential(new DriveUntilToteSensed(new SwerveVector(-1, 0), 0,
                    270), 1.0);
            addSequential(new TriggerToteSensed());
        }
    }

    private class DriveRightToGrabTote extends CommandGroup {
        public DriveRightToGrabTote()
        {
            // TODO: speed and timeout 
            addSequential(new DriveUntilToteSensed(new SwerveVector(1, 0), 0,
                    90), 1.0);
            addSequential(new TriggerToteSensed());
        }
    }

    private class DriveToAutoScoringZone extends CommandGroup {
        public DriveToAutoScoringZone()
        {
            // TODO: speed and timeout 
            addSequential(new Drive(new SwerveVector(0, 1), 0, -1), 2);
        }
    }
    
    private class DeliverAndDropTotes extends CommandGroup {
        public DeliverAndDropTotes()
        {
            addSequential(new DriveToAutoScoringZone());

            if (lift.GetTotesInStack() >= 2 || dropSmallStack)
            {
                addSequential(new DropToteStack());

                // Back up
                // TODO: speed and timeout 
                addSequential(new Drive(new SwerveVector(.2, 0), 0, 270), 1);
            }
        }
    }

    private class NavigateAroundContainerToPickUpTote extends CommandGroup {
        public NavigateAroundContainerToPickUpTote()
        {
            // TODO: speed and timeout 
            addSequential(new Drive(new SwerveVector(0, 1), 0, 270), 1);
            addSequential(new Drive(new SwerveVector(-1, 0), 0, 270), 1);
            addSequential(new DriveBackwardsToLineUpTote());
            addSequential(new DriveLeftToGrabTote());
        }
    }

    private class GrabTote extends CommandGroup {
        public GrabTote()
        {
            addParallel(new PickupFloorTotes(0));
            addSequential(new TriggerToteSensed());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab2Totes extends CommandGroup {
        public Grab2Totes()
        {
            addParallel(new PickupFloorTotes(1));
            addSequential(new TriggerToteSensed());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab2TotesNoContainer extends CommandGroup {
        public Grab2TotesNoContainer()
        {
            addParallel(new PickupFloorTotes(1));
            addSequential(new TriggerToteSensed());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab3Totes extends CommandGroup {
        public Grab3Totes()
        {
            addParallel(new PickupFloorTotes(2));
            addSequential(new TriggerToteSensed());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab3TotesNoLeftContainer extends CommandGroup {
        public Grab3TotesNoLeftContainer()
        {
            addParallel(new PickupFloorTotes(2));
            addSequential(new TriggerToteSensed());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab3TotesNoRightContainer extends CommandGroup {
        public Grab3TotesNoRightContainer()
        {
            addParallel(new PickupFloorTotes(2));
            addSequential(new TriggerToteSensed());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class Grab3TotesNoContainers extends CommandGroup {
        public Grab3TotesNoContainers()
        {
            addParallel(new PickupFloorTotes(2));
            addSequential(new TriggerToteSensed());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class GrabContainer extends CommandGroup {
        public GrabContainer()
        {
            addSequential(new PickupContainer());
            addSequential(new DriveToAutoScoringZone());
        }
    }

    private class GrabContainer1Tote extends CommandGroup {
        public GrabContainer1Tote()
        {
            addSequential(new PickupContainer());
            addParallel(new PickupFloorTotes(0));
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class GrabContainer2Totes extends CommandGroup {
        public GrabContainer2Totes()
        {
            addSequential(new PickupContainer());
            addParallel(new PickupFloorTotes(1));
            addSequential(new DriveRightToGrabTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class GrabContainer3Totes extends CommandGroup {
        public GrabContainer3Totes()
        {
            addSequential(new PickupContainer());
            addParallel(new PickupFloorTotes(2));
            addSequential(new DriveRightToGrabTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new NavigateAroundContainerToPickUpTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class GrabContainer3TotesNoLeftContainer extends CommandGroup {
        public GrabContainer3TotesNoLeftContainer()
        {
            addSequential(new PickupContainer());
            addParallel(new PickupFloorTotes(2));
            addSequential(new DriveRightToGrabTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DriveLeftToGrabTote());
            addSequential(new DeliverAndDropTotes());
        }
    }

    private class TriggerToteSensed extends Command {
        // TODO: Implement

        @Override
        protected void initialize()
        {
        }

        @Override
        protected void execute()
        {
            // TODO Auto-generated method stub
        }

        @Override
        protected boolean isFinished()
        {
            return true;
        }

        @Override
        protected void end()
        {
        }

        @Override
        protected void interrupted()
        {
            end();
        }
    }

    private class DriveBackwardsToLineUpTote extends Command {
        boolean Finished = false;

        public DriveBackwardsToLineUpTote()
        {
            requires(drive);
        }

        @Override
        protected void initialize()
        {
            this.Finished = false;
        }

        @Override
        protected void execute()
        {
            double maxSlide = Application.prefs.getDouble("MaxSlide", 1);
            double slideP = Application.prefs.getDouble("SlideP", 2);
            double targetX = Application.prefs.getDouble("SlideTargetX", .55);
            double xErrorThreshold =
                    Application.prefs.getDouble("SlideTargetXError", .05);

            SwerveVector Velocity;

            if (vision.getToteSeen())
            {
                double xError = vision.getResultX() - targetX;

                if (Math.abs(xError) < xErrorThreshold)
                {
                    this.Finished = true;
                }

                double slideVelocity =
                        Utilities.clampToRange(xError * slideP, -maxSlide,
                                maxSlide);

                Velocity = new SwerveVector(0, slideVelocity);

            }
            else
            {
                Velocity = new SwerveVector(0, -maxSlide);
            }

            drive.UpdateDrive(Velocity, 0, 270);
        }

        @Override
        protected boolean isFinished()
        {
            return this.Finished;
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

    private class DropToteStack extends Command {
        private boolean Finished = false;

        public DropToteStack()
        {
            requires(lift);
        }

        @Override
        protected void initialize()
        {
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
        }

        @Override
        protected void interrupted()
        {
            end();
        }
    }

    private class PickupContainer extends Command {
        private boolean Finished = false;

        public PickupContainer()
        {
            requires(lift);
        }

        @Override
        protected void initialize()
        {
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
        }

        @Override
        protected void interrupted()
        {
            end();
        }
    }

    private class PickupFloorTotes extends Command {
        private int MaxTotesInStack;
        private boolean Finished;

        public PickupFloorTotes(int MaxTotesInStack)
        {
            this.MaxTotesInStack = MaxTotesInStack;
            this.Finished = false;
            requires(lift);
        }

        @Override
        protected void initialize()
        {
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
        }

        @Override
        protected void interrupted()
        {
            end();
        }

    }

    private class Drive extends Command {
        private SwerveVector Velocity;
        private double Rotation;
        private double Heading;

        public Drive(SwerveVector velocity, double rotation, double heading)
        {
            this.Velocity = velocity;
            this.Rotation = rotation;
            this.Heading = heading;
            requires(drive);
        }

        @Override
        protected void initialize()
        {
        }

        @Override
        protected void execute()
        {
            drive.UpdateDrive(Velocity, Rotation, Heading);
        }

        @Override
        protected boolean isFinished()
        {
            return false;
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

    private class DriveUntilToteSensed extends Command {
        private SwerveVector Velocity;
        private double Rotation;
        private double Heading;

        public DriveUntilToteSensed(SwerveVector velocity, double rotation,
                double heading)
        {
            this.Velocity = velocity;
            this.Rotation = rotation;
            this.Heading = heading;
            requires(drive);
        }

        @Override
        protected void initialize()
        {
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
