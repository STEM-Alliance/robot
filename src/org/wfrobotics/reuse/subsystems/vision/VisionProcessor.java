package org.wfrobotics.reuse.subsystems.vision;

import org.wfrobotics.reuse.subsystems.vision.CameraServer.CameraListener;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.RobotState;

// TODO try wait/notify, with volatile flag or Atomic boolean. Need sleep or something in camera too.
// TODO Could go on future fast task

/** Receives vision messages, tells robot state to interpret them **/
public class VisionProcessor implements Runnable, CameraListener
{
    private static VisionProcessor instance = null;

    private RobotState consumer = RobotState.getInstance();
    private VisionMessageTargets update;

    @Override
    public void Notify(VisionMessageTargets message)
    {
        // TODO handle other message types
        update = message;
//        if (m.startsWith(VisionUpdate.sGetType()))
//        {
//            update = m;
//        }
    }

    public VisionProcessor()
    {
        CameraServer.getInstance().AddListener(this);
        new Thread(this).start();
    }

    public static VisionProcessor getInstance()
    {
        if (instance == null) { instance = new VisionProcessor(); }
        return instance;
    }

    public void run()
    {
        while (true)
        {
            VisionMessageTargets v = null;

            synchronized (this)
            {
                if (update == null)
                {
                    v = update;
                    update = null;
                }
            }

            if (v != null)
            {
                consumer.addVisionUpdate(v);
            }

            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                new HerdLogger(this.getClass()).debug("Vision Processor", e.getMessage());
            }
        }
    }

}
