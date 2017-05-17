package org.wfrobotics.reuse.subsystems.motor2.examples;

import org.wfrobotics.reuse.subsystems.motor2.HerdMotor;
import org.wfrobotics.reuse.subsystems.motor2.examples.goals.Goal;
import org.wfrobotics.reuse.utilities.HerdLogger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A basic subsystem which controls a HerdMotor, specifying a setPoint for its PID
 * @author Team 4818 WFRobotics
 */
public class HerdMotorSubsystem extends Subsystem
{
    private final String NAME;
    
    private final HerdLogger log = new HerdLogger(HerdMotorSubsystem.class);
    private final HerdMotor smartMotor;
    private final Command defaultCmd;
    
    public HerdMotorSubsystem(String name, HerdMotor closedLoop, Command defaultCmd)
    {
        NAME = name;
        smartMotor = closedLoop;
        this.defaultCmd = defaultCmd;
    }
    
    public String toString()
    {
        return smartMotor.toString();
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(defaultCmd);
    }
    
    public void set(Goal desired)
    {
        double setpoint = desired.get();
        
        smartMotor.set(setpoint);
        smartMotor.update(setpoint);
        
        log.debug(NAME, this);
    }
    
    public boolean atSetpoint(Goal p)
    {
        return p.atSetpoint(smartMotor.get());
    }
}
