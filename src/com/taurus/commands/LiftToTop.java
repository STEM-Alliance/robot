package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class LiftToTop extends CommandGroup{

    public LiftToTop()
    {
        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new LiftToTopInternal());
    }
       
    private class LiftToTopInternal extends Command 
    {
        private boolean done;
        private double endHeight;
    
        public LiftToTopInternal() {
            requires(Robot.liftSubsystem);
        }
        
        protected void initialize() {
            done = false;
            endHeight = Preferences.getInstance().getDouble("LiftHighOffset", 40);
            Robot.liftSubsystem.enableBrakeMode(false);
        }
        
        protected void execute() {
            Utilities.PrintCommand("Lift", this);
            done = Robot.liftSubsystem.setHeightFromFloor(endHeight);//store result of setHeight function into the done variable
        }
    
        protected boolean isFinished() {
            return done;
        }
        
        protected void end() {
            Robot.liftSubsystem.enableBrakeMode(true);
            Robot.liftSubsystem.setSpeed(0, 0);
            done = Robot.liftSubsystem.setHeightFromFloor(Robot.liftSubsystem.getHeightFromFloorAverage());
        }
        
        protected void interrupted() {
            end();
        }
    }
}