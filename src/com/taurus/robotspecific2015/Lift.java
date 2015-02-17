package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.command.Subsystem;

// Manages manipulators and supporting systems
public class Lift extends Subsystem {

    // TODO add container state variable
    private boolean ContainerInStack = false;
    private RAIL_CONTENTS RailContents = RAIL_CONTENTS.EMPTY;
    private int TotesInStack = 0;

    private STATE_ADD_CHUTE_TOTE_TO_STACK StateAddChuteToteToStack =
            STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
    private STATE_ADD_FLOOR_TOTE_TO_STACK StateAddFloorToteToStack =
            STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
    private STATE_ADD_CONTAINER_TO_STACK StateAddContainerToStack =
            STATE_ADD_CONTAINER_TO_STACK.INIT;
    private STATE_CARRY StateCarry = STATE_CARRY.INIT;
    private STATE_DROP_STACK StateDropStack = STATE_DROP_STACK.INIT;
    private STATE_EJECT_STACK StateEjectStack = STATE_EJECT_STACK.INIT;

    private Car LiftCar;
    private Ejector StackEjector;
    private PneumaticSubsystem CylindersRails;
    private PneumaticSubsystem CylindersContainerCar;
    private PneumaticSubsystem CylindersContainerFixed;
    private PneumaticSubsystem CylindersStackHolder;
    private SensorDigital ToteIntakeSensor;
    private boolean AutonomousToteTriggered;

