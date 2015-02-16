package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Application extends com.taurus.Application {
    private Vision vision = new Vision();
    private Lift lift;
    private AnalogInput distance_sensor_left;
    private AnalogInput distance_sensor_right;

    private STATE_LIFT_ACTION CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;

    private SendableChooser autoChooser;
    private SendableChooser testChooser;
    private Autonomous autonomous;
    
    private LEDs leds;

    public Application()
    {
        super();

        lift = new Lift();
        vision = new Vision();
        distance_sensor_left = new AnalogInput(
                Constants.DISTANCE_SENSOR_LEFT_PIN);
        distance_sensor_right = new AnalogInput(
                Constants.DISTANCE_SENSOR_RIGHT_PIN);

        vision.Start();

        autoChooser = new SendableChooser();
        autoChooser.addDefault("Do nothing", Integer.valueOf(0));
        autoChooser.addObject("Push tote", Integer.valueOf(1));
        autoChooser.addObject("Grab tote", Integer.valueOf(2));
        autoChooser.addObject("Grab container", Integer.valueOf(3));
        autoChooser.addObject("Grab 2 totes moving left", Integer.valueOf(4));
        // autoChooser.addObject("Grab 2 totes moving right",
        // Integer.valueOf(5));
        autoChooser.addObject("Grab container, 2 totes middle",
                Integer.valueOf(6));
        // autoChooser.addObject("Grab container 2 totes side",
        // Integer.valueOf(7));
        autoChooser.addObject("Container + tote", Integer.valueOf(8));
        autoChooser.addObject("3 totes", Integer.valueOf(9));
        autoChooser.addObject("Container + 3 totes", Integer.valueOf(10));

        SmartDashboard.putData("Autonomous mode", autoChooser);
        
        testChooser = new SendableChooser();
        testChooser.addDefault("Pneumatics/Motors",
                Integer.valueOf(Constants.TEST_MODE_PNEUMATIC));
        testChooser.addObject("Actuator",
                Integer.valueOf(Constants.TEST_MODE_ACTUATOR));
        SmartDashboard.putData("Test", testChooser);
        
        leds = new LEDs();
        leds.setAll(new Color(0,0,0));
    }

    public void TeleopInitRobotSpecific()
    {
        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
        lift.init();
        leds.FlashAll(Color.Green, Color.White, 1);
    }

    
    private void UpdateDashboard()
    {
        SmartDashboard.putBoolean("ToteIntakeSensor", lift
                .GetToteIntakeSensor().IsOn());
        SmartDashboard.putNumber("Car Height", lift.GetCar().GetHeight());
        SmartDashboard.putBoolean("Zero Sensor", lift.GetCar().GetZeroSensor()
                .IsOn());
        SmartDashboard.putNumber("Actuator Raw", lift.GetCar().GetActuator()
                .GetRaw());
        SmartDashboard.putNumber("Actuator Position", lift.GetCar()
                .GetActuator().GetPositionRaw());
        SmartDashboard
                .putNumber("Distance Left", 12.402 * Math.pow(
                        distance_sensor_left.getVoltage(), -1.074) / 2.54);
        SmartDashboard
                .putNumber("Distance Right", 12.402 * Math.pow(
                        distance_sensor_right.getVoltage(), -1.074) / 2.54);

        SmartDashboard.putNumber("TotesInStack", lift.GetTotesInStack());
        SmartDashboard.putBoolean("ToteOnRails", lift.GetToteOnRails());
        SmartDashboard.putBoolean("ContainerInStack",
                lift.GetContainerInStack());
        SmartDashboard.putBoolean("CylindersRails.IsExtended()", lift
                .GetCylindersRails().IsExtended());

        SmartDashboard.putString("CurrentLiftAction_",
                CurrentLiftAction.toString());
        SmartDashboard.putString("StateAddChuteToteToStack", lift
                .GetStateAddChuteToteToStack().toString());
        SmartDashboard.putString("StateEjectStack", lift.GetStateEjectStack()
                .toString());
        SmartDashboard.putString("StateAddContainerToStack", lift
                .GetStateAddContainerToStack().toString());
        SmartDashboard.putString("StateAddFloorToteToStack", lift
                .GetStateAddFloorToteToStack().toString());

    }

    public void TeleopPeriodicRobotSpecific()
    {
        leds.run();
        UpdateDashboard();

        if (controller.getCarHome())
        {
            lift.GetCar().GoToZero();
        }
        else if (controller.getCarTop())
        {
            lift.GetCar().GoToTop();
        }
        else if (controller.getEjector())
        {
            if (lift.GetEjector().GetCylindersPusher().IsExtended())
            {
                lift.GetEjector().GetCylindersPusher().Contract();
            }
            else
            {
                lift.GetEjector().GetCylindersPusher().Extend();
            }
        }

        else
        {
            if (controller.getStopAction())
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
            else if (controller.getDropStack())
            {
                CurrentLiftAction = STATE_LIFT_ACTION.DROP_STACK;
            }

            switch (CurrentLiftAction)
            {
                case ADD_CHUTE_TOTE:
                    if (lift.AddChuteToteToStack(5))
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;

                case ADD_FLOOR_TOTE:
                    if (lift.AddFloorToteToStack(5))
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;

                case ADD_CONTAINER:
                    if (lift.AddContainerToStack())
                    {
                        CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                    }
                    break;

                case EJECT_STACK:
                    if (!lift.GetEjector().GetCylindersPusher().IsExtended())
                    {
                        if (lift.EjectStack())
                        {
                            CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                        }
                    }
                    else
                    {
                        if (lift.ResetEjectStack())
                        {
                            CurrentLiftAction = STATE_LIFT_ACTION.NO_ACTION;
                        }
                    }
                    break;

                case DROP_STACK:
                    if (lift.DropStack())
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

    }

    public void TeleopDeInitRobotSpecific()
    {

    }

    public void AutonomousInitRobotSpecific()
    {
        drive.ZeroGyro();
        int automode = ((Integer) autoChooser.getSelected()).intValue();
        autonomous = new Autonomous(drive, lift, vision, automode);
        leds.setAll(new Color(0,0xfff,0));
    }

    public void AutonomousPeriodicRobotSpecific()
    {
        lift.GetCar().ZeroIfNeeded();
        UpdateDashboard();
        autonomous.Run();
    }

    public void AutonomousDeInitRobotSpecific()
    {
    }

    public void TestModeInitRobotSpecific()
    {
    }

    public void TestModePeriodicRobotSpecific()
    {
        boolean button1 = controller.getRawButton(1);
        boolean button2 = controller.getRawButton(2);
        boolean button3 = controller.getRawButton(3);
        boolean button4 = controller.getRawButton(4);
        boolean button5 = controller.getRawButton(5);
        boolean button6 = controller.getRawButton(6);
        boolean button15 = controller.getRawButton(15);
        boolean button16 = controller.getRawButton(16);
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
                else if (button4)
                {
                    testCylinders = lift.GetCylindersStackHolder();
                }
                else if (button5)
                {
                    testCylinders = lift.GetEjector().GetCylindersStop();
                }
                else if (button6)
                {
                    testCylinders = lift.GetEjector().GetCylindersPusher();
                }
                else
                {
                    testCylinders = lift.GetCylindersRails();
                    action = false;
                }

                if (action)
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
                if (button15)
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(.7);
                }
                else if (button16)
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(-.7);
                }
                else
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(0);
                }
                break;
            case Constants.TEST_MODE_ACTUATOR:
                if (button1)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.ZERO);
                }
                else if (button2)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.CHUTE);
                }
                else if (button3)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.DESTACK);
                }
                else if (button4)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.STACK);
                }
                else if (button5)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.CHUTE);
                }
                else if (button6)
                {
                    lift.GetCar().SetPosition(LIFT_POSITIONS_E.CONTAINER_STACK);
                }
                else
                {
                    lift.GetCar().GetActuator().SetSpeedRaw(0);
                    // lift.GetEjector().SetMotors(0);
                }
                break;
            default:
                break;
        }

        lift.GetCar().ZeroIfNeeded();
        UpdateDashboard();
    }

    public void TestModeDeInitRobotSpecific()
    {

    }

    public void DisabledInitRobotSpecific()
    {

    }

    public void DisabledPeriodicRobotSpecific()
    {
        lift.GetCar().ZeroIfNeeded();
        UpdateDashboard();

    }

    public void DisabledDeInitRobotSpecific()
    {

    }
}
