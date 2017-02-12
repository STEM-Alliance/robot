
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.controller.Panel.COLOR;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSwerveCalibration extends Command 
{
    public enum MODE {PANEL, SINGLE, FULL}
    
    private final MODE mode;

    private SendableChooser<Integer> testWheelChooser;
    private double[] m_wheelCalibrations;
    
    public DriveSwerveCalibration(MODE mode)
    {
        requires(Robot.driveSubsystem);
        this.mode = mode;
    }

    protected void initialize()
    {
        switch(mode)
        {
            case FULL:
                break;
                
            case PANEL:
                m_wheelCalibrations = Robot.driveSubsystem.getWheelCalibrations();
                break;
                
            case SINGLE:
                testWheelChooser = new SendableChooser<Integer>();
                testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
                testWheelChooser.addObject("Front Right", Integer.valueOf(1));
                testWheelChooser.addObject("Back Right", Integer.valueOf(2));
                testWheelChooser.addObject("Back Left", Integer.valueOf(3));
                SmartDashboard.putData("Test Wheel", testWheelChooser);
                break;
            default:
                break;
        }

    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this, mode.toString());

        switch(mode)
        {
            case FULL:
                Robot.driveSubsystem.setFieldRelative(false);
                Robot.driveSubsystem.driveVector(Vector.NewFromMagAngle(.75, 0), 0);
                break;
                
            case PANEL:
                double values[] = OI.DriveSwerveOI.getPanelKnobs();
                boolean save = OI.DriveSwerveOI.getPanelSave();
                
                COLOR leds[];
                
                if(save)
                {
                    leds = new COLOR[] { COLOR.RED, COLOR.RED, COLOR.RED, COLOR.RED};
                }
                else
                {
                    leds = new COLOR[] { COLOR.GREEN, COLOR.GREEN, COLOR.GREEN, COLOR.GREEN};
                }
                
                OI.setPanelLEDs(leds, leds);
                
                SmartDashboard.putNumber("PanelCalibration0", values[0]);
                SmartDashboard.putNumber("PanelCalibration1", values[1]);
                SmartDashboard.putNumber("PanelCalibration2", values[2]);
                SmartDashboard.putNumber("PanelCalibration3", values[3]);
                
                Robot.driveSubsystem.fullWheelCalibration(.5, values, save);
                break;
                
            case SINGLE:
                int i = ((Integer) testWheelChooser.getSelected()).intValue();

                //SmartDashboard.putNumber("Test Wheel", i);
                SmartDashboard.putNumber("Vel Ang", OI.DriveSwerveOI.getHaloDrive_Velocity().getAngle());
                SmartDashboard.putNumber("Vel Mag", OI.DriveSwerveOI.getHaloDrive_Velocity().getMag());
                
//                Vector WheelActual = ((SwerveDriveSubsystem)Robot.driveSubsystem).getWheel(i).setDesired(
//                        OI.DriveSwerveOI.getHaloDrive_Velocity(),
//                        OI.DriveSwerveOI.getHighGearEnable(),
//                        false);
//                    
//                // display in SmartDashboard
//                SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
//                SmartDashboard.putNumber("Test Wheel Angle Actual", WheelActual.getAngle());
                break;
            default:
                break;
        }
        
        
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
    }

    protected void interrupted() 
    {
    }
}
