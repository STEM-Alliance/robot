package com.taurus.shooter;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {

    CANTalon shooterFT;
    CANTalon shooterFB;
    CANTalon shooterBT;
    CANTalon shooterBB;
    DigitalInput stopSwitch;
    CANTalon aimer;
    PIDController aimerPID;
    MagnetoPot aimAngle;
    enum Shooter_States{
        Start, Spin, Push, Reset
    }
    Shooter_States currentShooterState;
    double shooterStartTime;
    
    
    
    /**
     * Constructor
     */
    public Shooter(){
        shooterFT = new CANTalon(20);
        shooterFB = new CANTalon(21);
        shooterBT = new CANTalon(22);
        shooterBB = new CANTalon(23);
        stopSwitch = new DigitalInput(0);
        aimer = new CANTalon(24);
        aimerPID = new PIDController(1, 0, 0, 1);
        aimAngle = new MagnetoPot(0,360);
        currentShooterState = Shooter_States.Start;
    }

    /**
     * grabs the boulder - top and bottom opposite directions
     * @param speed between 0 and 1
     */
    public void grab (double speed){

        if (stopSwitch.get() == false){
            setSpeed(-speed, -speed);
        }
        else {
            stop();
        }
    }

    /**
     * stops all motors to hold boulder
     */
    public void stop (){
        setSpeed(0,0);
        currentShooterState = Shooter_States.Start;
        

    }

    /**
     * shoots the ball using a switch statement
     * @param topSpeed between 0 to 1
     * @param bottomSpeed between 0 to 1
     */
    public void shoot (double topSpeed, double bottomSpeed){
        switch(currentShooterState){
            case Start:
                shooterStartTime = Timer.getFPGATimestamp();
                currentShooterState = Shooter_States.Spin;
                
                break;
            case Spin:
                setSpeed(topSpeed, bottomSpeed);
                if (Timer.getFPGATimestamp() - shooterStartTime >= 1){
                    currentShooterState = Shooter_States.Push;
                }
                    
                break;
            case Push:
                setSpeed(topSpeed, bottomSpeed);
                //set the actual pushing motion 
                
                if (Timer.getFPGATimestamp() - shooterStartTime >= 2){
                    currentShooterState = Shooter_States.Reset;
                    
                }
                break;
            case Reset:
                break;
            default:
                break;
            
        }
        // Front wheels spin 
        // TODO Wait

        // Push the boulder forward into the front wheels
        //shooterBT.set(Constants.SHOOTER_SPEED_RELEASE);
        //shooterBB.set(Constants.SHOOTER_SPEED_RELEASE);
        // Stop front wheels
        // Wait
    }
    
    /**
     * shoots the ball 
     * @param speed between 0 to 1
     */
    public void shoot (double speed){
        shoot(speed,speed);
    }
    
    /**
     * aims the shooter
     * @param angle 0 to 360
     * @return true if angle reached, false if not
     */
    public boolean aim(double angle){

        double motorOutput = aimerPID.update(angle, aimAngle.get());

        if(Math.abs(aimAngle.get()-angle) <= 5){
            aimer.set(0);
            return true;
        } else {
            aimer.set(motorOutput);
            return false;
        }

    }

    /**
     * helper function to set speeds 
     * @param topSpeed between -1 and 1
     * @param bottomSpeed between -1 and 1
     */
    private void setSpeed (double topSpeed, double bottomSpeed){
        shooterFT.set(topSpeed);
        shooterFB.set(-bottomSpeed);
        //shooterBT.set(topSpeed);
        //shooterBB.set(-bottomSpeed);
    }
}
