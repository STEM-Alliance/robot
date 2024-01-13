# SAFM 2024 Code


## Setup

- Install [WPILib Suite](https://github.com/wpilibsuite/allwpilib/releases/latest), following the included instructions.
  - VS Code, WPILib Code APIs, SmartDashboard, and other tools
  - Internet is required to first download the Vendor libraries (CTRE/NavX)
- Install [NI FRC Update Suite](http://www.ni.com/download/labview-for-frc-18.0/7841/en/), following the included instructions.
  - FRC Driver Station

## Install Third Party Librariers

1. In VSCode: `CTRL+SHIFT+P` 
2. then type `WPILib: Manage`
3. then go down to `Install New librariers (online)`
4. Paste the following into the dialog box:
This link may not work.
`https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix-frc2024-latest.json`

Repeat steps 1 through 3 then paste:
`https://www.kauailabs.com/dist/frc/2024/navx_frc.json`

`https://software-metadata.revrobotics.com/REVLib-2024.json`

## Configure Spark Max Controllers

The SPARK MAX Controllers have a really nice config app and documentation. The documentation can be found here: https://docs.revrobotics.com/sparkmax/gs-sm

The hardware client app can be found here: https://docs.revrobotics.com/sparkmax/rev-hardware-client/getting-started-with-the-rev-hardware-client

Connect a USB cable from your PC to the SPARK Max controller. The app will detect this and display the controller. From the app you can program firmware, configure the motor, and configure the CAN ID. **This must be unique from all controllers on the bus.**

## Configure the Talon SRX Controllers

These controllers are a little more difficult. Start at this page:
https://store.ctr-electronics.com/talon-srx/

Download the Phoenix Framework installer and the CTRE Device Firmware. 

![Phoenix Tuner](assets/phoenix_tuner_1.jpg)

After installing the framework you will start the `Phoenix Tuner` application.
 - Set the IP address in the Diagnostic Server Address to `10.TE.AM.2` where TE.AM is the team number: I.E. `10.70.48.2` or `10.43.60.2`.
 - Click `Run Temporary Diagnostic Server`
 - Click on `CAN Devices`
 - Now you can program all of the devices and configure their addresses

![Phoenix Tuner configure](assets/phoenix_tuner_2.jpg)

## Commands

- The VSCode Build Menu: `CTRL+SHFT+P`
  - Type `WPILib: Build Robot Code`
- VSCode Command Palette: `CTRL+SHFT+P`
  - Auto-fills on commands
- Deploy to the RoboRIO
  - `CTRL+SHIFT+P`
  - Type: `WPILib: Deploy Robot Code`
- Simulate code
  - `CTRL+SHIFT+P`
  - Type: `WPILib: Simulate Robot Code`

# Simulator

I'm still trying to figure out the simulator, but you can test out your control scheme and see how PWM motors respond. The SPARK Max motors don't show up.

# Robots

The idea is we break common code pieces into their own classes, but have two different RobotXXXX classes. Then in Main.java, you can uncomment the robot you want to run.


# Motor Current Limits
ALL motor will have current limits setup. Below is a table of the current limits that are deemed good:

```
  static public int NeoLimit = 80;
  static public int Neo550Limit = 20;
  static public int BagMotorLimit = 30; // Max power is 149 W, 12.4 A
  static public int M775ProLimit = 15; // Max power 347 W, 28.9 A
  static public int CIMSLimit = 28; // Max power 337 W, 28.0 A
  // https://firstwiki.github.io/wiki/denso-window-motor
  static public int WindowLimit = 15; // This seems safe
```