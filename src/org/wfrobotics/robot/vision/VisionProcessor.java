package org.wfrobotics.robot.vision;

import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.vision.messages.VisionUpdate;
import org.wfrobotics.robot.vision.util.CameraListener;

/** Receives vision messages, tells robot state to interpret them **/
public class VisionProcessor implements Runnable, CameraListener
{
    private static VisionProcessor instance;

    private RobotState consumer = RobotState.getInstance();
    private String update;

    public synchronized void CameraCallback(String m)
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
        VisionUpdate v;

        synchronized (this)
        {
            if (update == null)
            {
                return;
            }
            v = VisionUpdate.fromMessage(update);
            update = null;
        }
        consumer.addVisionUpdate(v);
    }
}
