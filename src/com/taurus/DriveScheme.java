package com.taurus;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveScheme {

    public static final int HALO_DRIVE = 0;
    public static final int ANGLE_DRIVE = 1;

    private SendableChooser driveChooser = new SendableChooser();
    
    public DriveScheme()
    {
        driveChooser = new SendableChooser();
        driveChooser.addDefault("Halo Drive", Integer.valueOf(HALO_DRIVE));
        driveChooser.addObject("Angle Drive", Integer.valueOf(ANGLE_DRIVE));
        SmartDashboard.putData("Drive Chooser", driveChooser);
    }

    public int get()
    {
        return ((Integer)driveChooser.getSelected()).intValue();
    }
}
