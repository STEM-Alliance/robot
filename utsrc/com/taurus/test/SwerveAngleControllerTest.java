package com.taurus.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taurus.SwerveAngleController;

public class SwerveAngleControllerTest
{

    @Test
    public void testCalcErrorAndReverseNeeded()
    {
        SwerveAngleController controller = new SwerveAngleController();
        assertEquals(0, controller.CalcErrorAndReverseNeeded(0, 0), 0.01);
        assertFalse(controller.isReverseDriveMotor());
        
        assertEquals(0, controller.CalcErrorAndReverseNeeded(180, 0), 0.01);
        assertTrue(controller.isReverseDriveMotor());
        
        assertEquals(0, controller.CalcErrorAndReverseNeeded(-180, 180), 0.01);
        assertFalse(controller.isReverseDriveMotor());
        
        assertEquals(-50, controller.CalcErrorAndReverseNeeded(-30, 20), 0.01);
        assertFalse(controller.isReverseDriveMotor());
        
        assertEquals(50, controller.CalcErrorAndReverseNeeded(30, -20), 0.01);
        assertFalse(controller.isReverseDriveMotor());
        
        assertEquals(-10, controller.CalcErrorAndReverseNeeded(-95, -85), 0.01);
        assertFalse(controller.isReverseDriveMotor());
        
        assertEquals(-85, controller.CalcErrorAndReverseNeeded(0, -95), 0.01);
        assertTrue(controller.isReverseDriveMotor());
        
        assertEquals(85, controller.CalcErrorAndReverseNeeded(0, 95), 0.01);
        assertTrue(controller.isReverseDriveMotor());
        
        assertEquals(-5, controller.CalcErrorAndReverseNeeded(0, -175), 0.01);
        assertTrue(controller.isReverseDriveMotor());
        
        assertEquals(-20, controller.CalcErrorAndReverseNeeded(-10, -170), 0.01);
        assertTrue(controller.isReverseDriveMotor());
    }

}
