package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Manages manipulators and supporting systems
public class Lift extends Subsystem {

    // TODO add container state variable
    private boolean ContainerInStack = false;
    private boolean ToteOnRails = false;
    private int TotesInStack = 0;
    private STATE_ADD_CHUTE_TOTE_TO_STACK StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
    private STATE_ADD_FLOOR_TOTE_TO_STACK StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
    private STATE_ADD_CONTAINER_TO_STACK StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
    private STATE_EJECT_STACK StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;

    private Car LiftCar;
    private Ejector StackEjector;
    private PneumaticSubsystem CylindersRails;
    private PneumaticSubsystem CylindersContainerCar;
    private PneumaticSubsystem CylindersContainerFixed;
    //private PneumaticSubsystem CylindersStackHolder;
    private SensorDigital ToteIntakeSensor;

    /**
     * Initialize lift and all objects owned by the lift
     */
    public Lift()
    {
        LiftCar = new Car();
        StackEjector = new Ejector();
        CylindersRails = new PneumaticSubsystem(Constants.CHANNEL_RAIL,
                Constants.PCU_RAIL, Constants.TIME_EXTEND_RAILS,
                Constants.TIME_CONTRACT_RAILS, Constants.CYLINDER_ACTION.EXTEND);
        CylindersContainerCar = new PneumaticSubsystem(
                Constants.CHANNEL_CONTAINER_CAR, Constants.PCU_CONTAINER_CAR,
                Constants.TIME_EXTEND_CONTAINER_CAR,
                Constants.TIME_CONTRACT_CONTAINER_CAR, Constants.CYLINDER_ACTION.CONTRACT);
        CylindersContainerFixed = new PneumaticSubsystem(
                Constants.CHANNEL_CONTAINER_FIXED, Constants.PCU_CONTAINER_FIXED,
                Constants.TIME_EXTEND_CONTAINER_FIXED,
                Constants.TIME_CONTRACT_CONTAINER_FIXED, Constants.CYLINDER_ACTION.CONTRACT);
//        CylindersStackHolder = new PneumaticSubsystem(
//                Constants.CHANNEL_STACK_HOLDER, Constants.MODULE_ID_PCU,
//                Constants.TIME_EXTEND_STACK_HOLDER,
//                Constants.TIME_CONTRACT_STACK_HOLDER, Constants.CYLINDER_ACTION.CONTRACT);
        ToteIntakeSensor = new SensorDigital(
                Constants.CHANNEL_DIGITAL_TOTE_INTAKE);
    }
    
