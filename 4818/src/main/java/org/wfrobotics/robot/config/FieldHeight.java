package org.wfrobotics.robot.config;

/** All heights are relative distances from zero */
public enum FieldHeight
{
    HatchHigh(84.0, 88.0),   
    HatchMiddle(53.8, 95.5),  
    HatchLow(0.0, 95.5),

    CargoHigh(71.5, 4.0),   
    CargoMiddle(36.0, 4.0),  
    CargoLow(0.0, 4.0),
    
    CargoPickup(0.0, 140.2),
    HatchPickup(0.0, 95.2);

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