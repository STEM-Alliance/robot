package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public abstract class RobotConfig implements TankConfigSupplier
{
    private static RobotConfig instance = null;

    // TODO Set to zero, test that PCM and PDP both can be zero
    public final int kPCMAddress = 0;

    //                      Intake
    // _________________________________________________________________________________
    public final int kIntakeAddressL = 19;
    public final int kIntakeAddressR = 20;
    public final int kIntakeSolenoidF = 2;
    public final int kIntakeSolenoidR = 3;
    public int kIntakeInfrared;

    public boolean kIntakeInvertR;
    public boolean kIntakeInvertL;
    public double kIntakeDistanceToCube;
    public double kJawsTimeoutSeconds;

    //                      Lift
    // _________________________________________________________________________________
    public boolean kLiftTuning = false;

    public ClosedLoopConfig LIFT_CLOSED_LOOP;
    public LimitSwitchNormal[] LIFT_LIMIT_SWITCH_NORMALLY;
    public int kLiftTicksStartup = -1500;

    //                      Winch
    // _________________________________________________________________________________
    public int kWinchAddress;
    public boolean kWinchInvert;
    public double kWinchSpeed;

    //                      Wrist
    // _________________________________________________________________________________
    public boolean kWinchTuning = false;
    public ClosedLoopConfig WRIST_CLOSED_LOOP;
    public double kWristDeadband;

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = RobotConfigPicker.get(new RobotConfig[] {
                new HerdVictor(),
            });
        }
        return instance;
    }
}

