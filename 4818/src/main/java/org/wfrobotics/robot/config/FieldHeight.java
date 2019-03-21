package org.wfrobotics.robot.config;

/**
 * Elevator height:    Inches from ground to bottom of intake when link is horizontal
 *                     Meaning it's zero when intake is resting on floor
 * 
 * Link angle:         Degrees from zero, where horizontal is 90
 *                     Meaning it's 90 when intake is resting on floor
 * 
 * Elevator vs Rocket: If the link is horizontal, elevator height will be ground to bottom of intake
 * 
 * Rocket:             
 *    Bottom of Cargo: 16" diameter holes, centered at 27.5" from floor, holes 27" center to center
 *                        High:   73.5"
 *                        Middle: 46.5"
 *                        Low:    19.5"
 *    Bottom of Hatch: 16.5" diameter holes, 19" from ground to center of lowest, holes 27" center to center
 *                        High:   64.75"
 *                        Middle: 37.75"
 *                        Low:    10.75"
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public enum FieldHeight
{
    HatchHigh(62, 100.0),   
    HatchMiddle(39.0, 100.0),  
    HatchLow(15.0, 100.0),

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