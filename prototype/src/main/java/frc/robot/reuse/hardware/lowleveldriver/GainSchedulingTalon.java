package frc.robot.reuse.hardware.lowleveldriver;

import java.util.List;


import frc.robot.reuse.config.TalonConfig.Gains;
import frc.robot.reuse.utilities.ConsoleLogger;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/** Gain scheduling <b>switches the gains used based on the operating conditions</b> in order to improve performance
 * @author STEM Alliance of Fargo Moorhead
 */
public class GainSchedulingTalon extends TalonSRX
{
    private final int[] kSlots;
    private final int[] kCruiseVelocitys;
    private final int[] kAccelerations;

    public GainSchedulingTalon(int deviceID, List<Gains> gains)
    {
        super(deviceID);
        kSlots = new int[gains.size()];
        kCruiseVelocitys = new int[gains.size()];
        kAccelerations = new int[gains.size()];

        for (int index = 0; index < gains.size(); index++)
        {
            kSlots[index] = gains.get(index).kSlot;
            kCruiseVelocitys[index] = gains.get(index).kCruiseVelocity.orElse(0);
            kAccelerations[index] = gains.get(index).kAcceleration.orElse(0);
        }
    }

    /** Allows <b>slot-specific cruise velocity and acceleration</b> for motion magic. <b>Call once</b> before first use of these gains. */
    public void setGains(int slot)
    {
        if (slot < kSlots.length)
        {
            configMotionCruiseVelocity(kCruiseVelocitys[slot], 10);  // TODO Timeout of 10 or 0?
            configMotionAcceleration(kCruiseVelocitys[slot], 10);
            selectProfileSlot(slot, 0);
        }
        else
        {
            ConsoleLogger.warning("Talon slot does not have config");
        }
    }
}
