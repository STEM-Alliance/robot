package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.LiftHold;
import com.taurus.commands.LiftStop;
import com.taurus.hardware.MagnetoPot;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftSubsystem extends Subsystem{
    public final double LIMIT_UPPER = 50;
    public final double LIMIT_LOWER = 9.6;
    public final double LIMIT_TOLERANCE = 1;
    public final double LENGTH_SCISSOR_STEP = 26;
    
    private CANTalon motorLeft;
    private CANTalon motorRight;
    private MagnetoPotSRX potLeft;
    private MagnetoPotSRX potRight;  // TODO - DRL remove if design changes and is unused
    private PIDController heightRightPID;
    private PIDController heightLeftPID;
    
        
    /**
     * Constructor
     */
    public LiftSubsystem() {
        motorLeft = new CANTalon(RobotMap.PIN_LIFT_TALON_L);
        motorRight = new CANTalon(RobotMap.PIN_LIFT_TALON_R);
        
        potLeft = new MagnetoPotSRX(motorLeft, -360);
        potRight = new MagnetoPotSRX(motorRight, -360);
        heightRightPID = new PIDController(.5, 0, 0, 1);
        heightLeftPID = new PIDController(.5, 0, 0, 1);
    }
    
    protected void initDefaultCommand()
    {
        setDefaultCommand(new LiftStop());
    }
    
    /**
     * set lift height
     * @param height we want to be at in inches from the floor
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeight(double height) {
       
        double[] motorOutput;
        boolean arrivedL;
        boolean arrivedR;
        
        // Error check height, set to within bounds if needed
        if(height >= LIMIT_UPPER)
        {
            height = LIMIT_UPPER;
        } 
        else if(height <= LIMIT_LOWER)
        {
            height = LIMIT_LOWER;
        }
        
        // subtract the distance from the floor to the bottom of the lift
        // this lets the rest of the calculations all be relative to the same point
        height -= LIMIT_LOWER;
        
        SmartDashboard.putNumber("Lift Desired Height", height);
        
        motorOutput = getPIDSpeed(height);

        // determine if each side is at the desired height
        arrivedL = Math.abs(getHeightL()-height) <= LIMIT_TOLERANCE;
        arrivedR = Math.abs(getHeightR()-height) <= LIMIT_TOLERANCE;
        
        if(arrivedL && arrivedR)
        {
            stopLift();
        }
        else
        {
            setSpeed(motorOutput[0]* Preferences.getInstance().getDouble("LiftSpeedLimit", .5),
                    motorOutput[1]* Preferences.getInstance().getDouble("LiftSpeedLimit", .5));
        }
        
        return arrivedL && arrivedR;
    }
    
    /**
     * helper function: sets the motor speeds for the lift to the same value
     * @param right speed between -1 to 1
     * @param right speed between -1 to 1
     */
    public void setSpeed(double right, double left) {
        SmartDashboard.putNumber("Lift Total Height", getTotalHeight());
        SmartDashboard.putNumber("Lift Height", getHeight());
        SmartDashboard.putNumber("Lift Height L", getHeightL());
        SmartDashboard.putNumber("Lift Height R", getHeightR());
        SmartDashboard.putNumber("Lift Pot L", getAngleL());
        SmartDashboard.putNumber("Lift Pot R", getAngleR());
        
        updatedPotOffsets();

        //Once height is 20 inches begin decelerating
            //If height is = 20 set motor speeds to 40% of current speed
        
        //Based on height set the motor speed to a percentage of current speed
            //At 15 inches set motor speeds to 50% of current speed
            //At 10 inches set motor speeds to 80% of current speed
            //At 5 inches set motor speeds to 40% of current speed
        
        //When height reaches 0, turn motors off/set their speed to 0
            //At 0 inches set motor speeds to 0
        
        left = speedDamper(left, getHeightL());
        right = speedDamper(right, getHeightR());
        
        SmartDashboard.putNumber("Lift Speed L", left);
        SmartDashboard.putNumber("Lift Speed R", right);
        
        motorRight.set(right);
        motorLeft.set(left);        
    }

    private double speedDamper(double speed, double height){
        //Once height is 20 inches begin decelerating
        //If height is = 20 set motor speeds to 40% of current speed
    
    //Based on height set the motor speed to a percentage of current speed
        //At 15 inches set motor speeds to 50% of current speed
        //At 10 inches set motor speeds to 80% of current speed
        //At 5 inches set motor speeds to 40% of current speed
    
    //When height reaches 0, turn motors off/set their speed to 0
        //At 0 inches set motor speeds to 0
        
        if (speed < 0){            
                 

            if (height == 0){
                speed = 0;
            }
            
            else if (height <= 5){
                speed = speed * .4;
            }
            
            else if (height <= 10){
                speed = speed * .8;
            }
            
            else if (height <= 15){
                speed = speed * .5;
            }
            
            else if (height <= 20){
                speed = speed * .4;
            }    

        }
        return speed;
    }
    
    private void stopLift() {
        setSpeed(0, 0);
    }
    
    private void updatePIDConstants()
    {
        heightRightPID.setP(Preferences.getInstance().getDouble("LiftPID_P", .5));
        heightRightPID.setI(Preferences.getInstance().getDouble("LiftPID_I", 0));
        heightRightPID.setD(Preferences.getInstance().getDouble("LiftPID_D", 0));
        heightLeftPID.setP(Preferences.getInstance().getDouble("LiftPID_P", .5));
        heightLeftPID.setI(Preferences.getInstance().getDouble("LiftPID_I", 0));
        heightLeftPID.setD(Preferences.getInstance().getDouble("LiftPID_D", 0));
    }
    
    /**
     * Calculate the desirable speeds to keep the two lift motors in sync.
     * This is advantageous because it corrects position drift due to one motor being faster than the other.
     * Author: David Lindner
     * @param height desired, inches from bottom of lift
     * @return speed calculated (right, left)
     */
    private double[] getPIDSpeed(double height)
    {
        updatePIDConstants();
        
        // Array convention -> (right side, left side)
        double[] heightActual = {getHeightR(), getHeightL()};
        
        double heightActualAverage = (heightActual[0] + heightActual[1]) / 2;
        double[] speedPidAverage = {heightRightPID.update(height, getHeightR()), heightLeftPID.update(height, getHeightL()) };
        double[] heightError = {heightActual[0] - heightActualAverage, heightActual[1] - heightActualAverage};  // Ex: 2 - 1.5 = .5 error
        double[] direction = {Math.signum(speedPidAverage[0]), Math.signum(speedPidAverage[1]) };  // Up: 1, Down: -1
        
        // If we are doing down, have error percentage be based on how far we are ahead/behind in that desired direction.
        // Ex: 10% ahead -> 10% behind, .1 error * -1 / 1 average + 1 = .91 rather than 1.1
        double[] heightErrorPercent = {(direction[0] * heightError[0] / (heightActualAverage + LIMIT_LOWER) + 1), 
                                       (direction[1] * heightError[1] / (heightActualAverage + LIMIT_LOWER) + 1) };  // Ex: 2 vs 1.5 -> 1.33 
        double[] speedCompensated = {speedPidAverage [0] / heightErrorPercent[0], speedPidAverage [1] / heightErrorPercent[1]};  // Ex: 1 / 1.33 = .75 speed
        
        // At this point, speed may be greater than one because we corrected for error.
        // Ex: Told one motor to go 110% of speed desired, the other to go 91% of speed desired. But speed desired is 1.
        // Scale it back to within range -1 to 1.        
        double[] speedCompensatedAbs = {Math.abs(speedCompensated[0]), Math.abs(speedCompensated[1])};
        
        for (int index = 0; index < 2; index++)
        {
            if (speedCompensatedAbs[index] > 1)
            {                
                // This is the side with positive error, and after compensation, happens to be above max magnitude of 1.
                // Scale back both speeds proportionally to how far the faster side is above max magnitude of 1.
                //double percentAbove = speedCompensatedAbs[index] - 1;
                
                // we can't subtract 1 from this, as when the sides become close,
                // the values will be much larger than 1. By dividing by the largest,
                // we will scale everything to a max of 1.
                double percentAbove = speedCompensatedAbs[index]; 

                for (int side = 0; side < 2; side++)
                {
                    speedCompensated[side] /= percentAbove;
                }
                break;
            }
        }
        
        return speedCompensated;
    }
    
    private void updatedPotOffsets()
    {
        potRight.setOffset(Preferences.getInstance().getDouble("LiftPotOffsetR", 0));
        potLeft.setOffset(Preferences.getInstance().getDouble("LiftPotOffsetL", 0));
        potRight.setFullRange(Preferences.getInstance().getDouble("LiftPotScaleR", 0));
        potLeft.setFullRange(Preferences.getInstance().getDouble("LiftPotScaleL", 0));
    }

    private double getAngleR() {
        return Utilities.wrapToRange(potRight.get(), 0, 360);
    }
    private double getAngleL() {
        return Utilities.wrapToRange(potLeft.get(), 0, 360);
    }

    public double getHeight() {
        return (getHeightL() + getHeightR()) / 2;
    }
    
    public double getTotalHeight(){
        return getHeight() + LIMIT_LOWER;
    }
    
    private double getHeightR() {
        return (Math.sin(Math.toRadians(getAngleR()))*LENGTH_SCISSOR_STEP)*2;
    }
    
    private double getHeightL() {
        return (Math.sin(Math.toRadians(getAngleL()))*LENGTH_SCISSOR_STEP)*2;
    }
}