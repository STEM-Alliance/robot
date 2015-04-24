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
    
    // first set is competition, second is backup robot
    public static final int[][] CHANNEL_RAIL                = { { 3, 2 }, { 3, 2 } };
    public static final int[][] CHANNEL_CONTAINER_CAR       = { { 5, 4 }, { 5, 4 } };
    public static final int[][] CHANNEL_CONTAINER_FIXED     = { { 7, 6 }, { 7, 6 } };
    public static final int[][] CHANNEL_STOP                = { { 1, 0 }, { 1, 0 } };
    public static final int[][] CHANNEL_STACK_HOLDER        = { { 1, 0 }, { 1, 0} };

    // what is the amount of time required to extend?
    public static final double TIME_EXTEND_RAILS = .3;
    public static final double TIME_EXTEND_CONTAINER_CAR = .3;
    public static final double TIME_EXTEND_CONTAINER_FIXED = .3;
    public static final double TIME_EXTEND_STACK_HOLDER = .3;
    public static final double TIME_EXTEND_STOP = .1;
    public static final double TIME_EXTEND_PUSHER = 1.5;

    // what is the amount of time required to contract?
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
    public static final int CHANNEL_DIGITAL_CAR_CHUTE = 9;
    
    public static final int CHANNEL_TOTE_SENSOR = 12;
    
    public static final int CHANNEL_ANALOG_TOTE_SENSOR = 7;
    public static final int TOTE_SENSOR_TRIGGER_VALUE = 500;
    public static enum TOTE_SENSOR_STATES {
        NO_TOTE, TOTE_DETECTED, TOTE_IN
    };
    
    // Ultrasonic
    public static final int[] ULTRASONIC_SENSOR_BACK_LEFT = {10, 5};
    public static final int[] ULTRASONIC_SENSOR_BACK_RIGHT = {11, 6};
//    public static final int[] ULTRASONIC_SENSOR_ANGLE_LEFT = {1, 2};
//    public static final int[] ULTRASONIC_SENSOR_ANGLE_RIGHT = {3, 8};

    // Lift setup
    public static final int[] LIFT_MOTOR_PINS = { 8 };
    public static final double[] LIFT_MOTOR_SCALING = { -1 };
    public static final int LIFT_POT_PIN = 4;
    public static final double LIFT_POT_DISTANCE = 3.14;
    public static final double LIFT_THRESHOLD = .4;
    public static final double[] LIFT_POSTITIONS = { 0, .6, 1, 10.25, 18, 20, 20.75 };
    public static final double LIFT_CHUTE_READY_HEIGHT = 15;

    public static final double LIFT_CAR_SPEED_UP = 1.0;
    public static final double LIFT_CAR_SPEED_DOWN = 1.0;
    public static final double LIFT_CAR_SPEED_DOWN_INITIAL = 0.75;
    public static final double LIFT_CAR_SPEED_DOWN_FINAL = 1.0;
    public static final double LIFT_CAR_SPEED_SHAKE_UP = 1;
    public static final double LIFT_CAR_SPEED_SHAKE_DOWN = -1;
    public static final double LIFT_CAR_TIME_SHAKE_UP = .3;
    public static final double LIFT_CAR_TIME_SHAKE_DOWN = .225;

    public static final double LIFT_CAR_TIME_DOWN_INITIAL = 0.2;
    public static final double LIFT_CAR_TIME_DOWN_INCREASING = 0.7;
    public static final double LIFT_CAR_TIME_DOWN_FINAL = 1;


    // Car
    public static enum LIFT_POSITIONS_E {
        ZERO,
        CHUTE,
        EJECT,
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
                    return CHUTE;
                case 2:
                    return EJECT;
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

    public static double STACK_SPEED = .75;

    // Lift
    public static enum STATE_ADD_CHUTE_TOTE_TO_STACK {
        INIT, LIFT_TOTE, RESET
    }

    public static enum STATE_ADD_FLOOR_TOTE_TO_STACK {
        INIT, GRAB_TOTE, LIFT_TOTE, RESET
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

    public static final int LED_LATCH = 10;

    // Autonomous mode
    public static enum AUTO_MODE {
        DO_NOTHING,
        MOVE_CONTAINER_OUT,
        GO_TO_ZONE,
        GRAB_CONTAINER_GO_TO_ZONE,
        GRAB_CONTAINER_NO_MOVE,
        GRAB_CONTAINER_RIGHT_CHUTE,
        GRAB_CONTAINER_LEFT_CHUTE,
    }

    public static enum AUTO_STATE {
        GRAB_CONTAINER, GRAB_CONTAINER_WAIT,
        
        DRIVE_TO_AUTO_ZONE,
        
        MOVE_CONTAINER_OUT,
        
        LINE_UP, LINE_UP_ULTRA,
        
        STOP, 
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
