/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.DigitalModule;
import edu.wpi.first.wpilibj.I2C;

/**
 *
 * @author Taurus Robotics
 * interface with the arduino for actual values
 */
public class SwerveArduino
{
    private int MotorSpeed;  // actual motor speed reading
    private int MotorAngle;  // actual moto angle reading
 
    private int Address;     // arduino slave address
    private I2C Slave;
    
    // constructor
    public SwerveArduino(int SlaveAddress)
    {
        Address = SlaveAddress;
        MotorSpeed = 0;
        MotorAngle = 0;
        
        DigitalModule module = DigitalModule.getInstance(1);
        Slave = module.getI2C(SlaveAddress<<1);
        Slave.setCompatabilityMode(true);
        
    }
 
    // update values from arduino
    // return: current reading
    public SwervePoint Update()
    {
        byte[] send = new byte[5];
        byte[] send2 = new byte[5];
        
        // do read on I2C device
        if(Slave.transaction(send2, 0,send, 5) == false)
        {
          MotorSpeed = ((byte)send[1] << 8) | ((byte)send[2]); 
          MotorAngle = ((byte)send[3] << 8) | ((byte)send[4]);

            try
            {
                String a = new String();
                a = String.valueOf(send[0]) + " " + String.valueOf(MotorSpeed ) + " " + String.valueOf(MotorAngle);
                //log.info(a);
            }
            catch(Exception e)
            {
                //log.info("wtf");
            }
        }
        else
        {
            //log.info("failed");
        }
        
        //TODO will need to adjust for 0 degree position for angle relative to robot, not each wheel
 
        return Get();
    }
 
    // public get of arduino values
    public SwervePoint Get()
    {
        SwervePoint p = new SwervePoint();
        p.SetAngleHyp(MotorSpeed, MotorAngle);
 
        return p;
    }
}