package org.wfrobotics.reuse.subsystems.vision;

import org.wfrobotics.reuse.subsystems.vision.CameraServer.CameraListener;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.vision.messages.VisionUpdate;

// TODO try wait/notify, with volatile flag or Atomic boolean. Need sleep or something in camera too.
// TODO Could go on future fast task

/** Receives vision messages, tells robot state to interpret them **/
public class VisionProcessor implements Runnable, CameraListener
{
    private static VisionProcessor instance = null;

    private RobotState consumer = RobotState.getInstance();
    private String update;

    public void CameraCallback(String m)
    {
        if (m.startsWith(VisionUpdate.sGetType()))
        {
            update = m;
        }
    }

    public VisionProcessor()
    {
        CameraServer.getInstance().register(this);
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
            VisionUpdate v = null;

            synchronized (this)
            {
                if (update == null)
                {
                    v = VisionUpdate.fromMessage(update);
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
