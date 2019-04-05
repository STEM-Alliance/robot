package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.robot.commands.ConserveCompressor;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SuperStructure extends SuperStructureBase
{
    static class SingletonHolder
    {
        static SuperStructure instance = new SuperStructure();
    }

    public static SuperStructure getInstance()
    {
        return SingletonHolder.instance;
    }

    private static final double kCargoInPercent = 0.75;
    private static final double kHatchInInches = 12.0;

    private final Canifier jeff = new Canifier(6, new RGB(255, 255, 0));
    private final CircularBuffer cargoBuffer = new CircularBuffer(3, false);

    public SuperStructure()
    {
        jeff.setDigitalInputFramePeriod(10);  // Faster cargo digitals
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }

    public void cacheSensors(boolean isDisabled)
    {
        jeff.cacheSensors(false);  // Reads PWM0 & PWM1
        final boolean cargoLeft = jeff.getPWM0();
        final boolean cargoRight = jeff.getPWM1();

        cargoBuffer.addFirst(cargoRight || cargoLeft);        
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Cargo", getHasCargo());
        SmartDashboard.putBoolean("Hatch", getHasHatch());
    }
    
    public boolean getHasCargo()
    {
        return cargoBuffer.getAverage() >= kCargoInPercent;
    }

    public boolean getHasHatch()
    {
        return !Intake.getInstance().getGrabbersExtended();
    }

    public Canifier getJeff()
    {
        return jeff;
    }

    public double getDistanceFromWall()
    {
        return 0.0;
    }

    public double getAngleFromWall( )
    {
        return 0.0;   
    }
    
    @Override
    public TestReport runFunctionalTest()
    {
        TestReport report = super.runFunctionalTest();
        
        report.add(jeff.runFunctionalTest());

        jeff.testRobotSpecificColors();
        report.addManualTest("Tested Jeff's LED's");

        return report;
    }
}
