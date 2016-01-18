package com.taurus.rocker;

import com.taurus.Application;
import com.taurus.controller.Xbox;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class RockerApplication implements Application {

    RockerChassis chassis1 = new RockerChassis();
    Xbox controller = new Xbox(0);

    @Override
    public void TeleopInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodic()
    {
        // TODO Auto-generated method stub
        chassis1.drive(controller.getY(Hand.kRight),controller.getY(Hand.kLeft));
        
    }

    @Override
    public void TeleopDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousPeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModePeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledPeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledDeInit()
    {
        // TODO Auto-generated method stub

    }

}
