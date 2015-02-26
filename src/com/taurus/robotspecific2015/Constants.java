package com.taurus.robotspecific2015;

public abstract class Constants {
    // Pneumatics
    private static final int MODULE_ID_PCM = 9;
    private static final int MODULE_ID_PCM_2 = 10;
    public static final int COMPRESSOR_PCM = MODULE_ID_PCM;

    public static final int PCM_RAIL = MODULE_ID_PCM;
    public static final int PCM_CONTAINER_CAR = MODULE_ID_PCM;
    public static final int PCM_CONTAINER_FIXED = MODULE_ID_PCM;
    public static final int PCM_STOP = MODULE_ID_PCM;
    public static final int PCM_STACK_HOLDER = MODULE_ID_PCM_2;
    public static final int PCM_PUSHER = MODULE_ID_PCM_2;

    public static final int[] CHANNEL_RAIL = { 3, 2 };
    public static final int[] CHANNEL_CONTAINER_CAR = { 5, 4 };
    public static final int[] CHANNEL_CONTAINER_FIXED = { 7, 6 };
    public static final int[] CHANNEL_STOP = { 1, 0 };
    public static final int[] CHANNEL_PUSHER = { 3, 2 };
    public static final int[] CHANNEL_STACK_HOLDER = { 1, 0 };

    // TODO - what is the amount of time required to extend?
    public static final double TIME_EXTEND_RAILS = .3;
    public static final double TIME_EXTEND_CONTAINER_CAR = .3;
    public static final double TIME_EXTEND_CONTAINER_FIXED = .3;
    public static final double TIME_EXTEND_STACK_HOLDER = .3;
    public static final double TIME_EXTEND_STOP = .1;
    public static final double TIME_EXTEND_PUSHER = 1.5;

    // TODO - what is the amount of time required to contract?
    public static final double TIME_CONTRACT_RAILS = .3;
    public static final double TIME_CONTRACT_CONTAINER_CAR = .3;
    public static final double TIME_CONTRACT_CONTAINER_FIXED = .3;
    public static final double TIME_CONTRACT_STACK_HOLDER = .3;
    public static final double TIME_CONTRACT_STOP = .1;
    public static final double TIME_CONTRACT_PUSHER = .5;

    public static final int MOTOR_DIRECTION_FORWARD = 1;
    public static final int MOTOR_DIRECTION_BACKWARD = -1;

    // Sensors
    public static final int CHANNEL_DIGITAL_CAR_TOP_RIGHT = 4;
    public static final int CHANNEL_DIGITAL_CAR_TOP_LEFT = 5;
    public static final int CHANNEL_DIGITAL_CAR_ZERO_LEFT = 6;
    public static final int CHANNEL_DIGITAL_CAR_ZERO_RIGHT = 7;

    // Lift setup
    public static final int[] LIFT_MOTOR_PINS = { 8 };
    public static final double[] LIFT_MOTOR_SCALING = { -1 };
    public static final int LIFT_POT_PIN = 4;
    public static final double LIFT_POT_DISTANCE = 3.14;
    public static final double LIFT_THRESHOLD = .25;
    public static final double[] LIFT_POSTITIONS = { 0, 1, 2.5, 10.25, 18, 20,
            20.75 };
    public static final double LIFT_CHUTE_READY_HEIGHT = 5;

    public static final double LIFT_CAR_SPEED_UP = 0.9;
    public static final double LIFT_CAR_SPEED_DOWN = 0.9;
    public static final double LIFT_CAR_SPEED_DOWN_INITIAL = 0.3;

    public static final double LIFT_CAR_TIME_DOWN_INITIAL = 0.2;
    public static final double LIFT_CAR_TIME_DOWN_INCREASING = 0.5;

    public static final int DISTANCE_SENSOR_LEFT_PIN = 5;
    public static final int DISTANCE_SENSOR_RIGHT_PIN = 6;

    // Car
    public static enum LIFT_POSITIONS_E {
        ZERO,
        EJECT,
        CHUTE,
        DESTACK,
        NOODLE_LOAD,
        CONTAINER_STACK,
        STACK,
        MOVING;

