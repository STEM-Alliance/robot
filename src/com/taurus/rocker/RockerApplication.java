package com.taurus.rocker;

import com.taurus.Application;
import com.taurus.controller.RockerController;
import com.taurus.controller.Xbox;
import com.taurus.shooter.Shooter;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class RockerApplication implements Application {

    RockerChassis chassis1 = new RockerChassis();
    RockerController controller = new RockerController();
    Shooter shooter1 = new Shooter();

    
    @Override
    public void TeleopInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodic()
    {
        double right = controller.getY(Hand.kRight) * (.21 + .21 * Math.abs(controller.getTriggerVal(Hand.kLeft)));
        double left =  controller.getY(Hand.kLeft) * (.21 + .21 * Math.abs(controller.getTriggerVal(Hand.kLeft)));
        
        chassis1.tankDrive(0.5, 0.5);
        //chassis1.arcadeDrive(controller., controller.getX(Hand.kLeft));
        
        if (controller.getAButton())
        {
            shooter1.grab(.38 + .5 * controller.getTriggerVal(Hand.kRight));
        }
        else if(controller.getBButton())
        { 
            shooter1.shoot(
                    .5 + .1 * controller.getTriggerVal(Hand.kRight),
                    .7 + .1 * controller.getTriggerVal(Hand.kRight));

        }
        else
        {
            shooter1.stop();
        }

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
