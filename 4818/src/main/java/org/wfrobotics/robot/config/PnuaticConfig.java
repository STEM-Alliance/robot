package org.wfrobotics.robot.config;

/** @author STEM Alliance of Fargo Moorhead */
public class PnuaticConfig
{
    // Hardware
    public  int kAddressPCMGrippers;
    public  int kAddressPCMShifter;
    public  int kAddressPCMPoppers;
    public  int kAddressPCMLockers;
    public  int kAddressPCMPushUp;
    public  int kAddressPCMDeployer;
    public  int kAddressPCMMystory;
    
    // intake
    public  int kAddressSolenoidPoppersF;
    public  int kAddressSolenoidPoppersB;
    //climb -> Hug
    public  int kAddressSolenoidGrippersF;
    public  int kAddressSolenoidGrippersB;
    //elevator -> Shift
    public  int kAddressSolenoidShifterF;
    public  int kAddressSolenoidShifterB;
    // climb -> lock
    public  int kAddressSolenoidLockersF;
    public  int kAddressSolenoidLockersB;
    // climb -> Push Bumpers Above
    public  int KAddressSolenoidPushUpF;
    public  int KAddressSolenoidPushUpB;
    // climb -> shove the mech down
    public  int KAddressSolenoidDeployerF;
    public  int KAddressSolenoidDeployerB;
    // unknown
    public  int KAddressSolenoidMystoryF;
    public  int KAddressSolenoidMystoryB;

    // TODO public static class PneumaticSettings - PCM, forward, Optional backward
}
