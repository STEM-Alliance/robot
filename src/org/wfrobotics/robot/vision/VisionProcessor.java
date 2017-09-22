package org.wfrobotics.robot.vision;

import java.util.concurrent.ConcurrentLinkedQueue;

public class VisionProcessor implements Runnable, CameraListener
{
    private static VisionProcessor instance;

    public ConcurrentLinkedQueue<String> queue;

    public void CameraCallback(String m)
    {
        queue.add(m);  // TODO - Do we block long enough to need a queue?
    }

    public VisionProcessor()
    {
        queue = new ConcurrentLinkedQueue<String>();
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
        // TODO - Process camera updates into vision state
        // TODO - Update robot state when vision state changes
    }
}
