package org.wfrobotics.robot.vision.util;

/** For anybody registering to receive updates from camera server **/
public interface CameraListener
{
    public void CameraCallback(String m);
}
