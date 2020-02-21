/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Config;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static int[] intakePistonNumbers;
    public static int intakerNumber;
    public static double intakeSpeed;
    public static int magazineBeltNumber;
    public static int[] stopperNumbers;
    public static double magazineSpeed;
    public static double aimMotorSpeed;
    public static double shootMotorSpeed;
    public static int aimMotorNumber;
    public static int shootMotorNumber;
    public static double pizzaMotorSpeed;
    public static int pizzaMotorNumber;
    public static Value intakePistonOn;
    public static Value intakePistonOff;
    public static Value stopperOn;
    public static Value stopperOff;
    public static Value hookUpValue;
    public static Value hookDownValue;
    public static int[] hook;
}
