# SAFM 2022 Code


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
`https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix-frc2022-latest.json`

Repeat steps 1 through 3 then paste:
`https://www.kauailabs.com/dist/frc/2022/navx_frc.json`

`https://software-metadata.revrobotics.com/REVLib.json`

## Commands

- The VSCode Build Menu: `CTRL+SHFT+B`
  - Provides separate team commands to Build and Deploy
- VSCode Command Palette: `CTRL+SHFT+P`
  - Auto-fills on commands
- Deploy to the RoboRIO
  - `CTRL+SHIFT+P`
  - WPILib: Deploy Robot Code
- Simulate code
  - `CTRL+SHIFT+P`
  - WPILib: Simulate Robot Code

# Simulator

I'm still trying to figure out the simulator, but you can test out your control scheme and see how PWM motors respond. The SPARK Max motors don't show up.


# Robot Control

This code currently implements a two motor differential drive chassis using Spark MAX motor controllers. It uses an XBox and an arcade drive.

The left joystick drives the robot forward/back and left/right. Although I may have things backwards, we will need to test that. 

The shooter is also configured to use the buttons:
|Button|Description|
|---|---|
|A | 80% |
|B | 60% |
|X | 40% |