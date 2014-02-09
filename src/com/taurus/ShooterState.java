/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

/**
 * This class acts as an enumeration for the shooter's state machine
 * since apparently the version of Java used in FRC does not support actual
 * enumerations.
 * @author Tanner Danzey < arkaniad AT gmail DOT com >
 */
public class ShooterState {
    // Define the states
    public static int Start = 0;
    public static int RetractPin = 1;
    public static int RetractPinWait = 2;
    public static int SetFiringArm = 3;
    public static int SetFiringArmWait = 4;
    public static int SetFiringPin = 5;
    public static int SetfiringPinWait = 6;
    public static int RetractFiringMech = 7;
    public static int RetractFiringMechWait = 8;
    public static int FireReady = 9;
    public static int FireWait = 10;
}
