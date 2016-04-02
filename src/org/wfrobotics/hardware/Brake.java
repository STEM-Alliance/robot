package org.wfrobotics.hardware;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Brake 
{
    public enum BRAKE_STATE
    {
        NONE,   // No pawns engaged
        ONE,    // First pawn engaged
        BOTH,   // Both pawns engaged
        MOVING, // Changing states
    }
    
    private final double TIME_PER_DEGREE = .19/60;  // Seconds
    // TODO - DRL create a better value by trying this on actual hardware
    
    private Servo[] servos;
    
    private double[] anglePawnEngaged;
    private BRAKE_STATE stateCurrent;
    private BRAKE_STATE taskCurrent;
    private double taskTimeStart;
    private double taskDuration;
    
    /**
     * Represents a system of pawn-ratchet brakes controlled by servos.
     * @param servoPin PWN pins of the servos
     * @param engagedAngles each angle at which one of the pawns becomes engaged with the ratchets.
     */
    public Brake(int[] servoPin, double[] engagedAngles)
    {
        // Configuration
        servos = new Servo[servoPin.length];        
        for(int index = 0; index < servoPin.length; index++)
        {
            servos[index] = new Servo(servoPin[index]);
        }
        anglePawnEngaged = engagedAngles;
        
        //Initial state
        stateCurrent = BRAKE_STATE.BOTH;  // When the robot is not powered, all pawns are engaged
    }
    
    /**
     * All of the servos have to be in their default angle (BRAKE_STATE is NONE, pawns are retracted to known
     * safe angle) for it to be safe to move the device attached to this brake.
     * @return if the brake is in a state safe to move the device attached
     */
    public boolean isSafeToMove()
    {
        return getState() == BRAKE_STATE.NONE;
    }
    
    /**
     * State of the entire brake. All servos have to be in a state to say that the brake is in that state.
     * @return state of the brake (all of the servos angles are in that state)
     */
    public BRAKE_STATE getState()
    {
        // Update current state
        if (stateCurrent == BRAKE_STATE.MOVING)
        {
            if (Timer.getFPGATimestamp() - taskTimeStart > taskDuration)
            {
                stateCurrent = taskCurrent;
            }
        }
        
        return stateCurrent;
    }
    
    /**
     * Sets the brake to a new state. The brake will not accept new desired states until 
     * the current state commanded is run to completion.
     * @param desiredState new position to set the brake to
     * @return If the command was accepted.
     */
    public boolean setState(BRAKE_STATE desiredState)
    {
        boolean taskAccepted = false;
        
        if(getState() != desiredState)
        {
            double stateAngle = getAngle(desiredState);
                        
            // Set all of the servos to the angle that gets us to that state        
            for (int index = 0; index < servos.length; index++)
            {
                servos[index].setAngle(stateAngle);
            }
            
            // Update state
            taskCurrent = desiredState;
            taskTimeStart = Timer.getFPGATimestamp();
            taskDuration = TIME_PER_DEGREE * Math.abs(stateAngle - getAngle(stateCurrent));
            stateCurrent = BRAKE_STATE.MOVING;
            taskAccepted = true;
        }
        
        return taskAccepted;
    }
    
    /**
     * Translates brake state to angle of that state
     * @param state
     * @return angle of the state
     */
    private double getAngle(BRAKE_STATE state)
    {
        double stateAngle = 0;
        
        switch (state)
        {
            case NONE:
                stateAngle = 0;
                break;
            case ONE:
                stateAngle = anglePawnEngaged[0];
                break;
            case BOTH:
                stateAngle = anglePawnEngaged[1];
                break;
            default:
                stateAngle = 0;
                break;
        }
        
        return stateAngle;
    }
}
