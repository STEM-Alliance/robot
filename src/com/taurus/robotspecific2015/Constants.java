package com.taurus.robotspecific2015;

public abstract class Constants {
    // Pneumatics
    public static final int MODULE_ID_PCU = 9;
    public static final int MODULE_ID_PCU_2 = 10;

    public static final int[] CHANNEL_RAIL = { 0, 1 };
    public static final int[] CHANNEL_CONTAINER_CAR = { 2, 3 };
    public static final int[] CHANNEL_CONTAINER_FIXED = { 4, 5 };
    public static final int[] CHANNEL_STACK_HOLDER = { 6, 7 };
    public static final int[] CHANNEL_STOP = { 0, 1 };
    public static final int[] CHANNEL_PUSHER = { 2, 3 };

    // TODO - what is the amount of time required to extend?
    public static final double TIME_EXTEND_RAILS = 1;
    public static final double TIME_EXTEND_CONTAINER_CAR = 1;
    public static final double TIME_EXTEND_CONTAINER_FIXED = 1;
    public static final double TIME_EXTEND_STACK_HOLDER = 1;
    public static final double TIME_EXTEND_STOP = 1;
    public static final double TIME_EXTEND_PUSHER = 1;

    // TODO - what is the amount of time required to contract?
    public static final double TIME_CONTRACT_RAILS = 1;
    public static final double TIME_CONTRACT_CONTAINER_CAR = 1;
    public static final double TIME_CONTRACT_CONTAINER_FIXED = 1;
    public static final double TIME_CONTRACT_STACK_HOLDER = 1;
    public static final double TIME_CONTRACT_STOP = 1;
    public static final double TIME_CONTRACT_PUSHER = 1;

    // Motors
    public static final int[] PINS_EJECTOR = { 9 };

    public static final double[] SCALING_EJECTOR = { 1 };

    public static final int MOTOR_DIRECTION_FORWARD = 1;
    public static final int MOTOR_DIRECTION_BACKWARD = -1;

    // Sensors
    public static final int CHANNEL_DIGITAL_TOTE_INTAKE = 8;
    public static final int CHANNEL_DIGITAL_CAR_ZERO = 9;
    public static final int CHANNEL_DIGITAL_EJECTOR_OUT = 10;
    public static final int CHANNEL_DIGITAL_EJECTOR_IN = 11;

    // Lift setup
    public static final int[] LIFT_MOTOR_PINS = { 8 };
    public static final double[] LIFT_MOTOR_SCALING = { 1 };
    public static final int[] LIFT_ENCODER_PINS = { 0, 1 };
    public static final int LIFT_ENCODER_PULSES = 64;
    // TODO - find distance of lift traveled for one full rotation
    public static final double LIFT_ENCODER_FULL_TURN_DISTANCE = 1;
    public static final double LIFT_ENCODER_INCHES_PER_PULSE = LIFT_ENCODER_FULL_TURN_DISTANCE / LIFT_ENCODER_PULSES;
    public static final double[] LIFT_POSTITIONS = { 0, 6, 12, 18 };

    public static enum CYLINDER_STATE {
        NONE, EXTENDED, EXTENDING, CONTRACTED, CONTRACTING
    }
    
    public static enum STATE_LIFT_ACTION {
        NO_ACTION, ADD_CHUTE_TOTE, ADD_FLOOR_TOTE, ADD_CONTAINER, EJECT_STACK
    }
    
    // Lift
    public static enum STATE_ADD_CHUTE_TOTE_TO_STACK {
        INIT, INTAKE_TOTE, LIFT_TOTE, HANDLE_CONTAINER, RESET
    }

    public static enum STATE_ADD_FLOOR_TOTE_TO_STACK {
        INIT, INTAKE_TOTE, GRAB_TOTE, LIFT_TOTE, HANDLE_CONTAINER, RESET
    }

    public static enum STATE_ADD_CONTAINER_TO_STACK {
        INIT, CONTAINER_CAR_EXTEND, LIFT_CAR, CONTAINER_FIXED_EXTEND, CONTAINER_CAR_CONTRACT, LOWER_CAR, RESET
    }

    public static enum STATE_EJECT_STACK {
        LIFT_CAR, STACK_HOLDER_CONTRACT, LOWER_CAR, EJECT_STACK, RESET,
    }

    // Car
    public static enum POSITION_CAR {
        STACK, DESTACK, CHUTE, BOTTOM, MOVING
    }

    // Ejector
    public static enum STATE_EJECT {
        PUSHER_EXTEND, MOVE_OUT, RESET
    }

    // TODO: If we do not use this, remove this enum and create boolean in
    // ejector class
    public static enum POSITION_EJECTOR {
        OUT, MID, IN
    }

    // Test mode
    public static final int TEST_MODE_PNEUMATIC = 0;
    public static final int TEST_MODE_MOTORS = 1;
    public static final int TEST_MODE_SENSOR = 2;

    // Autonomous mode
    public static enum AUTO_STATE_MACHINE_L {
        START, DRIVE_FORWARD, DRIVE_STOP, DRIVE_RIGHT, END,
    }
    
    public static enum AUTO_STATE_MACHINE_MOVE_TO_SCORING_ZONE {
        START, GO, END

    }
    
    public static enum AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE {
        START, DRIVE_TOWARDS_TOTE, MOVE_OUT, MOVE_OVER, MOVE_IN, DRIVE_TOWARDS_TOTE2, MOVE_TO_SCORING_AREA
    }
    
    public static enum AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER {
        START, DRIVE_TOWARDS_CONTAINER, GRABBED
    }
    
}
