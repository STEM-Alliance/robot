package com.taurus.robotspecific2015;

import com.taurus.UltrasonicMaxBotix;
import com.taurus.controller.Controller;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

public class UltrasonicDrive extends SwerveChassis {
    private UltrasonicMaxBotix ultra;
    private int output = 7;
    private int input = 8;
    private double driveRate = .1;

    public UltrasonicDrive(Controller controller)
    {
        super(controller);
        ultra = new UltrasonicMaxBotix(output, input);
        ultra.setAutomaticMode(true);

    }

    public void UltrasonicLineUp()
    {
        this.setFieldRelative(false);
        double distance = ultra.getRangeInches();
        double yvel = -(distance - 30) * driveRate;
        SwerveVector vec = new SwerveVector(0, yvel);
        this.UpdateDrive(vec, 0, -1);
    }

}
