// package frc.robot.subsystems;

// import frc.robot.Configuration;
// import frc.robot.Robot;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.controller.ProfiledPIDController;
// import edu.wpi.first.math.trajectory.TrapezoidProfile;
// import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.*;
// import com.revrobotics.CANSparkMax;
// import com.revrobotics.RelativeEncoder;
// import com.revrobotics.CANSparkLowLevel.MotorType;
// import frc.robot.commands.*;

// public class ShooterSubsystem extends SubsystemBase {
//     private final CANSparkMax m_shooter;
//     private final CANSparkMax m_shooter2;
//     private final CANSparkMax m_armMotor;
//     private final RelativeEncoder m_armEnc;
//     private final RelativeEncoder m_shooterEnc;

//     private final DigitalInput m_limitSwitch = new DigitalInput(Configuration.kArmLimitswitchChannel);

//     private final PIDController m_shooterPID = new PIDController(Configuration.kShooterArmKp,
//     Configuration.kShooterArmKi, Configuration.kShooterArmKd);

//     // private final PIDController m_shooterPID = new PIDController(Configuration.kShooterArmKp,
//     //     Configuration.kShooterArmKi, Configuration.kShooterArmKd);

//     // private final IntakeSubsystem m_intake;
//     // private final DigitalInput m_noteSensor;
//     // private final LimelightSubsystem m_limelight;

//     private int anglePosition = 0;
//     private final double[] anglePositions = Configuration.kShooterArmPositions;

//     private final int[] shooterMotorChannels = Configuration.kShooterMotorChannels;
//     private final int shooterArmMotorChannel = Configuration.kShooterArmMotorChannel;

//     public double setTargetAngle;
//     public boolean atTargetAngle;

//     /** Creates a new ShooterSubsystem. */
//     public ShooterSubsystem() {
        
//         // m_intake = null;
//         // m_limelight = null;
//         // m_noteSensor = null;
        
//         // Make a CANSparkMax that controls both of the shooter motors
//         m_shooter = new CANSparkMax(shooterMotorChannels[0], MotorType.kBrushless);
//         m_shooter.setSmartCurrentLimit(Configuration.NeoLimit);
//         m_shooter2 = new CANSparkMax(shooterMotorChannels[1], MotorType.kBrushless);
//         m_shooter2.setSmartCurrentLimit(Configuration.NeoLimit);

//         m_shooter2.follow(m_shooter);

//         m_armMotor = new CANSparkMax(shooterArmMotorChannel, MotorType.kBrushless);
//         m_armMotor.setSmartCurrentLimit(Configuration.NeoLimit);
        
//         m_armEnc = m_armMotor.getEncoder();
//         m_armEnc.setPositionConversionFactor(360/230 * 1.5);
//         m_armEnc.setPosition(0);

//         m_shooterEnc = m_shooter.getEncoder();
//     }

//     public void periodic() {
//         SmartDashboard.putNumber("target angle", setTargetAngle);
//         SmartDashboard.putNumber("armangle", m_armEnc.getPosition());
//         SmartDashboard.putNumber("shooter_rps", m_shooterEnc.getVelocity()/60);

//         angleShooter(setTargetAngle);
//     }

//     // public void movementLoop() {
//     //     angleShooter(setTargetAngle);
//     // }

//     /* Only if note is in intake, spin up motors, align with limelight and calculate shooting angle
//     When motors up to speed and at target angle, shoot note and move to down position */
//     // public void shootNoteSpeaker() {
//     //     // Make sure that there is a note in the intake
//     //     // if (!m_noteSensor.get()) {
//     //     //     return;
//     //     // }

//     //     // spin shooter function
//     //     // is at target angle function
//     //     // command for limelight to align to speaker
//     //     // shoot note at speaker command, waits until all true and then
//     //     // runs the feed note into shooter command

//     //     /* Spin up the shooter motors and angle the shooter while
//     //     waiting for the robot to align to the speaker */
//     //     // boolean isShooterReady = spinShooter();
//     //     // boolean isRobotAligned = m_limelight.alignToSpeaker();
//     //     // boolean isShooterAngled = angleShooter(false);

//     //     // // If all the requirements are not met, wait for the next Periodic() loop
//     //     // while (!isShooterReady && isShooterAngled && isRobotAligned) {
//     //     //     // Wait for the next Periodic() loop
//     //     //     return;
//     //     // }

