package org.wfrobotics.robot.config;

/** All heights are relative distances from zero */
public enum FieldHeight
{
    HatchHigh(86, 181.5),   
    HatchMiddle(57, 181.5),  
    HatchLow(15.0, 181.0),

    CargoHigh(80.5, 78),   
    CargoMiddle(48.2, 78.0),  
    CargoLow(15.0, 78),
    
    CargoPickup(15.0, 180),
    HatchPickup(15.0, 181.1),

    CargoBay(15.0, 20),
    CargoBoxPickup(15.0, 158);

    private final double elevator;
    private final double link;

    private FieldHeight(double elevator, double link) 
    {
         this.elevator = elevator;
         this.link = link;
     }
    public double getE() { return elevator; }
    public double getL() { return link; }

}