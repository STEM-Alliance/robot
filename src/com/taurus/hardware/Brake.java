package com.taurus.hardware;

import edu.wpi.first.wpilibj.Servo;

public class Brake 
{
    public enum BRAKE_STATE
    {
        NONE,
        ONE,
        BOTH,
        MOVING,
    }
    
    private final int TOLERANCE = 1;  // Degrees within angle to count as at that angle
    
    private Servo[] servos;
    private double[] anglePawnEngaged;
    
    /**
     * Represents a system of pawn-ratchet brakes controlled by servos.
     * @param servoPin PWN pins of the servos
     * @param engagedAngles each angle at which one of the pawns becomes engaged with the ratchets.
     */
    public Brake(int[] servoPin, double[] engagedAngles)
    {
        for(int index = 0; index < servoPin.length; index++)
        {
            servos[index] = new Servo(servoPin[index]);
        }
        anglePawnEngaged = engagedAngles;
    }
    
    /**
     * All of the servos have to be in their default angle (BRAKE_STATE is NONE, pawns are retracted to known
     * safe angle) for it to be safe to move the device attached to this brake.
     * @return if the brake is in a state safe to move the device attached
     */
    public boolean isSafeToMove()
    {
        boolean isSafe = true;
        
        for (int index = 0; index < servos.length; index++)
        {
            if(getPawnState(servos[index].getAngle()) != BRAKE_STATE.NONE)
            {
                isSafe = false;
                break;
            }
        }
        
        return isSafe;
    }
    
    /**
     * State of the entire brake. All servos have to be in a state to say that the brake is in that state.
     * @return state of the brake (all of the servos angles are in that state)
     */
    public BRAKE_STATE getState()
    {
        BRAKE_STATE[] states = new BRAKE_STATE[servos.length];
        BRAKE_STATE result;
        
        // Get all of the servo states, which are based on the angle
        for (int index = 0; index < servos.length; index++)
        {
            states[index] = getPawnState(servos[index].getAngle());
        }

        // All of the servos have to be in a state to say the brake is in that state
        result = states[0];
        for (int index = 0; index < servos.length; index++)
        {            
            if(states[index] != states[0])
            {
                result = BRAKE_STATE.MOVING;
                break;
            }
        }
        
        return result;
    }
    
    public void setState(BRAKE_STATE desiredState)
    {
        double stateAngle = 0;
        
        // Determine the angle of the state desired
        switch (desiredState)
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
        
        // Set all of the servos to the angle that gets us to that state        
        for (int index = 0; index < servos.length; index++)
        {
            servos[index].setAngle(stateAngle);
        }
    }
    
    /**
     * Determine which brake state this servo is at
     * @param angle of the servo (0 to 360)
     * @return state of this servo
     */
    private BRAKE_STATE getPawnState(double angle)
    {
        BRAKE_STATE result = BRAKE_STATE.MOVING;
        
        if (angle < TOLERANCE)
        {
            result = BRAKE_STATE.NONE;
        }
        else if (Math.abs(angle - anglePawnEngaged[1]) < TOLERANCE)
        {
            result = BRAKE_STATE.ONE;
        }
        else if (Math.abs(angle - anglePawnEngaged[2]) < TOLERANCE)
        {
            result = BRAKE_STATE.BOTH;
        }
        
        return result;
    }
}