//     //     angleShooter(false);
//     // }

//     // public void shootNoteAmp() {
//     //     // if (!m_noteSensor.get()) {
//     //     //     return;
//     //     // }

//     //     // spinShooter()
//     //     // .alongWith(AlignToAmp);
//     //     //.alongWith(angleShooter(false));
//     //     // boolean isRobotAligned = m_limelight.alignToAmp();
//     //     // boolean isShooterAngled = angleShooter(true);
//     // }

//     // public void shootNote() {
//     //     return;
//     // }

//     // public boolean moveToIntakePos() {
//     //     return angleShooter(anglePositions[0]);
//     // }

//     private double calculateShooterAngle(double Distance) {
//         /* Example logarithmic curve with values
//         {{0.25, 85}, {0.5, 60}, {1, 55}, {2, 35}} (meters, degrees) */
//         return 52.5 - 22.3618 * Math.log(Distance);
//     }

//     // private void angleShooter(boolean isTargetAmp) {
//     //     if (isTargetAmp) {
//     //         angleShooter(anglePositions[2]);
//     //     }

//     //     // double distanceToSpeaker = m_limelight.getDistanceToSpeaker();
//     //     // double targetShooterAngle = calculateShooterAngle(distanceToSpeaker);

//     //     // targetAngle = targetShooterAngle;
//     // }

//     private void angleShooter(double targetAngle) {
//         // Drive the motors to the shooters angle to whatever the target angle is
//         double shooterAngle = m_armEnc.getPosition();

//         double output = m_shooterPID.calculate(shooterAngle, targetAngle);

//         // Limit the PID output
//         if (output > Configuration.kShooterArmLimit)
//         {output = Configuration.kShooterArmLimit;}

//         else {if (output < -Configuration.kShooterArmLimit)
//         {output = -Configuration.kShooterArmLimit;}}

//         SmartDashboard.putNumber("pid out", output);

//         m_armMotor.set(output);

//         // Return true if the arm is at the setpoint
//         if (Math.abs(output) <= 0.025) {
//             atTargetAngle = true;
//         } else {
//             atTargetAngle = false;
//         }
//     }

//     // Spin the shooter motor and return true when the velocity is above the minimum
//     public Command spinShooter(boolean force) {
//         return new FunctionalCommand(
//             () -> m_shooter.set(-1),
//             () -> {},
//             interrupted -> m_shooter.set(0),
//             () -> !force && Math.abs(m_shooterEnc.getVelocity()) >= Configuration.kMinFlywheelSpeed,
//             this
//         );
//     }

//     public Command unhookShooter() {
//         return new FunctionalCommand(
//             () -> {},
//             () -> setPosition(-1),
//             interrupted -> {},
//             () -> m_armEnc.getPosition() > 5,
//             this
//         );
//     }

//     public Command lowerShooter() {
//         return new FunctionalCommand(
//         () -> System.out.println("Lowering shooter"),
//         () -> setPosition(1),
//         interrupted -> {},
//         () -> m_limitSwitch.get(),
//         this
//         );
//     }

    
//     public Command raiseShooter(double rate) {
//         return new InstantCommand();
//     }

//     public Command lowerShooter(double rate) {
//         return new InstantCommand();
//     }

//     public boolean setPosition(double upDownChange) {
//         double shooterAngle = m_armEnc.getPosition();

//         upDownChange = -upDownChange;

//         if ((m_limitSwitch.get() && upDownChange < 0) || (shooterAngle >= Configuration.kArmBackwardsLimit && upDownChange > 0)){
//             System.out.println("setPosition at limit");
//             return true;
//         } else {
//             setTargetAngle += upDownChange / 2;
//             return false;
//         }
//     }

//     private void movePosition() {
//         // Limit anglePosition to 0-1, 0 is intake and 1 is travel position
//         if (anglePosition > 1) {anglePosition = 1;}
//         else {if (anglePosition < 0) {anglePosition = 0;}}

//         setTargetAngle = anglePositions[anglePosition];
//     }

//     public Command movePositionUp() {
//         anglePosition++;
//         movePosition();

//         return new InstantCommand();
//     }

//     public Command movePositionDown() {
//         anglePosition--;
//         movePosition();

//         return new InstantCommand();
//     }
// }
