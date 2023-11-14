// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Filesystem;
import java.io.File;

import javax.lang.model.util.ElementScanner14;

/**
 * Do NOT add any static variables to this class, or any initialization at all.
 * Unless you know what
 * you are doing, do not modify this file except to change the parameter class
 * to the startRobot
 * call.
 */
public final class Main {
    private Main() {
    }

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>
     * If you change your main robot class, change the parameter type.
     */
    public static void main(String... args) {
        var dir = Filesystem.getOperatingDirectory();
        File[] files = dir.listFiles();
        System.out.println("***Checking robot configuration");
        for (File file : files) {
            System.out.println(file.getName());
            if (file.getName().contains("4360.txt"))
            {
                System.out.println("***Configured as Moorhead team 4360 robot");
                RobotBase.startRobot(Robot4360::new);
            }
            else if(file.getName().contains("testboard.txt")) 
            {
                System.out.println("***Configured as Testboard robot");
                RobotBase.startRobot(TestBoard::new);  
            }
            
        }
        System.out.println("***ERROR, please copy a text file for the correct team onto the unit");
    }
}