        public static LIFT_POSITIONS_E fromInt(int x)
        {
            switch (x)
            {
                case 0:
                    return ZERO;
                case 1:
                    return EJECT;
                case 2:
                    return CHUTE;
                case 3:
                    return DESTACK;
                case 4:
                    return NOODLE_LOAD;
                case 5:
                    return CONTAINER_STACK;
                case 6:
                    return STACK;
                case 7:
                    return MOVING;
            }
            return null;
        }
    }

    public static enum CYLINDER_STATE {
        NONE, EXTENDED, EXTENDING, CONTRACTED, CONTRACTING
    }

    public static enum CYLINDER_ACTION {
        NONE, EXTEND, CONTRACT
    }

    public static enum STATE_LIFT_ACTION {
        NO_ACTION,
        ADD_CHUTE_TOTE,
        ADD_FLOOR_TOTE,
        ADD_CONTAINER,
        CARRY_STACK,
        EJECT_STACK,
        DROP_STACK,
        ZERO_LIFT
    }

    // Lift
    public static enum STATE_ADD_CHUTE_TOTE_TO_STACK {
        INIT, LIFT_TOTE, RESET
    }

    public static enum STATE_ADD_FLOOR_TOTE_TO_STACK {
        INIT, GRAB_TOTE, LIFT_TOTE
    }

    public static enum STATE_ADD_CONTAINER_TO_STACK {
        INIT, CONTAINER_CAR_EXTEND, LIFT_CAR, CONTAINER_FIXED_EXTEND, RESET
    }

    public static enum STATE_CARRY {
        INIT, STACK_HOLDER_RELEASE, LOWER_CAR
    }
        
    public static enum STATE_EJECT_STACK {
        INIT, EJECT, RESET
    }
    
    public static enum STATE_DROP_STACK {
        INIT, LOWER_STACK, RELEASE, BACK_UP
    }

    public static enum RAIL_CONTENTS {
        EMPTY, TOTE, STACK
    }

    // Ejector
    public static enum STATE_EJECT {
        PUSHER_EXTEND, MOVE_OUT, RESET
    }

    // Test mode
    public static final int TEST_MODE_PNEUMATIC = 0;
    public static final int TEST_MODE_MOTORS = 1;
    public static final int TEST_MODE_ACTUATOR = 2;
    
    public static final int LED_LATCH = 0;

    // Autonomous mode
    public static enum AUTO_MODE {
        DO_NOTHING,
        GO_TO_ZONE,
        GRAB_CONTAINER,
        GRAB_CONTAINER_AND_LINE_UP,
        GRAB_CONTAINER_AND_1_TOTE,
        GRAB_CONTAINER_AND_2_TOTES,
    }
    
    public static enum AUTO_STATE {
        GRAB_CONTAINER,
        
        DRIVE_RIGHT_TO_GRAB_TOTE,
        GRAB_RIGHT_SIDE_TOTE,
        
        DRIVE_LEFT_TO_GRAB_TOTE,
        GRAB_LEFT_SIDE_TOTE,
        
        DRIVE_TO_AUTO_ZONE,
        
        LOWER_TOTES,
        DROP_TOTES,
        BACK_UP,
        
        STOP,
        LINE_UP,
    }

    public static enum AUTO_STATE_MACHINE_L {
        START, DRIVE_FORWARD, DRIVE_STOP, DRIVE_RIGHT, END,
    }

    public static enum AUTO_STATE_MACHINE_MOVE_TO_SCORING_ZONE {
        START, GO, END

    }

    public static enum AUTO_STATE_MACHINE_FIND_AND_GRAB_TOTE {
        START,
        DRIVE_TOWARDS_TOTE,
        MOVE_OUT,
        MOVE_OVER,
        MOVE_IN,
        DRIVE_TOWARDS_TOTE2,
        MOVE_TO_SCORING_AREA
    }

    public static enum AUTO_STATE_MACHINE_FIND_AND_GRAB_CONTAINER {
        START, DRIVE_TOWARDS_CONTAINER, GRABBED
    }

}
