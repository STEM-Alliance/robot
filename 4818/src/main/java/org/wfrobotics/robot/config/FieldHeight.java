package org.wfrobotics.robot.config;

/** All heights are relative distances from zero */
public enum FieldHeight
{

    HatchHigh(62, 100.0),   
    HatchMiddle(39.0, 100.0),  
    HatchLow(15.0, 100.0),

    // Cargo are ~9 inches above hatch with the same link angle
    CargoHigh(70.0, 92.0),
    CargoMiddle(48.0, 78.0),
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