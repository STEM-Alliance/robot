package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Application extends com.taurus.Application
{
    private Vision vision = new Vision();
    private Lift lift;
        
    private STATE_LIFT_ACTION CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;

    private SendableChooser autoChooser;
    private SendableChooser testChooser;
    private Autonomous autonomous;
    
    public Application()
    {
        super();

        lift = new Lift();
        vision = new Vision();
        
        vision.Start();
        
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Do nothing", Integer.valueOf(0));
        autoChooser.addObject("Push tote", Integer.valueOf(1));
        autoChooser.addObject("Grab tote", Integer.valueOf(2));
        autoChooser.addObject("Grab container", Integer.valueOf(3));
        autoChooser.addObject("Grab 2 totes moving left", Integer.valueOf(4));
//        autoChooser.addObject("Grab 2 totes moving right", Integer.valueOf(5));
        autoChooser.addObject("Grab container, 2 totes middle", Integer.valueOf(6));
//        autoChooser.addObject("Grab container 2 totes side", Integer.valueOf(7));
        autoChooser.addObject("Container + tote", Integer.valueOf(8));
        autoChooser.addObject("3 totes", Integer.valueOf(9));
        autoChooser.addObject("Container + 3 totes", Integer.valueOf(10));
        
        SmartDashboard.putData("Autonomous mode", autoChooser);
    }
    
    public void TeleopInitRobotSpecific()
    {
        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
        lift.init();
    }

    public void TeleopPeriodicRobotSpecific()
    {
        SmartDashboard.putNumber("Car Height", lift.GetCar().GetHeight() );
        SmartDashboard.putBoolean("Zero Sensor", lift.GetCar().GetZeroSensor().IsOn());
        SmartDashboard.putNumber("Actuator Raw", lift.GetCar().GetActuator().GetRaw());
        SmartDashboard.putNumber("Actuator Position", lift.GetCar().GetActuator().GetPositionRaw());


        if(controller.getTrigger(Hand.kRight))
        {
            lift.GetCar().GoToZero();
        }
        else if(controller.getTrigger(Hand.kLeft))
        {
            lift.GetCar().GoToTop();
        }
        else
        {
            if(controller.getBrake())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                lift.init();
            }
            
            lift.GetCar().ZeroIfNeeded();
            
            // if not currently running anything, try and find a button
            // do this first so we can run the action this run rather than
            // waiting until the next time this task is ran
            
            // find if a button is pressed, then execute
            if (controller.getAddChuteTote())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_CHUTE_TOTE;
            }
            else if (controller.getAddFloorTote())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_FLOOR_TOTE;
            }
            else if (controller.getAddContainer())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.ADD_CONTAINER;
            }
            else if (controller.getEjectStack())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.EJECT_STACK;
            }

            switch(CurrentLiftAction)
            {
                case ADD_CHUTE_TOTE:
                    if(lift.AddChuteToteToStack(5))
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;
                    
                case ADD_FLOOR_TOTE:
                    if(lift.AddFloorToteToStack(5))
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;
                    
                case ADD_CONTAINER:
                    if(lift.AddContainerToStack())
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;
                    
                case EJECT_STACK:
                    if(lift.EjectStack())
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;
                    
                case NO_ACTION:
                default:
                    lift.GetCar().GetActuator().SetSpeedRaw(0);
                    break;
            }
        }
        
        SmartDashboard.putNumber("CurrentLiftAction", CurrentLiftAction.ordinal());
    }

    public void TeleopDeInitRobotSpecific()
    {

    }

    public void AutonomousInitRobotSpecific()
    {
        drive.ZeroGyro();
        int automode = ((Integer) autoChooser.getSelected()).intValue();
        autonomous = new Autonomous(drive, lift, vision, automode);        
    }

    public void AutonomousPeriodicRobotSpecific()
    {
        autonomous.Run();
    }

    public void AutonomousDeInitRobotSpecific()
    {   
    }
    
    public void TestModeInitRobotSpecific()
    {
        testChooser = new SendableChooser();
        testChooser.addDefault("Pneumatics", Integer.valueOf(Constants.TEST_MODE_PNEUMATIC));
        testChooser.addObject("Motors", Integer.valueOf(Constants.TEST_MODE_MOTORS));
        testChooser.addObject("Actuator", Integer.valueOf(Constants.TEST_MODE_ACTUATOR));
        SmartDashboard.putData("Test", testChooser);
    }

    public void TestModePeriodicRobotSpecific()
    {
        boolean button1 = controller.getRawButtion(1);
        boolean button2 = controller.getRawButtion(2);
        boolean button3 = controller.getRawButtion(3);
        boolean button4 = controller.getRawButtion(4);
        boolean button5 = controller.getRawButtion(5);
        boolean button6 = controller.getRawButtion(6);
        lift.GetCar().ZeroIfNeeded();

        // test modes for cylinders and motors and features.
        switch (((Integer) testChooser.getSelected()).intValue())
        {
            case Constants.TEST_MODE_PNEUMATIC:
                PneumaticSubsystem testCylinders;
                boolean action = true;

                if (button1)
                {
                    testCylinders = lift.GetCylindersRails();
                }
                else if (button2)
                {
                    testCylinders = lift.GetCylindersContainerCar();
                }
                else if (button3)
                {
                    testCylinders = lift.GetCylindersContainerFixed();
                }   
//                else if (button4)
//                {
//                    testCylinders = lift.GetCylindersStackHolder();
//                }
                else
                {
                    testCylinders = lift.GetCylindersRails();
                    action = false;
                }

                if(action)
                {
                    // Toggle selected cylinders to opposite position
                    if (testCylinders.IsExtended())
                    {
                        testCylinders.Contract();
                    }
                    else
                    {
                        testCylinders.Extend();
                    }
                }
                break;
            case Constants.TEST_MODE_MOTORS:
                if (button1)
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(1);
                }
                else if (button2)
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(-1);
                }
                else if (button3)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button4)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_BACKWARD);
                }
                else
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(0);
                    lift.GetEjector().SetMotors(0);
                }
                break;
            case Constants.TEST_MODE_ACTUATOR:
                if (button1)
                {
                    lift.GetCar().SetPosition(0);
                }
                else if (button2)
                {
                    lift.GetCar().SetPosition(1);
                }
                else if (button3)
                {
                    lift.GetCar().SetPosition(2);
                }
                else if (button4)
                {
                    lift.GetCar().SetPosition(3);
                }
                else if (button5)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button6)
                {
                    lift.GetEjector().SetMotors(Constants.MOTOR_DIRECTION_BACKWARD);
                }
                else
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(0);
                    lift.GetEjector().SetMotors(0);
                }
                break;
            default:
                break;
        }
        
        // TODO: Get the value of one sensor and report that
        SmartDashboard.putBoolean("ToteIntakeSensor", lift.GetToteIntakeSensor().IsOn());
        SmartDashboard.putNumber("Car Height", lift.GetCar().GetHeight() );
    }

    public void TestModeDeInitRobotSpecific()
    {

    }

    public void DisabledInitRobotSpecific()
    {

    }

    public void DisabledPeriodicRobotSpecific()
    {
        lift.GetCar().ZeroIfNeeded();        // TODO: Get the value of one sensor and report that
        SmartDashboard.putBoolean("ToteIntakeSensor", lift.GetToteIntakeSensor().IsOn());
        SmartDashboard.putNumber("Car Height", lift.GetCar().GetHeight() );

    }

    public void DisabledDeInitRobotSpecific()
    {

    }
}
