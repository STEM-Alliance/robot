package com.taurus.test;

import com.taurus.PIController;

import junit.framework.TestCase;

public class PIControllerTest extends TestCase
{
    private static double delta = 0.0000001;
    
    public void testP()
    {
        PIController c = new PIController(-1, 0, 10);
        
        assertEquals(-1, c.update(1, 0), delta);
        assertEquals(0, c.integral, delta);
        
        assertEquals(-10, c.update(100, 0), delta);
        assertEquals(0, c.integral, delta);
        
        assertEquals(9, c.update(-9, 0), delta);
        assertEquals(0, c.integral, delta);
        
        assertEquals(10, c.update(-10.1, 0), delta);
        assertEquals(0, c.integral, delta);
    }
    
    public void testSimpleWindup()
    {
        PIController c = new PIController(2, 1, 3);
        
        // Start time shouldn't matter
        double t = 77;
        assertEquals(2, c.update(1, t), delta);
        assertEquals(0, c.integral, delta);
        
        // Integral increases steadily every update.
        t += .1;
        assertEquals(2.2, c.update(1, t), delta);
        assertEquals(0.2, c.integral, delta);
        
        t += .3;
        assertEquals(2.8, c.update(1, t), delta);
        assertEquals(0.8, c.integral, delta);
        
        t += .1;
        assertEquals(3.0, c.update(1, t), delta);
        assertEquals(1.0, c.integral, delta);
        
        // Output is capped, but integral still grows.
        t += .1;
        assertEquals(3.0, c.update(1, t), delta);
        assertEquals(1.2, c.integral, delta);
        
        for (int i = 8; i <= 14; i += 2)
        {
            t += .2;
            assertEquals(3.0, c.update(1, t), delta);
            assertEquals(i * 0.2, c.integral, delta);
        }
        
        // Integral is capped.
        t += .1;
        assertEquals(3.0, c.update(1, t), delta);
        assertEquals(3.0, c.integral, delta);
        
        t += .1;
        assertEquals(3.0, c.update(1, t), delta);
        assertEquals(3.0, c.integral, delta);
    }
    
    public void testClamping()
    {
        PIController c = new PIController(1, 1, 10);
        
        assertEquals(0, c.update(0, 0), delta);
        assertEquals(0, c.integral, delta);

        assertEquals(-10, c.update(-1000, .1), delta);
        assertEquals(-1, c.integral, delta);
    }
    
    public void testIntegralReset()
    {
        PIController c = new PIController(1, 1, 10);
        
        assertEquals(1, c.update(1, 0.9), delta);
        assertEquals(0, c.integral, delta);
        
        assertEquals(1.1, c.update(1, 1.0), delta);
        assertEquals(0.1, c.integral, delta);

        // Too much time has passed, reset integral 
        assertEquals(1, c.update(1, 2.0), delta);
        assertEquals(0, c.integral, delta);
    }
}