    /**
     * Initialize lift and all objects owned by the lift
     */
    public Lift()
    {
        LiftCar = new Car();
        StackEjector = new Ejector();
        CylindersRails =
                new PneumaticSubsystem(Constants.CHANNEL_RAIL,
                        Constants.PCU_RAIL, Constants.TIME_EXTEND_RAILS,
                        Constants.TIME_CONTRACT_RAILS,
                        Constants.CYLINDER_ACTION.EXTEND);
        CylindersContainerCar =
                new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_CAR,
                        Constants.PCU_CONTAINER_CAR,
                        Constants.TIME_EXTEND_CONTAINER_CAR,
                        Constants.TIME_CONTRACT_CONTAINER_CAR,
                        Constants.CYLINDER_ACTION.CONTRACT);
        CylindersContainerFixed =
                new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_FIXED,
                        Constants.PCU_CONTAINER_FIXED,
                        Constants.TIME_EXTEND_CONTAINER_FIXED,
                        Constants.TIME_CONTRACT_CONTAINER_FIXED,
                        Constants.CYLINDER_ACTION.CONTRACT);
        CylindersStackHolder =
                new PneumaticSubsystem(Constants.CHANNEL_STACK_HOLDER,
                        Constants.PCU_STACK_HOLDER,
                        Constants.TIME_EXTEND_STACK_HOLDER,
                        Constants.TIME_CONTRACT_STACK_HOLDER,
                        Constants.CYLINDER_ACTION.CONTRACT);
        ToteIntakeSensor =
                new SensorDigital(Constants.CHANNEL_DIGITAL_TOTE_INTAKE);

        init();
    }

    private void initStates()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateCarry = STATE_CARRY.INIT;
        this.StateDropStack = STATE_DROP_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.INIT;
    }

    public void init()
    {
        this.initStates();
        this.TotesInStack = 0;
        this.ContainerInStack = false;
        this.RailContents = RAIL_CONTENTS.EMPTY;
        this.AutonomousToteTriggered = false;
    }

    public boolean IsToteInPlace()
    {
        return AutonomousToteTriggered
               || ToteIntakeSensor.IsOn()
               || Application.controller.getFakeToteAdd();
    }

    public void SetAutonomousToteTriggered(boolean b)
    {
        this.AutonomousToteTriggered = b;
    }

    /**
     * Routine to add a new tote to existing stack from chute
     * 
     * @return true if finished
     */
    public boolean AddChuteToteToStack(int MaxTotesInStack)
    {
        switch (StateAddChuteToteToStack)
        {
            case INIT:
                this.initStates();

                switch (RailContents)
                {
                    case EMPTY:
                        if (LiftCar.GoToChute()
                            & CylindersRails.Extend()
                            & CylindersStackHolder.Contract()
                            & CylindersContainerCar.Contract()
                            & StackEjector.StopOut()
                            & IsToteInPlace())
                        {
                            RailContents = RAIL_CONTENTS.TOTE;

                            if (TotesInStack < MaxTotesInStack)
                            {
                                StateAddChuteToteToStack =
                                        STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                            }
                        }
                        break;

                    case TOTE:
                    case STACK:
                        if (TotesInStack < MaxTotesInStack)
                        {
                            StateAddChuteToteToStack =
                                    STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                        }
                        break;
                }
                break;

            case LIFT_TOTE:
                if (TotesInStack != 0)
                {
                    // If this is not the first tote move the container holder
                    // out of the way.
                    CylindersContainerFixed.Contract();
                }

                if (LiftCar.GoToStack()
                    & StackEjector.StopIn()
                    & CylindersRails.Extend()
                    & CylindersStackHolder.Contract())
                {
                    // The stack holder has latched.
                    if (RailContents == RAIL_CONTENTS.TOTE)
                    {
                        // The tote is now in the stack.
                        TotesInStack = TotesInStack + 1;
                    }

                    RailContents = RAIL_CONTENTS.EMPTY;
                    StateAddChuteToteToStack =
                            STATE_ADD_CHUTE_TOTE_TO_STACK.RESET;

                }
                break;

            case RESET:
                if (LiftCar.GoToBottom())
                {
                    StateAddChuteToteToStack =
                            STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
                }
                break;
        }

        // If the sensor triggered, and we have 5 totes, we have six totes and
        // this method is "done"
        return TotesInStack >= MaxTotesInStack && IsToteInPlace();
    }

    /**
     * Routine to add a new tote to existing stack from floor
     * 
     * @return true if finished
     */
    public boolean AddFloorToteToStack(int MaxTotesInStack)
    {
        switch (StateAddFloorToteToStack)
        {
            case INIT:
                this.initStates();

                switch (RailContents)
                {
                    case EMPTY:
                        if (LiftCar.GoToBottom()
                            & CylindersRails.Contract()
                            & CylindersStackHolder.Contract()
                            & CylindersContainerCar.Contract()
                            & StackEjector.StopIn()
                            & IsToteInPlace())
                        {
                            StateAddFloorToteToStack =
                                    STATE_ADD_FLOOR_TOTE_TO_STACK.GRAB_TOTE;
                        }
                        break;

                    case TOTE:
                    case STACK:
                        if (TotesInStack < MaxTotesInStack)
                        {
                            StateAddFloorToteToStack =
                                    STATE_ADD_FLOOR_TOTE_TO_STACK.LIFT_TOTE;
                        }
                        else
                        {
                            // Keep what we have, don't open the rails.
                            LiftCar.GoToEject();
                            CylindersRails.Extend();
                            CylindersStackHolder.Contract();
                            CylindersContainerCar.Contract();
                            StackEjector.StopOut();
                        }
                        break;
                }
                break;

            case GRAB_TOTE:
                if (CylindersRails.Extend())
                {
                    if (StackEjector.StopOut())
                    {
                        RailContents = RAIL_CONTENTS.TOTE;

                        if (TotesInStack < MaxTotesInStack)
                        {
                            StateAddFloorToteToStack =
                                    STATE_ADD_FLOOR_TOTE_TO_STACK.LIFT_TOTE;
                        }
                        else
                        {
                            StateAddFloorToteToStack =
                                    STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                        }
                    }
                }
                break;

            case LIFT_TOTE:
                if (LiftCar.GoToStack()
                    & StackEjector.StopIn()
                    & CylindersRails.Extend()
                    & CylindersStackHolder.Contract())
                {
                    // Locked into place.
                    if (RailContents == RAIL_CONTENTS.TOTE)
                    {
                        // The tote is now in the stack.
                        TotesInStack = TotesInStack + 1;
                    }

                    RailContents = RAIL_CONTENTS.EMPTY;
                    StateAddFloorToteToStack =
                            STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                }
                break;
        }

        return TotesInStack >= MaxTotesInStack
               && LiftCar.GetPosition() == LIFT_POSITIONS_E.EJECT;
    }

    /**
     * Routine to lift a container to start a new stack
     * 
     * @return true if finished
     */
    public boolean AddContainerToStack()
    {
        switch (StateAddContainerToStack)
        {
            case INIT:
                this.initStates();

                if (RailContents == RAIL_CONTENTS.EMPTY
                    && !ContainerInStack
                    && TotesInStack == 0)
                {
                    // Sanity check this should even be called.

                    if (LiftCar.GoToBottom()
                        & CylindersRails.Contract()
                        & CylindersStackHolder.Extend()
                        & CylindersContainerFixed.Contract()
                        & CylindersContainerCar.Contract()
                        & IsToteInPlace())
                    {
                        StateAddContainerToStack =
                                STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_EXTEND;
                    }
                }
                break;

            case CONTAINER_CAR_EXTEND:
                if (LiftCar.GoToBottom()
                    & CylindersRails.Extend()
                    & CylindersContainerCar.Extend())
                {
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR;
                }
                break;

            case LIFT_CAR:
                if (LiftCar.GoToStack())
                {
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.CONTAINER_FIXED_EXTEND;
                }
                break;

            case CONTAINER_FIXED_EXTEND:
                if (LiftCar.GoToStack() & GetCylindersContainerFixed().Extend())
                {
                    ContainerInStack = true;
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.RESET;
                }
                break;

            case RESET:
                if (LiftCar.GoToBottom()
                    & CylindersContainerCar.Contract()
                    & CylindersRails.Contract())
                {
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.INIT;
                }
                break;
        }

        return ContainerInStack
               && LiftCar.GetPosition() == LIFT_POSITIONS_E.ZERO;
    }

    /**
     * Lower the stack from the upper holder to carry height and clamp into
     * place.
     * 
     * @return true if finished
     */
    public boolean LowerStackToCarryHeight()
    {
        switch (StateCarry)
        {
            case INIT:
                this.initStates();

                switch (RailContents)
                {
                    case TOTE:
                        if (LiftCar.GoToDestack()
                            & CylindersRails.Extend()
                            & CylindersStackHolder.Contract()
                            & CylindersContainerCar.Contract()
                            & StackEjector.StopIn())
                        {
                            StateCarry = STATE_CARRY.STACK_HOLDER_RELEASE;
                        }
                        break;

                    case EMPTY:
                        if (LiftCar.GoToStack()
                            & CylindersRails.Extend()
                            & CylindersStackHolder.Contract()
                            & CylindersContainerCar.Contract()
                            & StackEjector.StopIn())
                        {
                            StateCarry = STATE_CARRY.STACK_HOLDER_RELEASE;
                        }
                        break;

                    case STACK:
                        // Hold the stack in place.
                        LiftCar.GoToEject();
                        CylindersRails.Extend();
                        CylindersStackHolder.Extend();
                        CylindersContainerCar.Extend();
                        CylindersContainerFixed.Extend();
                        StackEjector.StopOut();
                        break;
                }
                break;

            case STACK_HOLDER_RELEASE:
                // Note: may be stack OR destack height depending on whether
                // there is a tote on the rails to push up with.
                LiftCar.UpdateLastPosition();

                if (CylindersStackHolder.Extend()
                    & CylindersContainerFixed.Contract()
                    & CylindersRails.Extend()
                    & CylindersContainerCar.Extend()
                    & StackEjector.StopIn())
                {
                    if (RailContents == RAIL_CONTENTS.TOTE)
                    {
                        // The tote is now in the stack.
                        TotesInStack = TotesInStack + 1;
                    }

                    RailContents = RAIL_CONTENTS.STACK;
                    StateCarry = STATE_CARRY.LOWER_CAR;
                }
                break;

            case LOWER_CAR:
                if (LiftCar.GoToEject()
                    & CylindersStackHolder.Extend()
                    & CylindersContainerFixed.Contract()
                    & CylindersRails.Extend()
                    & CylindersContainerCar.Extend()
                    & StackEjector.StopIn())
                {
                    StateCarry = STATE_CARRY.INIT;
                }
                break;

        }

        return LiftCar.GetPosition() == LIFT_POSITIONS_E.EJECT
               && RailContents == RAIL_CONTENTS.STACK;
    }

    /**
     * Place the stack on the ground, then push it onto the scoring platform
     * 
     * @return true if finished
     */
    public boolean EjectStack()
    {
        switch (StateEjectStack)
        {
            case INIT:
                if (LowerStackToCarryHeight())
                {
                    StateEjectStack = STATE_EJECT_STACK.EJECT;
                }
                break;

            case EJECT:
                if (LiftCar.GoToEject()
                    & StackEjector.StopIn()
                    & StackEjector.EjectStack())
                {
                    RailContents = RAIL_CONTENTS.EMPTY;
                    TotesInStack = 0;
                    ContainerInStack = false;

                    if (Application.controller.getEjectStack())
                    {
                        // Wait for the driver to press eject again to retract
                        // the piston (yes, I know, ewww...).
                        StateEjectStack = STATE_EJECT_STACK.RESET;
                    }
                }
                break;

            case RESET:
                StackEjector.ResetEjectStack();

                // Rely on other state machines to set the state back to init.
                break;
        }

        return StateEjectStack == STATE_EJECT_STACK.RESET;
    }

    /**
     * Place the stack on the ground and release the rails
     * 
     * @return true if finished
     */
    public boolean DropStack()
    {
        return DropStack(false);
    }

    /**
     * Place the stack on the ground and release the rails
     * 
     * @return true if finished
     */
    public boolean DropStack(boolean keepContainer)
    {
        switch (StateDropStack)
        {
            case INIT:
                if (LowerStackToCarryHeight())
                {
                    StateDropStack = STATE_DROP_STACK.LOWER_STACK;
                }
                break;

            case LOWER_STACK:
                if (LiftCar.GoToBottom()
                    & (keepContainer || CylindersContainerFixed.Contract())
                    & CylindersStackHolder.Contract())
                {
                    StateDropStack = STATE_DROP_STACK.RELEASE;
                }
                break;

            case RELEASE:
                if (CylindersContainerCar.Contract()
                    & CylindersRails.Contract()
                    & StackEjector.StopIn())
                {
                    RailContents = RAIL_CONTENTS.EMPTY;
                    TotesInStack = 0;
                    ContainerInStack = false;

                    StateDropStack = STATE_DROP_STACK.BACK_UP;
                }
                break;

            case BACK_UP:
                // TODO
                // Note: relies on other modes to reset the state to INIT.
                break;
        }

        return StateDropStack == STATE_DROP_STACK.BACK_UP;
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
    public PneumaticSubsystem GetCylindersStackHolder()
    {
        return CylindersStackHolder;
    }

    @Override
    protected void initDefaultCommand()
    {
    }

    public int GetTotesInStack()
    {
        return TotesInStack;
    }

    public RAIL_CONTENTS GetRailContents()
    {
        return RailContents;
    }

    public boolean GetContainerInStack()
    {
        return ContainerInStack;
    }

    public STATE_ADD_CHUTE_TOTE_TO_STACK GetStateAddChuteToteToStack()
    {
        return StateAddChuteToteToStack;
    }

    public STATE_ADD_FLOOR_TOTE_TO_STACK GetStateAddFloorToteToStack()
    {
        return StateAddFloorToteToStack;
    }

    public STATE_ADD_CONTAINER_TO_STACK GetStateAddContainerToStack()
    {
        return StateAddContainerToStack;
    }

    public STATE_CARRY GetStateCarryStack()
    {
        return StateCarry;
    }

    public STATE_DROP_STACK GetStateDropStack()
    {
        return StateDropStack;
    }

    public STATE_EJECT_STACK GetStateEjectStack()
    {
        return StateEjectStack;
    }
}
