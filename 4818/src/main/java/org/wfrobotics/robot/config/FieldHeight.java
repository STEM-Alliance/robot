package org.wfrobotics.robot.config;

/** All heights are relative distances from zero */
public enum FieldHeight
{
    HatchHigh(80.0, 181.5),   
    HatchMiddle(47.5, 181.5),  
    HatchLow(15.0, 181.0),

    CargoHigh(80.5, 96.1),   
    CargoMiddle(48.2, 96.0),  
    CargoLow(15.0, 95.2),
    
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