    public void init()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
    }
    
    public boolean CarryTotes()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
        
        return CylindersRails.Extend() & LiftCar.GoToChute();
    }

    /**
     * Routine to add a new tote to existing stack from chute
     * 
     * @return true if finished
     */
    public boolean AddChuteToteToStack(int MaxTotesInStack)
    {
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
        
        SmartDashboard.putNumber("TotesInStack", TotesInStack);
        SmartDashboard.putBoolean("ToteOnRails", ToteOnRails);
        SmartDashboard.putBoolean("CylindersRails.IsExtended()", CylindersRails.IsExtended());
        
        switch (StateAddChuteToteToStack)
        {
            case INIT:
                if (TotesInStack < MaxTotesInStack) // Sanity check this should
                                                    // even be called
                {
                    if (ToteOnRails)
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                    }
                    else if (LiftCar.GoToChute() & CylindersRails.Extend())
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INTAKE_TOTE;
                    }
                }
                break;
            case INTAKE_TOTE:
                // When sensor triggered, go to next state to lift the tote
                LiftCar.GoToChute();
                if (ToteIntakeSensor.IsOn())
                {
                    StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                }
                break;
            case LIFT_TOTE:
                if (LiftCar.GoToStack())
                {
                    ToteOnRails = false;

                    if (ContainerInStack)
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.HANDLE_CONTAINER;
                    }
                    else
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.RESET;
                    }
                }
                break;
            case HANDLE_CONTAINER:
                LiftCar.GoToStack();
                if (TotesInStack == 0)
                {
                    if (GetCylindersContainerFixed().Contract())
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.RESET;
                    }
                }
                else
                {
                    StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.RESET;
                }
                break;
            case RESET:
                StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
                TotesInStack = TotesInStack + 1;
                break;
            default:
                // TODO: Put error condition here
                break;
        }
        
        SmartDashboard.putNumber("StateAddChuteToteToStack", StateAddChuteToteToStack.ordinal());

        // If the sensor triggered, and we have 5 totes, we have six totes and
        // this method is "done"
        return TotesInStack >= MaxTotesInStack && ToteIntakeSensor.IsOn();
    }

    /**
     * Routine to add a new tote to existing stack from floor
     * 
     * @return true if finished
     */
    public boolean AddFloorToteToStack(int MaxTotesInStack)
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
        
        switch (StateAddFloorToteToStack)
        {
            case INIT:
                if (ToteOnRails)
                {
                    if (TotesInStack < MaxTotesInStack)
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.LIFT_TOTE;
                    }
                }
                else if (LiftCar.GoToBottom() & CylindersRails.Contract())
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INTAKE_TOTE;
                }
                break;
            case INTAKE_TOTE:
                if (ToteIntakeSensor.IsOn())
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.GRAB_TOTE;
                }
                break;
            case GRAB_TOTE:
                if (CylindersRails.Extend())
                {
                    if (TotesInStack < MaxTotesInStack)
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.LIFT_TOTE;
                    }
                    else
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.RESET;
                    }
                }
                break;
            case LIFT_TOTE:
                if (LiftCar.GoToStack())
                {
                    ToteOnRails = false;

                    if (ContainerInStack)
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.HANDLE_CONTAINER;
                    }
                    else
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.RESET;
                    }
                }
                break;
            case HANDLE_CONTAINER:
                if (TotesInStack == 0)
                {
                    if (GetCylindersContainerFixed().Contract())
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.RESET;
                    }
                }
                else
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.RESET;
                }
                break;
            case RESET:
                if (TotesInStack < MaxTotesInStack)
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                    TotesInStack = TotesInStack + 1;
                }
                else
                {
                    if (LiftCar.GoToChute())
                    {
                        ToteOnRails = true;
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                    }
                }
                break;
        }
        
        SmartDashboard.putNumber("StateAddFloorToteToStack", StateAddFloorToteToStack.ordinal());
        
        return TotesInStack >= MaxTotesInStack
                && LiftCar.GetPosition() == POSITION_CAR.CHUTE;
    }

    /**
     * Routine to lift a container to start a new stack
     * 
     * @return true if finished
     */
    public boolean AddContainerToStack()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
        
        if (TotesInStack == 0 && ContainerInStack == false) // Sanity check this
                                                            // should even be
                                                            // called
        {
            switch (StateAddContainerToStack)
            {
                case INIT:
                    if (GetCylindersRails().Contract() & ToteIntakeSensor.IsOn())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_EXTEND;
                    }
                    break;
                // TODO: Need sensor to tell us when the container is in
                // position to secure with pneumatics
                case CONTAINER_CAR_EXTEND:
                    if (GetCylindersContainerCar().Extend())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR;
                    }
                    break;
                case LIFT_CAR:
                    if (LiftCar.GoToStack()) // TODO: Add new height for adding
                                             // container to stack?
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_FIXED_EXTEND;
                    }
                    break;
                case CONTAINER_FIXED_EXTEND:
                    if (GetCylindersContainerFixed().Extend())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_CONTRACT;
                    }
                    break;
                case CONTAINER_CAR_CONTRACT:
                    if (GetCylindersContainerCar().Contract())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LOWER_CAR;
                    }
                    break;
                case LOWER_CAR:
                    if (LiftCar.GoToChute())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.RESET;
                    }
                    break;
                case RESET:
                    if (GetCylindersRails().Extend())
                    {
                        ContainerInStack = true;
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
                    }
                    break;
                default:
                    // TODO: Put error condition here
                    break;
            }
        }
        
        SmartDashboard.putNumber("StateAddContainerToStack", StateAddContainerToStack.ordinal());
        
        return ContainerInStack;
    }

    /**
     * Place the stack on the ground, then push it onto the scoring platform
     * 
     * @return true if finished
     */
    public boolean EjectStack()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        
        switch (StateEjectStack)
        {
            case LIFT_CAR:
                if (TotesInStack == 0)
                {
                    if (ToteOnRails)
                    {
                        StateEjectStack = STATE_EJECT_STACK.LOWER_CAR;
                    }
                }
                else
                {
                    if (ToteOnRails)
                    {
                        if (LiftCar.GoToDestack()) // TODO: Add new height for
                                                   // adding
                        // container to stack?
                        {
                            StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
                        }
                    }
                    else
                    {
                        if (CylindersRails.Extend() & LiftCar.GoToStack())
                        {
                            StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
                        }
                    }
                }
                break;
            case STACK_HOLDER_CONTRACT:
                //if (GetCylindersStackHolder().Contract())
                if(true)
                {
                    ToteOnRails = true;
                    StateEjectStack = STATE_EJECT_STACK.LOWER_CAR;
                }
                break;
            case LOWER_CAR:
                if (LiftCar.GoToChute())
                {
                    StateEjectStack = STATE_EJECT_STACK.EJECT_STACK;
                }
                break;
            case EJECT_STACK:
                if (StackEjector.EjectStack())
                {
                    ToteOnRails = false;
                    StateEjectStack = STATE_EJECT_STACK.RESET;
                }
                break;
            // IMPORTANT: Resetting the Ejector needs to happen, but with a
            // seperate method call
            // This allows robot to asynchronous drive and reset the Ejector
            default:
                // TODO: Put error condition here
                break;
        }
        
        SmartDashboard.putNumber("StateEjectStack", StateEjectStack.ordinal());
        
        return StateEjectStack == STATE_EJECT_STACK.RESET;
    }

    /**
     * reset the stack eject
     * 
     * @return true if finished
     */
    public boolean ResetEjectStack()
    {
        boolean finishedReset = false;

        if (StateEjectStack == STATE_EJECT_STACK.RESET)
        {
            // IMPORTANT: Use single '&' to execute all cleanup routines
            // asynchronously
            if (StackEjector.ResetEjectStack()
                    /*& GetCylindersStackHolder().Extend()*/)
            {
                finishedReset = true;
                ContainerInStack = false;
                TotesInStack = 0;
                StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
            }
        }
        return finishedReset;
    }

    /**
     * Place the stack on the ground and release the rails
     * 
     * @return true if finished
     */
    public boolean DropStack()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        
        switch (StateEjectStack)
        {
            case LIFT_CAR:
                if (TotesInStack == 0)
                {
                    if (ToteOnRails)
                    {
                        StateEjectStack = STATE_EJECT_STACK.LOWER_CAR;
                    }
                }
                else
                {
                    if (ToteOnRails)
                    {
                        if (LiftCar.GoToDestack()) // TODO: Add new height for
                                                   // adding
                                                   // container to stack?
                        {
                            StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
                        }
                    }
                    else
                    {
                        if (LiftCar.GoToStack())
                        {
                            StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
                        }
                    }
                }
                break;
            case STACK_HOLDER_CONTRACT:
                if (true/*GetCylindersStackHolder().Contract()*/)
                {
                    ToteOnRails = true;
                    StateEjectStack = STATE_EJECT_STACK.LOWER_CAR;
                }
                break;
            case LOWER_CAR:
                if (LiftCar.GoToBottom())
                {
                    StateEjectStack = STATE_EJECT_STACK.EJECT_STACK;
                }
                break;
            case EJECT_STACK:
                if (CylindersRails.Contract())
                {
                    ToteOnRails = false;
                    StateEjectStack = STATE_EJECT_STACK.RESET;
                }
                break;
            // IMPORTANT: Resetting the Rails needs to happen, but with a
            // seperate method call
            // This allows robot to asynchronous drive and reset the Rails
            default:
                // TODO: Put error condition here
                break;
        }
        return StateEjectStack == STATE_EJECT_STACK.RESET;
    }

    /**
     * reset the stack drop
     * 
     * @return true if finished
     */
    public boolean ResetDropStack()
    {
        boolean finishedReset = false;

        if (StateEjectStack == STATE_EJECT_STACK.RESET)
        {
            // IMPORTANT: Use single '&' to execute all cleanup routines
            // asynchronously
            if (GetCylindersRails().Extend()
                    /*& GetCylindersStackHolder().Extend()*/)
            {
                finishedReset = true;
                ContainerInStack = false;
                TotesInStack = 0;
                StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
            }
        }
        return finishedReset;
    }

    /**
     * get the car object
     * 
     * @return
     */
    public Car GetCar()
    {
        return LiftCar;
    }

    /**
     * get the ejector object
     * 
     * @return
     */
    public Ejector GetEjector()
    {
        return StackEjector;
    }

    /**
     * get the tote intake sensor object
     * 
     * @return
     */
    public Sensor GetToteIntakeSensor()
    {
        return ToteIntakeSensor;
    }

    /**
     * get the cylinder rails object
     * 
     * @return the CylindersRails
     */
    public PneumaticSubsystem GetCylindersRails()
    {
        return CylindersRails;
    }

    /**
     * get the cylinder container car object
     * 
     * @return the CylindersContainerCar
     */
    public PneumaticSubsystem GetCylindersContainerCar()
    {
        return CylindersContainerCar;
    }

    /**
     * get the cylinder container fixed object
     * 
     * @return the CylindersContainerFixed
     */
    public PneumaticSubsystem GetCylindersContainerFixed()
    {
        return CylindersContainerFixed;
    }

    /**
     * get the cylinder stack object
     * 
     * @return the CylindersStackHolder
     */
//    public PneumaticSubsystem GetCylindersStackHolder()
//    {
//        return CylindersStackHolder;
//    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
}