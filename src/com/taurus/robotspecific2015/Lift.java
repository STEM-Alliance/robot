package com.taurus.robotspecific2015;

import com.taurus.controller.Controller;
import com.taurus.robotspecific2015.Constants.*;
import com.taurus.swerve.SwerveChassis;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

// Manages manipulators and supporting systems
public class Lift extends Subsystem {

    private boolean ContainerInStack = false;
    private RAIL_CONTENTS RailContents = RAIL_CONTENTS.EMPTY;
    private int TotesInStack = 0;
    private double ledFlashTime = 0;

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

    private boolean AutonomousToteTriggered;

    private double StopperWaitTime = 0;
    
    private final Controller controller;

    public final Relay LED = new Relay(2);

    /**
     * Initialize lift and all objects owned by the lift
     * 
     * @param drive
     *            Chassis object, used to control driving from lift states
     * @param controller
     *            main controller object
     */
    public Lift(SwerveChassis drive, Controller controller)
    {
        this.controller = controller;

        LiftCar = new Car(this.controller);
        StackEjector = new Ejector();
        CylindersRails =
                new PneumaticSubsystem(Constants.CHANNEL_RAIL[Application.ROBOT_VERSION],
                        Constants.PCM_RAIL, Constants.TIME_EXTEND_RAILS,
                        Constants.TIME_CONTRACT_RAILS,
                        Constants.CYLINDER_ACTION.EXTEND);
        CylindersContainerCar =
                new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_CAR[Application.ROBOT_VERSION],
                        Constants.PCM_CONTAINER_CAR,
                        Constants.TIME_EXTEND_CONTAINER_CAR,
                        Constants.TIME_CONTRACT_CONTAINER_CAR,
                        Constants.CYLINDER_ACTION.CONTRACT);
        CylindersContainerFixed =
                new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_FIXED[Application.ROBOT_VERSION],
                        Constants.PCM_CONTAINER_FIXED,
                        Constants.TIME_EXTEND_CONTAINER_FIXED,
                        Constants.TIME_CONTRACT_CONTAINER_FIXED,
                        Constants.CYLINDER_ACTION.CONTRACT);
        CylindersStackHolder =
                new PneumaticSubsystem(Constants.CHANNEL_STACK_HOLDER[Application.ROBOT_VERSION],
                        Constants.PCM_STACK_HOLDER,
                        Constants.TIME_EXTEND_STACK_HOLDER,
                        Constants.TIME_CONTRACT_STACK_HOLDER,
                        Constants.CYLINDER_ACTION.CONTRACT);

        LED.setDirection(Direction.kForward);
        LED.set(Value.kForward);

        init();
    }

    /**
     * Reset state machines
     */
    private void initStates()
    {
        this.StateAddChuteToteToStack = STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
        this.StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.INIT;
        this.StateAddFloorToteToStack = STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
        this.StateCarry = STATE_CARRY.INIT;
        this.StateDropStack = STATE_DROP_STACK.INIT;
        this.StateEjectStack = STATE_EJECT_STACK.INIT;
    }

    /**
     * Reset class variables
     */
    public void init()
    {
        this.initStates();
        this.TotesInStack = 0;
        // this.ContainerInStack = false;
        this.RailContents = RAIL_CONTENTS.EMPTY;
        this.AutonomousToteTriggered = false;
    }

    public boolean IsToteInPlace()
    {
        return AutonomousToteTriggered
        // || ToteIntakeSensor.IsOn()
               || controller.getFakeToteAdd();
    }

    public void SetAutonomousToteTriggered(boolean b)
    {
        this.AutonomousToteTriggered = b;
    }

    @Override
    protected void initDefaultCommand()
    {
    }

    /**
     * Routine to add a new tote to existing stack from chute
     * 
     * @return true if finished
     */
    public boolean AddChuteToteToStack(int MaxTotesInStack)
    {

        // LED.set(Value.kOn);
        switch (StateAddChuteToteToStack)
        {
            case INIT:
                this.initStates();
                LED.set(Value.kForward);

                if (ContainerInStack && TotesInStack < 1)
                {
                    CylindersContainerFixed.Extend();
                }
                else if (ContainerInStack && TotesInStack >= 1)
                {
                    CylindersContainerFixed.Contract();
                }

                switch (RailContents)
                {
                    case EMPTY:
                        LiftCar.GoToChute(false);
                        if (CylindersRails.Extend()
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

                                // Application.leds.AddEffect(effectsIntakeNotReady,
                                // true);
                                StopperWaitTime = Timer.getFPGATimestamp();
                            }
                        }
                        break;

                    case TOTE:
                    case STACK:
                        if (TotesInStack < MaxTotesInStack)
                        {
                            StateAddChuteToteToStack =
                                    STATE_ADD_CHUTE_TOTE_TO_STACK.LIFT_TOTE;
                            LED.set(Value.kOff);

                            // Application.leds.AddEffect(effectsIntakeNotReady,
                            // true);
                            StopperWaitTime = Timer.getFPGATimestamp();
                        }
                        else
                        {
                            // Hold steady.
                            LiftCar.GoToChute(false);
                            CylindersRails.Extend();
                            CylindersStackHolder.Contract();
                            CylindersContainerCar.Contract();
                            StackEjector.StopOut();
                        }
                        break;
                }
                break;

            case LIFT_TOTE:
                if (TotesInStack > 0)
                {
                    // If this is not the first tote move the container holder
                    // out of the way.
                    CylindersContainerFixed.Contract();
                }

                LED.set(Value.kOff);

                if (LiftCar.GoToStack(TotesInStack + 1, ContainerInStack)
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
                    ledFlashTime = Timer.getFPGATimestamp();
                    StopperWaitTime = Timer.getFPGATimestamp();

                }
                // wait 2 seconds before putting in the stopper
                if (Timer.getFPGATimestamp() - StopperWaitTime < .25)
                {
                    StackEjector.StopIn();
                }
                else if (Timer.getFPGATimestamp() - StopperWaitTime < .5)
                {
                    //StackEjector.StopOut();
                }
                else 
                {
                    StackEjector.StopIn();
                }
                break;

            case RESET:
                // Determine if we are within a distance above the bottom,
                // preemptively set LEDs
                if (LiftCar.GetHeight() < Constants.LIFT_CHUTE_READY_HEIGHT)
                {
                    if (TotesInStack < MaxTotesInStack)
                    {
                        if (Timer.getFPGATimestamp() - ledFlashTime < .125)
                        {
                            LED.set(Value.kForward);
                        }
                        else if (Timer.getFPGATimestamp() - ledFlashTime < .25)
                        {
                            LED.set(Value.kOff);
                        }
                        else
                        {
                            ledFlashTime = Timer.getFPGATimestamp();
                        }
                    }
                    else
                    {
                        // Indicate that we are ready to drive off
                        LED.set(Value.kForward);
                    }
                }
                else
                {
                    LED.set(Value.kOff);
                }

                if (LiftCar.GoToBottom(TotesInStack))
                {
                    StateAddChuteToteToStack =
                            STATE_ADD_CHUTE_TOTE_TO_STACK.INIT;
                    CylindersRails.Extend();
                }
                else if (Timer.getFPGATimestamp() - StopperWaitTime > 1)
                {

                    StackEjector.StopOut();
                    if (LiftCar.GetHeight() < 4)
                    {
                        CylindersRails.Extend();
                    }
                    else
                    {
                        CylindersRails.Contract();
                    }
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
     * @param MaxTotesInStack
     *            Used to limit maximum carry capacity to exit states early
     * @return true if finished
     */
    public boolean AddFloorToteToStack(int MaxTotesInStack)
    {
        switch (StateAddFloorToteToStack)
        {
            case INIT:
                this.initStates();

                if (ContainerInStack && TotesInStack < 1)
                {
                    CylindersContainerFixed.Extend();
                }
                else
                {
                    CylindersContainerFixed.Contract();
                }

                switch (RailContents)
                {
                    case EMPTY:
                        if (LiftCar.GoToBottom(6)
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
                            StopperWaitTime = Timer.getFPGATimestamp();
                        }
                        else
                        {
                            // Keep what we have, don't open the rails.
                            LiftCar.GoToEject();
                            CylindersRails.Extend();
                            CylindersStackHolder.Contract();
                            CylindersContainerCar.Extend();
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
                            StopperWaitTime = Timer.getFPGATimestamp();
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
                if (TotesInStack != 0)
                {
                    // If this is not the first tote move the container holder
                    // out of the way.
                    CylindersContainerFixed.Contract();
                }

                if (LiftCar.GoToStack(TotesInStack + 1, ContainerInStack)
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
                            STATE_ADD_FLOOR_TOTE_TO_STACK.RESET;
                    StopperWaitTime = Timer.getFPGATimestamp();
                    ledFlashTime = Timer.getFPGATimestamp();
                }
                // wait 2 seconds before putting in the stopper
                else if (Timer.getFPGATimestamp() - StopperWaitTime > 2)
                {
                    StackEjector.StopIn();
                }
                break;
            case RESET:
                // Determine if we are within a distance above the bottom,
                // preemptively set LEDs
                if (LiftCar.GetHeight() < Constants.LIFT_CHUTE_READY_HEIGHT)
                {
                    if (TotesInStack < MaxTotesInStack)
                    {
                        if (Timer.getFPGATimestamp() - ledFlashTime < .125)
                        {
                            LED.set(Value.kForward);
                        }
                        else if (Timer.getFPGATimestamp() - ledFlashTime < .25)
                        {
                            LED.set(Value.kOff);
                        }
                        else
                        {
                            ledFlashTime = Timer.getFPGATimestamp();
                        }
                    }
                    else
                    {
                        // Indicate that we are ready to drive off
                        LED.set(Value.kForward);
                    }
                }
                else
                {
                    LED.set(Value.kOff);
                }

                if (LiftCar.GoToBottom(TotesInStack))
                {
                    StateAddFloorToteToStack =
                            STATE_ADD_FLOOR_TOTE_TO_STACK.INIT;
                    CylindersRails.Extend();
                }
                else if (Timer.getFPGATimestamp() - StopperWaitTime > .5)
                {
                    StackEjector.StopOut();

                    if (LiftCar.GetHeight() < 4)
                    {
                        CylindersRails.Extend();
                    }
                    else
                    {
                        CylindersRails.Contract();
                    }
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
                // && !ContainerInStack
                    && TotesInStack == 0)
                {
                    // Sanity check this should even be called.

                    if (LiftCar.GoToBottom(6)
                        & CylindersRails.Contract()
                        & CylindersStackHolder.Extend()
                        & CylindersContainerCar.Contract()
                        & StackEjector.StopIn()

                        & IsToteInPlace())
                    {
                        CylindersContainerFixed.Contract();
                        StateAddContainerToStack =
                                STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_EXTEND;
                    }
                }
                break;

            case CONTAINER_CAR_EXTEND:
                if (LiftCar.GoToBottom(6)
                    & CylindersRails.Extend()
                    & CylindersContainerCar.Extend())
                {
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR;
                }
                break;

            case LIFT_CAR:
                if (LiftCar.GoToStack(0, ContainerInStack)
                    & CylindersContainerFixed.Contract())

                {
                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.CONTAINER_FIXED_EXTEND;
                }
                break;

            case CONTAINER_FIXED_EXTEND:
                if (LiftCar.GoToStack(0, ContainerInStack)
                    & CylindersContainerFixed.Extend())
                {
                    ContainerInStack = true;

                    StateAddContainerToStack =
                            STATE_ADD_CONTAINER_TO_STACK.RESET;
                }
                break;

            case RESET:
                if (LiftCar.GoToBottom(6)
                    & CylindersContainerCar.Contract()
                    & CylindersRails.Contract()
                    & CylindersContainerFixed.Extend())
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
                        if (TotesInStack > 0)
                        {
                            CylindersContainerFixed.Contract();
                        }

                        if (LiftCar.GoToDestack(ContainerInStack)
                            & CylindersRails.Extend()
                            & CylindersStackHolder.Contract()
                            & CylindersContainerCar.Contract()
                            & StackEjector.StopIn())
                        {
                            StateCarry = STATE_CARRY.STACK_HOLDER_RELEASE;
                        }
                        break;

                    case EMPTY:
                        if (TotesInStack > 0)
                        {
                            CylindersContainerFixed.Contract();
                        }

                        if (LiftCar.GoToStack(TotesInStack, ContainerInStack)
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
                        LiftCar.GoToChute(true);
                        CylindersRails.Extend();
                        CylindersStackHolder.Extend();
                        CylindersContainerCar.Contract();
                        CylindersContainerFixed.Contract();
                        // StackEjector.StopOut();
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
                    & CylindersContainerCar.Contract()
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
                if (LiftCar.GoToChute(true)
                    & CylindersStackHolder.Extend()
                    & CylindersContainerFixed.Contract()
                    & CylindersRails.Extend()
                    & CylindersContainerCar.Contract()
                    & StackEjector.StopIn())
                {
                    // Application.leds.AddEffect(effectsInTransit, true);
                    StateCarry = STATE_CARRY.INIT;
                }
                break;

        }

        return (LiftCar.GetPosition() == LIFT_POSITIONS_E.EJECT || (controller
                .getFakePostion() && controller.getManualLift()))
               && RailContents == RAIL_CONTENTS.STACK;
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
     * @param keepContainer
     *            true if you want to keep the container in the top holder
     * @return true if finished
     */
    public boolean DropStack(boolean keepContainer)
    {
        boolean Finish = false;
        switch (StateDropStack)
        {
            case INIT:
                // Application.leds.AddEffect(effectsScore, true);
                StateDropStack = STATE_DROP_STACK.LOWER_STACK;
                break;

            case LOWER_STACK:
                if (!keepContainer)
                {
                    CylindersContainerFixed.Contract();
                }

                if (LiftCar.GoToBottom(TotesInStack)
                    & CylindersStackHolder.Extend())
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

                    StateDropStack = STATE_DROP_STACK.INIT;
                    Finish = true;
                }
                break;

            case BACK_UP:
                // SwerveVector vector = new SwerveVector();
                // vector.setMagAngle(1, 180);
                // Finish = true; //drive.autoRun(vector, 2,
                // DropDriveFirstTime);
                // DropDriveFirstTime = false;
                break;
        }

        return Finish;
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

    public void SetContainerInStack(boolean b)
    {
        ContainerInStack = b;
    }

    public void SetToteOnRails(boolean b)
    {
        if (b)
        {
            RailContents = RAIL_CONTENTS.TOTE;
        }
    }

    // Dislog a stuck tote by jittering the car
    public void ShakeCar()
    {
        LiftCar.ShakeCar();
    }
}
