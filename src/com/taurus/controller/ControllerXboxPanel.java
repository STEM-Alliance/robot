package com.taurus.controller;

import com.taurus.controller.Xbox.ButtonType;
import com.taurus.controller.Xbox.RumbleType;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class ControllerXboxPanel implements Controller, Runnable {

    private Xbox xbox;
    private Panel panel;

    public static final double DEADBAND = 0.25;

    private boolean fieldRelative;
    private boolean fieldRelativeLast;

    private Thread thread;
    private volatile float RumbleVal = 0;
    private volatile Hand RumbleHand = Hand.kLeft;
    private volatile double RumbleTime = 0;
    
    /**
     * Create a new Xbox Controller object
     */
    public ControllerXboxPanel()
    {
        xbox = new Xbox(0);
        panel = new Panel(1);

        fieldRelative = true;
        fieldRelativeLast = false;
        
        thread = new Thread();
        thread.setPriority(Thread.MIN_PRIORITY);
    }

    public double getX(Hand hand)
    {
        return xbox.getX(hand);
    }

    public double getY(Hand hand)
    {
        return xbox.getY(hand);
    }

    /**
     * Get the magnitude of the direction vector formed by the joystick's
     * current position relative to its origin
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand)
    {
        double value = 0;

        value = xbox.getMagnitude(hand);

        if (value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * degrees
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand)
    {
        return xbox.getDirectionDegrees(hand);
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * radians
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand)
    {
        return xbox.getDirectionRadians(hand);
    }

    /**
     * Get the Rotation value of the joystick for Halo Drive
     * 
     * @return The Rotation value of the joystick.
     */
    public double getHaloDrive_Rotation()
    {
        double value = 0;

        value = xbox.getAxis(Xbox.AxisType.kRightX);

        if (Math.abs(value) < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Halo
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getHaloDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }

        return value;
    }

    @Override
    public double getHaloDrive_Heading45()
    {
        double val = -1;
        
        if(xbox.getBack())
        {
            val = 45;
        }
        else if(xbox.getStart())
        {
            val = -45;
        }
        return val;
    }
    
    /**
     * Get the heading/angle in degrees for Angle Drive
     * 
     * @return The angle in degrees of the joystick.
     */
    public double getAngleDrive_Heading()
    {
        double Angle = -1;
        if (xbox.getMagnitude(Hand.kRight) > 0.65)
        {
            Angle = xbox.getDirectionDegrees(Hand.kRight);
        }

        return Angle;
    }

    /**
     * Get rotation from the DPad for Angle Drive
     * 
     * @return
     */
    public double getAngleDrive_Rotation()
    {
        double Rotation = 0;
        if (xbox.getPOV() == 90)
        {
            Rotation = .75;
        }
        else if (xbox.getPOV() == 270)
        {
            Rotation = -.75;
        }
        return Rotation;
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Angle
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getAngleDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }
        return value;
    }

    public boolean getRawButton(int button)
    {
        if(button <= Xbox.ButtonType.kCount.value)
        {
            return xbox.getRawButton(button);
        }
        else
        {
            return panel.getRawButton(button - Xbox.ButtonType.kCount.value);
        }
    }

    public double getDPad()
    {
        return xbox.getPOV();
    }

    /**
     * Get whether the high gear should be enabled
     * 
     * @return true if high gear, else low gear
     */
    public boolean getHighGearEnable()
    {
        return false;
    }

    /**
     * Get the brake
     * 
     * @return
     */
    public boolean getSwerveBrake()
    {
        return false; 
    }

    public boolean getResetGyro()
    {
        return panel.getYellowRButton();
    }

    public boolean getFieldRelative()
    {
        if (!fieldRelativeLast && panel.getGreenRButton())
        {
            fieldRelative = !fieldRelative;
        }
        fieldRelativeLast = panel.getGreenRButton();
        return fieldRelative;
    }

    @Override
    public boolean getTrigger(Hand hand)
    {
        return xbox.getTrigger(hand);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAddChuteTote()
    {
        return xbox.getAButton();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAddFloorTote()
    {
        return xbox.getBButton();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAddContainer()
    {
        return xbox.getXButton();
    }

    @Override
    public boolean getCarryStack()
    {
        return xbox.getYButton();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEjectStack()
    {
        return false;
    }

    @Override
    public boolean getCarHome()
    {
        return panel.getGreenLButton();
    }

    @Override
    public boolean getCarTop()
    {
        return panel.getYellowLButton();
    }

    @Override
    public boolean getStopAction()
    {
        return panel.getWhiteRButton();
    }

    @Override
    public boolean getFakeToteAdd()
    {
        return xbox.getBumper(Hand.kLeft);
    }

    @Override
    public boolean getManualLift()
    {
        return panel.getSwitchL();

    }

    @Override
    public boolean getFakePostion()
    {

        return panel.getWhiteLButton();
    }

    @Override
    public boolean getReleaseEverything()
    {
        return panel.getBlackRButton();
    }

    @Override
    public boolean getDropStack()
    {
        return xbox.getBumper(Hand.kRight);
    }

    @Override
    public boolean getHighSpeed()
    {
        return panel.getSwitchR();
    }

    @Override
    public double getLowSpeed()
    {
        return xbox.getTriggerVal(Hand.kRight);
    }

    @Override
    public void setRumble(Hand hand, float value)
    {
        xbox.setRumble(hand == Hand.kLeft ? RumbleType.kLeftRumble : RumbleType.kRightRumble, value);
    }

    @Override
    /**
     * Set rumble for a specified time; starts a new thread
     * @param hand hand to rumble
     * @param value intensity of rumble (0 and 1)
     * @param length length in seconds
     */
    public synchronized void setRumble(Hand hand, float value, double length)
    {
        if(!thread.isAlive())
        {
            RumbleHand = hand;
            RumbleVal = value;
            RumbleTime = length;
            thread.start();
        }
    }
    
    public void run()
    {
        RumbleType Rumble = RumbleHand == Hand.kLeft ? RumbleType.kLeftRumble : RumbleType.kRightRumble;
        
        xbox.setRumble(Rumble, RumbleVal);
        
        try
        {
            Thread.sleep((long) (RumbleTime * 1000));
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
        }
        
        xbox.setRumble(Rumble, 0);
    }

    @Override
    public boolean getWheelCal()
    {
        return false;
    }

    @Override
    public boolean getInitialize()
    {
        return xbox.getButton(ButtonType.kLeftStick) && xbox.getButton(ButtonType.kRightStick);
    }

    @Override
    public boolean getCarryStackNoTote()
    {
        return xbox.getTrigger(Hand.kLeft);
    }

    @Override
    public boolean getLiftShake()
    {
       
        return panel.getBlackLButton();
    }

    @Override
    public boolean getLeftThumb()
    {
        return xbox.getTop(Hand.kLeft);
    }
}
