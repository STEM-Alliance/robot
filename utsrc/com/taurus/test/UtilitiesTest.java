package com.taurus.test;

import com.taurus.Utilities;

import junit.framework.TestCase;

public class UtilitiesTest extends TestCase
{
    public static final double delta = 0.00001;
    
    public void testScaleToRange()
    {
        assertEquals(102, Utilities.scaleToRange(1.2, 1, 2, 100, 110), delta);
        assertEquals(100, Utilities.scaleToRange(.9, 1, 2, 100, 110), delta);
        assertEquals(110, Utilities.scaleToRange(1000, 1, 2, 100, 110), delta);
        assertEquals(107, Utilities.scaleToRange(1.3, 1, 2, 110, 100), delta);
        assertEquals(-107, Utilities.scaleToRange(1.3, 1, 2, -110, -100), delta);
        assertEquals(-103, Utilities.scaleToRange(1.3, 1, 2, -100, -110), delta);
    }
    
    public void testWrapToRange()
    {
        assertEquals(1, Utilities.wrapToRange(1, 0, 10), delta);
        assertEquals(0, Utilities.wrapToRange(100, 0, 10), delta);
        assertEquals(-9, Utilities.wrapToRange(11, -10, 10), delta);
        assertEquals(9, Utilities.wrapToRange(-11, -10, 10), delta);
        assertEquals(12, Utilities.wrapToRange(2, 10, 20), delta);
        assertEquals(-18, Utilities.wrapToRange(2, -20, -10), delta);
    }
}
