package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.Elevate;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftSubsystem extends Subsystem
{
    // TODO List of present heights
    // TODO Preset heights in configuration file

    public TalonSRX LiftMotor;
    public DigitalInput BottomSensor;
    public DigitalInput TopSensor;
    public int encoderValue;

    public LiftSubsystem()
    {
        // TODO Use Talon factory. If not position control, at least makeTalon()
        LiftMotor = new TalonSRX(18);
        BottomSensor = new DigitalInput(1);
        TopSensor = new DigitalInput(0);
        LiftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);

        // TODO Setup Position control mode

        // TODO Figure out what settings are ideal

        // TODO Poofs used brake mode on drive postion control. Why didn't ours work?

        // TODO Make sure frame rate is high, but not too high. Double check we aren't setting irrelevant frame types super as fast - we need the bandwidth for lift/drive important frames

        // TODO Can we get away with follower mode or do we need to two that try to adjust if we slip a geartooth? Ask mechanical what to do.

        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches

        // TODO Beast mode - Distance sensors to sense scale here or somewhere else? Low priority.

        // TODO There's a "state pattern" that can help us if the rules for going to/from each state gets too complex
    }

    // TODO Lift needs to hold position by default
    //      Beast mode - Can (or should we even) automatically go the height based on if we have a cube or some IO to tell our intended preset to score on?
    public void initDefaultCommand()
    {
        setDefaultCommand(new Elevate(0));
    }

    public boolean isAtTop()
    {
        return TopSensor.get();
    }

    public boolean isAtBottom()
    {
        return BottomSensor.get();
    }

    public int getEncoder()
    {
        return LiftMotor.getSelectedSensorPosition(0);
    }

    public void setSpeed(double speed)
    {
    }

    public void goToPosition(double position)
    {
    }

    // TODO Report fommatted state to RobotState. Not the height, but instead something like what the Robot can do. Ex: isSafeToExhaustScale

    // TODO Automatically zero whenever we pass by that sensor(s)

    // TODO What's the most automatic way we can score on the first layer of cube (on scale/switch) vs the second? What are the easiest xbox controls for that?

    // TODO Beast mode - The fastest lift possible probably dynamically changes it's control strategy to get to it's destination fastest
    //                   This might mean a more aggressive PID (profile) on the way down
    //                   Could go as far as using both closed and open loop control modes
}
