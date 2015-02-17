package com.taurus.robotspecific2015;

public abstract class Constants {
    // Pneumatics
    private static final int MODULE_ID_PCU = 9;
    private static final int MODULE_ID_PCU_2 = 10;

    public static final int PCU_RAIL = MODULE_ID_PCU;
    public static final int PCU_CONTAINER_CAR = MODULE_ID_PCU;
    public static final int PCU_CONTAINER_FIXED = MODULE_ID_PCU;
    public static final int PCU_STOP = MODULE_ID_PCU;
    public static final int PCU_STACK_HOLDER = MODULE_ID_PCU_2;
    public static final int PCU_PUSHER = MODULE_ID_PCU_2;

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
    public static final double TIME_EXTEND_STOP = .3;
    public static final double TIME_EXTEND_PUSHER = 1.5;

    // TODO - what is the amount of time required to contract?
    public static final double TIME_CONTRACT_RAILS = .3;
    public static final double TIME_CONTRACT_CONTAINER_CAR = .3;
    public static final double TIME_CONTRACT_CONTAINER_FIXED = .3;
    public static final double TIME_CONTRACT_STACK_HOLDER = .3;
    public static final double TIME_CONTRACT_STOP = .3;
    public static final double TIME_CONTRACT_PUSHER = .5;

    public static final int MOTOR_DIRECTION_FORWARD = 1;
    public static final int MOTOR_DIRECTION_BACKWARD = -1;

    // Sensors
    public static final int CHANNEL_DIGITAL_CAR_TOP = 5;
    public static final int CHANNEL_DIGITAL_CAR_ZERO = 6;
    public static final int CHANNEL_DIGITAL_TOTE_INTAKE = 7;

    // Lift setup
    public static final int[] LIFT_MOTOR_PINS = { 8 };
    public static final double[] LIFT_MOTOR_SCALING = { -1 };
    public static final int  LIFT_POT_PIN = 4;
    public static final double  LIFT_POT_DISTANCE = 3.14;
    public static final double  LIFT_THRESHOLD = .25;
    public static final double[] LIFT_POSTITIONS = { 0, 1, 2.5, 10.25, 18, 20, 20.75 };
    
    public static final double LIFT_CAR_SPEED_UP = 0.8;
    public static final double LIFT_CAR_SPEED_DOWN = 0.7;
    public static final double LIFT_CAR_SPEED_DOWN_INITIAL = 0.3;

    public static final double LIFT_CAR_TIME_DOWN_INITIAL = 0.5;
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
        EJECT_STACK,
        DROP_STACK
    }

    // Lift
    public static enum STATE_ADD_CHUTE_TOTE_TO_STACK {
        INIT, INTAKE_TOTE, LIFT_TOTE, HANDLE_CONTAINER, BOTTOM, RESET
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


    // Ejector
    public static enum STATE_EJECT {
        PUSHER_EXTEND, MOVE_OUT, RESET
    }

    // Test mode
    public static final int TEST_MODE_PNEUMATIC = 0;
    public static final int TEST_MODE_MOTORS = 1;
    public static final int TEST_MODE_ACTUATOR = 2;

    // Autonomous mode
    public static enum AUTO_MODE {
        DO_NOTHING,
        GO_TO_ZONE,
        GRAB_1_TOTE,
        GRAB_2_TOTES,
        GRAB_3_TOTES,
        GRAB_CONTAINER,
        GRAB_CONTAINER_AND_1_TOTE,
        GRAB_CONTAINER_AND_2_TOTES,
        GRAB_CONTAINER_AND_3_TOTES
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
