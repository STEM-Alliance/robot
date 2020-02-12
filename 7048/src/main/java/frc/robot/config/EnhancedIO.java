package frc.robot.config;

/** Maps controllers to Commands **/
public interface EnhancedIO
{
    /** How fast the robot is being requested to drive forwards/backwards (-1.0 to 1.0)*/
    public double getThrottle();
    /** How fast the robot is being requested to turn left/right (-1.0 to 1.0)*/
    public double getTurn();
    /** Override constant curvature turns for more raw, rate of heading change turns. Better at lower speeds*/
    public boolean getDriveQuickTurn();
    /** Is the drive requesting the autonomous drive command should end early, for safety*/
    public boolean isDriveOverrideRequested();
    /** Vibrate each Xbox controller*/
    public void setRumble(boolean rumble);
    
    public void assignButtons();
    
}
