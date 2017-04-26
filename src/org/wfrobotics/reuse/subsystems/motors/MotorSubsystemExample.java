package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.hardware.motors.HerdMotorRotationMagPot;
import org.wfrobotics.reuse.hardware.motors.config.HerdMotorConfig;
import org.wfrobotics.reuse.hardware.motors.config.PIDControllerConfig;

public class MotorSubsystemExample {

    public MotorSubsystemExample()
    {

        PIDControllerConfig pid = new PIDControllerConfig(.5)
                                            .d(.01)
                                            .maxOutput(1);
        HerdMotorConfig motor = new HerdMotorConfig("Tester", 40, pid)
                                            .outputVoltageMax(11)
                                            .brakeMode(false);
        
        MotorSubsystem turret = new MotorPositionSubsystem(new HerdMotorRotationMagPot(motor));

        turret.set(0);
        
    }
}
