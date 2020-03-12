/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Config;

// import jdk.javadoc.internal.doclets.formats.html.resources.standard_ja;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static int[] intakePistonNumbers={7,6};
    public static int intakerNumber=7;
    public static double intakeSpeed=0.5;
    public static int magazineBeltNumber=30;
    public static int[] stopperNumbers={4,5};
    public static double magazineSpeed=0.5;
    public static double aimMotorSpeed=0.5;
    public static double shootMotorSpeed=1.0;
    public static int aimMotorNumber = 1;
    public static int shootMotorNumber=8;
    public static double pizzaMotorSpeed=0.5;
    public static int pizzaMotorNumber;
    public static double intakeMagSpeed=0.5;
    public static int winchNumber=9;
    public static int cameraRelayNumber=0;

    // public static Value intakePistonOn;
    // public static Value intakePistonOff;
    // public static Value stopperOn;
    // public static Value stopperOff;
    // public static Value hookUpValue;
    // public static Value hookDownValue;
    public static int[] hookOne = { 3,2 };
    public static int[] hookTwo = { 0,1};
    public static int hoodSwitch = 6;
}
