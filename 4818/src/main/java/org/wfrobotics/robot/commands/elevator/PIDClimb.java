package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.reuse.math.control.PID;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDClimb extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final Climb climb = Climb.getInstance();

    private final boolean desired;

    private static final double kPitchIZone = 1.0;  // Degrees Error
    private final PID pid;

    public PIDClimb(boolean liftNotClimb)
    {
        requires(elevator);
        requires(climb);

        final RobotConfig config = RobotConfig.getInstance();
        final Preferences prefs = Preferences.getInstance();
        final double p = prefs.getDouble("p_elevator", config.kVisionP);
        final double i = prefs.getDouble("i_elevator", config.kVisionI);
        final double d = prefs.getDouble("d_elevator", config.kVisionD);

        pid = new PID(p, i, d, kPitchIZone);
        desired = liftNotClimb;
    }

    protected void execute()
    {
        SmartDashboard.putNumber("Gyro Pitch", TankSubsystem.getInstance().getGyroPitch() );
        climb.setOpenLoop(IO.getInstance().getClimbArmsStick());
        elevator.setOpenLoop(IO.getInstance().getElevatorStick());
        climb.setPullers(1.0);

        final double elevatorCorrection = getPitchCorrection();
        
        elevator.setOpenLoop(elevatorCorrection);
    }
    private double getPitchCorrection()
    {
        double percentOutput = 0.0;
            
        if (Elevator.getInstance().getPosition() == 0)
        {
            pid.reset();
        }

        final double error = TankSubsystem.getInstance().getGyroPitch();
        final double sign = Math.signum(error);
        final double correction = pid.update(Timer.getFPGATimestamp(), sign);
        
        if (Math.abs(correction) > 0.01 && !Elevator.getInstance().getLiftNotClimb())
        {
            percentOutput = correction;
        }
        else
        {
            pid.reset();
        }

        return percentOutput;
    }
    protected void initialize()
    {
        elevator.setShifter(desired);
        TankSubsystem.getInstance().zeroGyroPitch();
    }

    protected boolean isFinished()
    {
        return false;
    }
    protected void end()
    {
        elevator.setShifter(!desired);
        climb.setPullers(0.0);
    }
}