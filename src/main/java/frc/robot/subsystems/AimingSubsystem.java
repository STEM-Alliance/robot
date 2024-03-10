// package frc.robot.subsystems;

// import edu.wpi.first.wpilibj2.command.*;
// import frc.robot.Helpers;rkTableEntry;
// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.wpilibj2.command.*;

// public class AimingSubsystem extends SubsystemBase {

//   boolean m_linedUpForShot = false;

//   /** Creates a new DriveSubsystem. */
//   public AimingSubsystem() 
//   {
//   }

//   @Override

//   public void periodic() {
//     NetworkTable table = NetworkTableInstance.getDefault( ).getTable("limelight");
//     NetworkTableEntry tx = table.getEntry("tx");
//     NetworkTableEntry ty = table.getEntry("ty");
//     NetworkTableEntry ta = table.getEntry("ta");
//     NetworkTableEntry tid = table.getEntry("tid");

//     //read values periodically
//     double x = tx.getDouble(0.0);
//     double y = ty.getDouble(0.0);
//     double area = ta.getDouble(0.0);
//     double tidnum = tid.getDouble(0);

//     if (tidnum == Helpers.getDesiredAprilTag())
//     {
//       if (abs(tx) < 
//     }

//   }
// }