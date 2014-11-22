package com.taurus;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * <pre>
 * Halo Drive: Left joystick controls moving
 * left/right/forward/back. Right joystick
 * controls rotation (CCW/CW).
 *          ▲
 *          |               
 *      ◄---+---►           ◄---+---►
 *          |
 *          ▼
 *       Movement            Rotation
 *    
 * Parts adapted from Team 3946 (https://github.com/frc3946)
 * </pre>
 * @author
 */
public class SwerveController {
    
    public boolean useXbox = false;
    private Joystick left;
    private Joystick right;
    
    private XboxController xbox;
    
    /**
     * 
     */
    public SwerveController()
    {
        left = new Joystick(1);
        right = new Joystick(2);
        xbox = new XboxController(1);
    }

    /**
     * Get the magnitude of the direction vector formed by the
     * joystick's current position relative to its origin
     * @param hand Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand)
    {
        if(useXbox)
        {
            return xbox.getMagnitude(hand);
        }
        else
        {
            if(hand == Hand.kLeft)
            {
                return left.getMagnitude();
            }
            else
            {
                return right.getMagnitude();
            }
        }
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in degrees
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand)
    {
        if(useXbox)
        {
            return xbox.getDirectionDegrees(hand);
        }
        else
        {
            if(hand == Hand.kLeft)
            {
                return left.getDirectionDegrees();
            }
            else
            {
                return right.getDirectionDegrees();
            }
        }
    }
    
    /**
     * Get the direction of the vector formed by the joystick and its origin in radians
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand)
    {
        if(useXbox)
        {
            return xbox.getDirectionRadians(hand);
        }
        else
        {
            if(hand == Hand.kLeft)
            {
                return left.getDirectionRadians();
            }
            else
            {
                return right.getDirectionRadians();
            }
        }
    }

    /**
     * Get the X value of the joystick for Halo Drive
     * @return The X value of the joystick.
     */
    public double getHaloDrive_X()
    {
        if(useXbox)
        {
            return xbox.getAxis(XboxController.AxisType.kLeftX);
        }
        else
        {
            return left.getAxis(Joystick.AxisType.kX);
        }
    }

    /**
     * Get the Y value of the joystick for Halo Drive
     * @return The Y value of the joystick.
     */
    public double getHaloDrive_Y()
    {
        if(useXbox)
        {
            return xbox.getAxis(XboxController.AxisType.kLeftY);
        }
        else
        {
            return left.getAxis(Joystick.AxisType.kY);
        }
    }

    /**
     * Get the Rotation value of the joystick for Halo Drive
     * @return The Rotation value of the joystick.
     */
    public double getHaloDrive_Rotation()
    {
        if(useXbox)
        {
            return xbox.getAxis(XboxController.AxisType.kRightX);
        }
        else
        {
            return right.getAxis(Joystick.AxisType.kX);
        }
    }

    /**
     * Get the swerve vector (mag & angle) of the joystick for Halo Drive
     * @return The vector of the joystick.
     */
    public SwerveVector getHaloDrive_Vector()
    {
        if(useXbox)
        {
            return new SwerveVector(xbox.getX(Hand.kLeft),
                                    xbox.getY(Hand.kLeft));
        }
        else
        {
            return new SwerveVector(left.getX(),
                                    left.getY());
        }
    }
    
    /**
     * Get whether the high gear should be enabled
     * @return true if high gear, else low gear
     */
    public boolean getHighGearEnable()
    {
        if(useXbox)
        {
            return !xbox.getBumper(Hand.kRight);
        }
        else
        {
            // shift to high gear if the button is not held down
            return !left.getRawButton(2);
        }
    }
}
