package com.taurus.robotspecific2015;

import com.taurus.controller.Controller;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.Ultrasonic;

public class UltraSonicDrive extends SwerveChassis {
    private Ultrasonic ultra;
    private int output = 7;
    private int input = 8;
    private double driveRate = .1;

    public UltraSonicDrive(Controller controller)
    {
        super(controller);
        ultra = new Ultrasonic(output, input);
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
