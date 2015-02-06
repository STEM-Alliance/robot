package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

// Manages manipulators and supporting systems
public class Lift {
    // TODO add container state variable
    private boolean ContainerInStack = false;
    private int TotesInStack = 0;
    STATE_ADD_CHUTE_TOTE_TO_STACK StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
    STATE_ADD_FLOOR_TOTE_TO_STACK StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
    STATE_ADD_CONTAINER_TO_STACK StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
    STATE_EJECT_STACK StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;

    Car LiftCar;
    Ejector StackEjector;
    PneumaticSubsystem CylindersRails;
    PneumaticSubsystem CylindersContainerCar;
    PneumaticSubsystem CylindersContainerFixed;
    PneumaticSubsystem CylindersStackHolder;
    SensorDigital ToteIntakeSensor;

    // Initialize lift and all objects owned by the lift
    public Lift()
    {
        LiftCar = new Car();
        StackEjector = new Ejector();
        CylindersRails = new PneumaticSubsystem(Constants.CHANNEL_RAIL,
                 Constants.MODULE_ID_PCU, Constants.TIME_EXTEND_RAILS, Constants.TIME_CONTRACT_RAILS,
                true);
        CylindersContainerCar = new PneumaticSubsystem(
                Constants.CHANNEL_CONTAINER_CAR,
                Constants.MODULE_ID_PCU,
                Constants.TIME_EXTEND_CONTAINER_CAR,
                Constants.TIME_CONTRACT_CONTAINER_CAR, false);
        CylindersContainerFixed = new PneumaticSubsystem(
                Constants.CHANNEL_CONTAINER_FIXED,
                Constants.MODULE_ID_PCU,
                Constants.TIME_EXTEND_CONTAINER_FIXED,
                Constants.TIME_CONTRACT_CONTAINER_FIXED, false);
        CylindersStackHolder = new PneumaticSubsystem(
                Constants.CHANNEL_STACK_HOLDER,
                Constants.MODULE_ID_PCU,
                Constants.TIME_EXTEND_STACK_HOLDER,
                Constants.TIME_CONTRACT_STACK_HOLDER, false);
        ToteIntakeSensor = new SensorDigital(
                Constants.CHANNEL_DIGITAL_TOTE_INTAKE);
    }

    // Routine to add a new tote to existing stack from chute
    public boolean AddChuteToteToStack()
    {
        if (TotesInStack < 5) // Sanity check this should even be called
        {
            switch (StateAddChuteToteToStack)
            {
                case INIT:
                    if (LiftCar.GoToChute())
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INTAKE_TOTE;
                    }
                    break;
                case INTAKE_TOTE:
                    // When sensor triggered, go to next state to lift the tote
                    if (ToteIntakeSensor.IsOn())
                    {
                        StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                    }
                    break;
                case LIFT_TOTE:
                    if (LiftCar.GoToStack())
                    {
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
                    if (TotesInStack == 0)
                    {
                        if (CylindersContainerFixed.Contract())
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
        }

        // If the sensor triggered, and we have 5 totes, we have six totes and
        // this method is "done"
        return TotesInStack == 5 && ToteIntakeSensor.IsOn();
    }

    // Routine to add a new tote to existing stack from floor
    public boolean AddFloorToteToStack()
    {
        switch (StateAddFloorToteToStack)
        {
            case INIT:
                if (LiftCar.GoToBottom())
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INTAKE_TOTE;
                }
                break;
            case INTAKE_TOTE:
                if (ToteIntakeSensor.IsOn())
                {
                    if (TotesInStack < 5)
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
                    if (CylindersContainerFixed.Contract())
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
                if (TotesInStack < 5)
                {
                    StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                    TotesInStack = TotesInStack + 1;
                }
                else
                {
                    if (LiftCar.GoToChute())
                    {
                        StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                    }
                }
                break;
        }
        return TotesInStack == 5 && LiftCar.GetPosition() == POSITION_CAR.CHUTE;
    }

    // Routine to lift a container to start a new stack
    public boolean AddContainerToStack()
    {
        if (TotesInStack == 0 && ContainerInStack == false) // Sanity check this
                                                            // should even be
                                                            // called
        {
            switch (StateAddContainerToStack)
            {
                case INIT:
                    if (CylindersRails.Contract())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_EXTEND;
                    }
                    break;
                // TODO: Need sensor to tell us when the container is in
                // position to secure with pneumatics
                case CONTAINER_CAR_EXTEND:
                    if (CylindersContainerCar.Extend())
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
                    if (CylindersContainerFixed.Extend())
                    {
                        StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_CONTRACT;
                    }
                    break;
                case CONTAINER_CAR_CONTRACT:
                    if (CylindersContainerCar.Contract())
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
                    if (CylindersRails.Extend())
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
        return ContainerInStack;
    }

    // Place the stack on the ground, then push it onto the scoring platform
    public boolean EjectStack()
    {
        switch (StateEjectStack)
        {
            case LIFT_CAR:
                if (LiftCar.GoToDestack()) // TODO: Add new height for adding
                                           // container to stack?
                {
                    StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
                }
                break;
            case STACK_HOLDER_CONTRACT:
                if (CylindersStackHolder.Contract())
                {
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
        return StateEjectStack == STATE_EJECT_STACK.RESET;
    }

    public boolean ResetEjectStack()
    {
        boolean finishedReset = false;

        if (StateEjectStack == STATE_EJECT_STACK.RESET)
        {
            // IMPORTANT: Use single '&' to execute all cleanup routines
            // asynchronously
            if (StackEjector.ResetEjectStack() & CylindersStackHolder.Extend())
            {
                finishedReset = true;
                ContainerInStack = false;
                TotesInStack = 0;
                StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
            }
        }
        return finishedReset;
    }
}