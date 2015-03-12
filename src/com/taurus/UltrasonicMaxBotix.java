package com.taurus;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Ultrasonic rangefinder class for MaxBotix sensors. Uses the built in 
 * Ultrasonic class, but auto converts the distances based on the datasheet
 * for MaxBotix sensors.
 */
public class UltrasonicMaxBotix extends Ultrasonic {

    // grabbed from Ultrasonic.class
    private static final double UltrasonicConstant = 1130.0 * 12.0 / 2.0;
    
    // grabbed from MaxBotix datasheet, 1uS == 1mm
    private double uS_Per_MM = 1.0;
    
    private static final double mmPerIn = 25.4;
    private static final double uS_Per_S = 1000000.0;
        
    /**
     * Manually set/change the number of uSeconds per MM
     * @param val
     */
    public void setUSPerMM(double val)
    {
        uS_Per_MM = val;
    }

    /**
     * Get the number of uSeconds per MM
     * @return
     */
    public double setUSPerMM()
    {
        return uS_Per_MM;
    }
    
    public UltrasonicMaxBotix(int pingChannel, int echoChannel)
    {
        super(pingChannel, echoChannel);
    }

    public UltrasonicMaxBotix(DigitalOutput pingChannel,
            DigitalInput echoChannel)
    {
        super(pingChannel, echoChannel);
    }

    public UltrasonicMaxBotix(int pingChannel, int echoChannel, Unit units)
    {
        super(pingChannel, echoChannel, units);
    }

    public UltrasonicMaxBotix(DigitalOutput pingChannel,
            DigitalInput echoChannel, Unit units)
    {
        super(pingChannel, echoChannel, units);
    }

    public double getRangeInches()
    {
        return super.getRangeInches() / UltrasonicConstant * (uS_Per_MM * uS_Per_S) / mmPerIn; 
    }
    
    public double getRangeMM()
    {
        return super.getRangeMM() / UltrasonicConstant * (uS_Per_MM * uS_Per_S); 
    }
    
    public double pidGet()
    {
        if(super.getDistanceUnits() == Unit.kInches)
        {
            return getRangeInches();
        }
        else if(super.getDistanceUnits() == Unit.kMillimeter)
        {
            return getRangeMM();
        }
        else
        {
            return 0.0;
        }
    }
    
    private ITable m_table;

    /**
     * {@inheritDoc}
     */
    public void initTable(ITable subtable) {
        m_table = subtable;
        updateTable();
    }

    /**
     * {@inheritDoc}
     */
    public ITable getTable() {
        return m_table;
    }

    /**
     * {@inheritDoc}
     */
    public void updateTable() {
        if (m_table != null) {
            m_table.putNumber("Value", getRangeInches());
        }
    }
}
