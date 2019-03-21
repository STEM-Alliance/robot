package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.robot.commands.ConserveCompressor;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
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
    private final AnalogInput ultra3;
    private final SharpDistance distanceL;
    private final SharpDistance distanceR;
    private final CircularBuffer cargoBuffer = new CircularBuffer(3, false);
    private final CircularBuffer hatchBuffer = new CircularBuffer(3, false);

    public SuperStructure()
    {
        final RobotConfig config = RobotConfig.getInstance();
        
        distanceL = new SharpDistance(config.kAddressInfraredL);
        distanceR = new SharpDistance(config.kAddressInfraredR);
        ultra3 = new AnalogInput(config.kAddressUltrasonic);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }
    
    public void cacheSensors(boolean isDisabled)
    {
        jeff.cacheSensors(false);  // Read PWM0 & PWM1
        final double hatchDistance = getUltraDistanceRaw();
        final boolean cargoLeft = jeff.getPWM0();
        final boolean cargoRight = jeff.getPWM1();

        hatchBuffer.addFirst(hatchDistance);
        cargoBuffer.addFirst(cargoRight || cargoLeft);        
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Cargo", getHasCargo());
        SmartDashboard.putBoolean("Hatch", getHasHatch());

        SmartDashboard.putNumber("Ultra3", getUltraDistance() );
        
        // SmartDashboard.putNumber("Tape Vision Angle", getTapeYaw());
        SmartDashboard.putBoolean("Tape In view", getTapeInView());
    }
    
    public boolean getHasCargo()
    {
        return cargoBuffer.getAverage() >= kCargoInPercent;
    }

    public boolean getHasHatch()
    {
        return hatchBuffer.getAverage() < kHatchInInches;
    }

    public Canifier getJeff()
    {
        return jeff;
    }

    public double getUltraDistance()
    {
        return hatchBuffer.getAverage();
    }

    private double getUltraDistanceRaw()
    {
        double m_conversionToInches = 1000.0 / .977 / 25.4;
        return (ultra3.getVoltage() *m_conversionToInches);
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

    public double getDistanceFromWall()
    {
        return 0.0;
    }

    public double getAngleFromWall( )
    {
        return 0.0;   
    }

    final NetworkTableInstance netInstance = NetworkTableInstance.getDefault();
    final NetworkTable chickenVision = netInstance.getTable("ChickenVision");

    private boolean driverVision, 
                    tapeVision, 
                    cargoVision, 
                    cargoSeen, 
                    tapeSeen;
    private NetworkTableEntry tapeDetected, 
                              cargoDetected,    
                              tapeYaw, 
                              cargoYaw,
                              videoTimestamp;

    public boolean getTapeInView()  {   return chickenVision.getEntry("tapeDetected").getBoolean(false);    }
    public boolean getCargoInView() {   return chickenVision.getEntry("cargoDetected").getBoolean(false);   }
    public double getTapeYaw()  {   return chickenVision.getEntry("tapeYaw").getDouble(0.0);    }
    public double getCargoYaw() {   return chickenVision.getEntry("cargoYaw").getDouble(0.0);   }
    public boolean getDriveCamera() {   return chickenVision.getEntry("Driver").getBoolean(false);  }
    public boolean getTapeCamera()  {   return chickenVision.getEntry("Tape").getBoolean(false);    }
    public boolean getCargoCamera() {   return chickenVision.getEntry("Cargo").getBoolean(false);   }
    public double getLastTimestamp(){   return chickenVision.getEntry("VideoTimestamp").getDouble(0.0); }
}
