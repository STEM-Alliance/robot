package com.taurus.test;

import junit.framework.TestCase;

import com.taurus.SwerveAngleController;

public class SwerveAngleControllerTest extends TestCase
{

    public void testCalcErrorAndReverseNeeded()
    {
        SwerveAngleController controller = new SwerveAngleController();
        assertEquals(0, controller.calcErrorAndReverseNeeded(0, 0), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(0, controller.calcErrorAndReverseNeeded(180, 0), 0.01);
        assertTrue(controller.isReverseMotor());

        assertEquals(0, controller.calcErrorAndReverseNeeded(-180, 180), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(-50, controller.calcErrorAndReverseNeeded(-30, 20), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(-50, controller.calcErrorAndReverseNeeded(-390, 380), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(50, controller.calcErrorAndReverseNeeded(30, -20), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(-10, controller.calcErrorAndReverseNeeded(-95, -85), 0.01);
        assertFalse(controller.isReverseMotor());

        assertEquals(-85, controller.calcErrorAndReverseNeeded(0, -95), 0.01);
        assertTrue(controller.isReverseMotor());

        assertEquals(85, controller.calcErrorAndReverseNeeded(0, 95), 0.01);
        assertTrue(controller.isReverseMotor());

        assertEquals(-5, controller.calcErrorAndReverseNeeded(0, -175), 0.01);
        assertTrue(controller.isReverseMotor());

        assertEquals(-20, controller.calcErrorAndReverseNeeded(-10, -170), 0.01);
        assertTrue(controller.isReverseMotor());
    }

}
