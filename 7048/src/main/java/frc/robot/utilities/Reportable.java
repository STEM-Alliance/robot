package frc.robot.utilities;

/**
 * Preferred way to output any perodic debugging robot information. Improves {@link Scheduler} performance by allowing commands to run first.
 * @author STEM Alliance of Fargo Moorhead
 */
public interface Reportable
{
    /**
     * Outputs this object's state.
     * Output is delayed until <b>after</b> the {@link Scheduler}.
     * Keeps slow operations from affecting the {@link Scheduler} period.
     */
    public void reportState();
}
