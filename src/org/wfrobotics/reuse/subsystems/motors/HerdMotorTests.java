package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.ConfigMotor.*;
import org.wfrobotics.reuse.subsystems.motors.ConfigPID.*;

public class HerdMotorTests {

    public HerdMotorTests()
    {
        ConfigPIDBuilder pid = new ConfigPIDBuilder(.5)
                                    .d(.01)
                                    .maxOutput(1);
        ConfigMotorBulider motor = new ConfigMotorBulider("Tester", 40, pid)
                                           .outputVoltageMax(11)
                                           .brakeMode(false);
        
        HerdMotorRotationMagPot potMotor = new HerdMotorRotationMagPot(motor);
        
        potMotor.set(0);
    }
}
