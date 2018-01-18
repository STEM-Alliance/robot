package org.wfrobotics.reuse.subsystems.tank;

import org.wfrobotics.reuse.commands.differential.DriveTank;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.subsystems.drive.DifferentialDrive;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A basic shifting tank drive. Can update, extend this class, or copy it to customize per year
 *
 */
public class TankSubsystem extends Subsystem implements DifferentialDrive
{
    private static TankSubsystem instance = null;

    protected TalonSRX[] motorsLeft;
    protected TalonSRX[] motorsRight;

    protected TankSubsystem()
    {
        // declare the array size
        motorsLeft = new TalonSRX[RobotMap.CAN_TANK_DRIVE_TALONS_L.length];
        // create the main talon as device 0
        motorsLeft[0] = TalonSRXFactory.makeTalon(RobotMap.CAN_TANK_DRIVE_TALONS_L[0]);       
        // create the rest as followers to device 0
        for (int i = 1; i < RobotMap.CAN_TANK_DRIVE_TALONS_L.length; i++)
        {
            motorsLeft[i] = TalonSRXFactory.makeFollowerTalon(
                    RobotMap.CAN_TANK_DRIVE_TALONS_L[i],
                    RobotMap.CAN_TANK_DRIVE_TALONS_L[0]);
        }
        
        // declare the array size
        motorsRight = new TalonSRX[RobotMap.CAN_TANK_DRIVE_TALONS_R.length];
        // create the main talon as device 0
        motorsRight[0] = TalonSRXFactory.makeTalon(RobotMap.CAN_TANK_DRIVE_TALONS_R[0]);
        // need to invert one side
        motorsRight[0].setInverted(true);
        // create the rest as followers to device 0
        for (int i = 1; i < RobotMap.CAN_TANK_DRIVE_TALONS_R.length; i++)
        {
            motorsRight[i] = TalonSRXFactory.makeFollowerTalon(
                    RobotMap.CAN_TANK_DRIVE_TALONS_R[i],
                    RobotMap.CAN_TANK_DRIVE_TALONS_R[0]);
        }
    }

    public static TankSubsystem getInstance()
    {
        if (instance == null) { instance = new TankSubsystem(); }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveTank());
    }

    public void driveBasic(HerdVector vector)
    {
        double x = vector.getX();
        double y = vector.getY();
        
        //note this math may need to be swapped, depending on gearbox orientations
        driveDifferential(y+x, y-x);
    }

    public void turnBasic(HerdVector vector)
    {
        // only take into consideration the x axis for just turning in place
        double x = vector.getX();
        
        driveDifferential(x, -x);
    }

    public void driveDifferential(double left, double right)
    {
        // only need to set the first one (0) and the rest will follow
        motorsLeft[0].set(ControlMode.PercentOutput, left);
        motorsRight[0].set(ControlMode.PercentOutput, right);
    }

    public void setBrake(boolean enable)
    {
        // if true, use Brake mode, else use Coast mode
        NeutralMode mode = enable ? NeutralMode.Brake : NeutralMode.Coast;
        motorsLeft[0].setNeutralMode(mode);
        motorsRight[0].setNeutralMode(mode);
    }

    public void setGear(boolean useHighGear)
    {
        // TODO add pneumatic control code
    }

    public void zeroGyro()
    {
        // TODO add gyro code
    }
}
