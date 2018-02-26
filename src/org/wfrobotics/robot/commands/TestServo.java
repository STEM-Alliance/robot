package org.wfrobotics.robot.commands;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class TestServo extends InstantCommand
{
    private double range = 55;
    private static boolean state = false;
    private final Servo s = new Servo(0);

    protected void initialize()
    {
        double direction = (state) ? -1 : 1;
        s.setAngle(100 + range/2 * direction);
        state = !state;
    }
}
