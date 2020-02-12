package frc.robot.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import frc.robot.utilities.ConsoleLogger;

/** @author STEM Alliance of Fargo Moorhead */
public abstract class TalonConfig
{
    /** Minimum configuration to build a SAFM closed-loop group of Talons
     * @author STEM Alliance of Fargo Moorhead
     */
    public static class ClosedLoopConfig
    {
        public final String name;
        public final List<MasterConfig> masters;
        public final List<Gains> gains;

        public ClosedLoopConfig(String name, MasterConfig[] masters, Gains[] gains)
        {
            this.name = name;
            this.masters = Arrays.asList(masters);
            this.gains = Arrays.asList(gains);
        }

        public Gains gainsByName(String name)
        {
            for (Gains gain : gains)
            {
                if (gain.name == name)
                {
                    return gain;
                }
            }
            ConsoleLogger.defcon1(String.format("No gains by name: %s", name));
            System.out.println(gains);
            return null;
        }

        public String toString()
        {
            final StringBuilder sb = new StringBuilder();

            sb.append("Closed Loop: " + name + "\n");
            for (MasterConfig master : masters)
            {
                sb.append("---\n");
                sb.append(master);
            }
            for (Gains gain : gains)
            {
                sb.append("---\n");
                sb.append(gain);
            }
            return sb.toString();
        }
    }

    /** @author STEM Alliance of Fargo Moorhead */
    public static class MasterConfig
    {
        public final int kAddress;
        public final boolean kInvert;
        public Optional<Boolean> kSensorPhase;
        public Optional<List<FollowerConfig>> kFollowers;

        public MasterConfig(int address, boolean invert, FollowerConfig... followers)
        {
            kAddress = address;
            kInvert = invert;
            kSensorPhase = Optional.empty();
            if (followers.length == 0)
            {
                kFollowers = Optional.empty();
            }
            else
            {
                kFollowers = Optional.ofNullable(Arrays.asList(followers));
            }
        }

        public MasterConfig(int address, boolean invert, boolean sensorPhase, FollowerConfig... followers)
        {
            kAddress = address;
            kInvert = invert;
            kSensorPhase = Optional.of(sensorPhase);
            if (followers.length == 0)
            {
                kFollowers = Optional.empty();
            }
            else
            {
                kFollowers = Optional.ofNullable(Arrays.asList(followers));
            }
        }

        public boolean areFollowersAllTalons()
        {
            boolean result = true;

            if (!kFollowers.isPresent())
            {
                return false;
            }

            for (FollowerConfig follower : kFollowers.get())
            {
                result &= follower.kIsTalon;
            }
            return result;
        }

        public String toString()
        {
            final StringBuilder sb = new StringBuilder();

            sb.append("Master: " + kAddress + "\n");
            sb.append("Invert: " + kInvert + "\n");
            if (kSensorPhase.isPresent())
            {
                sb.append("SensorPhase: " + kSensorPhase.get() + "\n");
            }
            if (kFollowers.isPresent())
            {
                for(FollowerConfig follower : kFollowers.get())
                {
                    sb.append("---\n");
                    sb.append(follower);
                }
            }
            return sb.toString();
        }
    }

    /** @author STEM Alliance of Fargo Moorhead */
    public static class FollowerConfig
    {
        public final int kAddress;
        public final boolean kIsTalon;
        public Optional<Boolean> kInvert;

        public FollowerConfig(int address, boolean isTalon)
        {
            kAddress = address;
            kIsTalon = isTalon;
            kInvert = Optional.empty();  // Use master's invert
        }

        public FollowerConfig(int address, boolean isTalon, boolean invert)
        {
            kAddress = address;
            kIsTalon = isTalon;
            kInvert = Optional.of(invert);  // May differ from master
        }

        public String toString()
        {
            final StringBuilder sb = new StringBuilder();

            sb.append("Follower: " + kAddress + "\n");
            sb.append("IsTalon: " + kIsTalon + "\n");
            if (kInvert.isPresent())
            {
                sb.append("Invert: " + kInvert + "\n");
            }
            return sb.toString();
        }
    }

    /** Tunable parameters for closed-loop Talons. Having more than one set of gains allows gains scheduling.
     * @author STEM Alliance of Fargo Moorhead
     */
    public static class Gains
    {
        public final String name;
        public final int kSlot;
        public final double kP;
        public final double kI;
        public final double kD;
        public final double kF;
        public final int kIZone;
        public final Optional<Integer> kCruiseVelocity;
        public final Optional<Integer> kAcceleration;

        public Gains(String name, int slot, double kP, double kI, double kD, double kF, int iZone)
        {
            this(name, slot, kP, kI, kD, kF, iZone, 0, 0);
        }

        public Gains(String name, int slot, double kP, double kI, double kD, double kF, int iZone, int cruiseVelocity, int acceleration)
        {
            this.name = name;
            kSlot = slot;
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = kF;
            kIZone = iZone;
            kCruiseVelocity = (cruiseVelocity > 0) ? Optional.of(cruiseVelocity) : Optional.empty();
            kAcceleration =  (acceleration > 0) ? Optional.of(acceleration) : Optional.empty();
        }

        public String toString()
        {
            final StringBuilder sb = new StringBuilder();

            sb.append("Gain: " + name + "\n");
            sb.append("Slot: " + kSlot + "\n");
            sb.append(String.format("P: %.3g\n", kP));
            sb.append(String.format("I: %.3g\n", kI));
            sb.append(String.format("D: %.3g\n", kD));
            sb.append(String.format("F: %.3g\n", kF));
            sb.append("IZone: " + kIZone + "\n");
            if (kCruiseVelocity.isPresent())
            {
                sb.append("CruiseVelocity: " + kCruiseVelocity.get() + "\n");
            }
            if (kAcceleration.isPresent())
            {
                sb.append("Acceleration: " + kAcceleration.get() + "\n");
            }
            return sb.toString();
        }
    }
}