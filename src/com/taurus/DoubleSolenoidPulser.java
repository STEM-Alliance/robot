package com.taurus;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class DoubleSolenoidPulser extends DoubleSolenoid {
    // Datasheet says switching time is 7 ms
    private static final double minTimeInDirection = 8.0 / 1000.0;

    private double forwardSpeed;
    private double reverseSpeed;

    Thread cycleThread;

    private volatile double dutyCycle = 0;

    public DoubleSolenoidPulser(int moduleNumber, int forwardChannel,
            int reverseChannel, double forwardSpeed, double reverseSpeed)
    {
        super(moduleNumber, forwardChannel, reverseChannel);

        this.forwardSpeed = Math.abs(forwardSpeed);
        this.reverseSpeed = -Math.abs(reverseSpeed);

        cycleThread = new Thread(new DutyCycleController());
        cycleThread.start();
    }

    @Override
    public void set(Value value)
    {
        if (value == Value.kForward)
            dutyCycle = forwardSpeed;
        else if (value == Value.kReverse)
            dutyCycle = reverseSpeed;
        else
            dutyCycle = 0;
    }

    public void setForwardSpeed(double v)
    {
        forwardSpeed = Math.abs(v);
        if (dutyCycle > 0)
            dutyCycle = forwardSpeed;
    }

    public void setReverseSpeed(double v)
    {
        reverseSpeed = -Math.abs(v);
        if (dutyCycle < 0)
            dutyCycle = reverseSpeed;
    }

    private class DutyCycleController implements Runnable {

        private double lastSwitchTime = 0;
        private Value lastDirection = Value.kOff;

        @Override
        public void run()
        {
            while (true)
            {
                double d = dutyCycle;
                double d_abs = Math.abs(d);
                
                double timeWait;

                if (lastDirection == Value.kOff)
                {
                    // How long should the solenoid stay off?
                    if (d_abs == 0)
                        timeWait = Double.POSITIVE_INFINITY;  // Forever.
                    else if (d_abs > .5)
                        timeWait = minTimeInDirection;        // As little as possible.
                    else
                        timeWait = minTimeInDirection * ((1 / d_abs) - 1);
                }
                else
                {
                    // How long should the solenoid stay on?
                    if (d_abs >= 1)
                        timeWait = Double.POSITIVE_INFINITY;  // Forever.
                    else if (d_abs < .5)
                        timeWait = minTimeInDirection;        // As little as possible.
                    else
                        timeWait = minTimeInDirection * d_abs / (1 - d_abs);
                }

                double t = Timer.getFPGATimestamp();
                double time_left = lastSwitchTime + timeWait - t;

                if (time_left > 0)
                {
                    // Not yet time to toggle direction, wait a little bit.
                    try
                    {
                        Thread.sleep(Math.min(5, (int) (1000 * time_left / 3)));
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                else
                {
                    // Toggle the direction.
                    if (lastDirection == Value.kOff)
                    {
                        lastDirection = d > 0 ? Value.kForward : Value.kReverse;
                    }
                    else
                    {
                        lastDirection = Value.kOff;
                    }

                    // Save the last time the direction was toggled.
                    lastSwitchTime = t;
                }

                // Apply the solenoid direction.
                setPrivate(lastDirection);
            }
        }
    }

    private void setPrivate(Value value)
    {
        super.set(value);
    }

}
