// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;

public class Helpers {
    private Helpers() {};

    static public int getDesiredAprilTag() {
        var ally = DriverStation.getAlliance();
        int desiredTag = 7;
        if (ally.isPresent()) {
            if (ally.get() == Alliance.Red)
            {
                desiredTag = 4;
            }
        }
        return desiredTag;
    }
}