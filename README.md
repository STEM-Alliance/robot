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
- VSCode Command Prompt
  - Bring up a Powershell or Command Prompt window: ``CTRL+SHFT+` ``
  - `.\\gradlew tasks` will lists all available tasks to run

## Build Commands

`.\\gradlew :4360:build`, `.\\gradlew :4818:build`, `.\\gradlew :7048:build`, or `.\\gradlew :prototype:build`
Build will compile and get the code ready without deploying it. It will also run all automated tests, which is great for testing your code before it ever gets run on a robot (which also means you can build whenever)

## Deploy Commands

`.\\gradlew :4360:deploy`, `.\\gradlew :4818:deploy`, `.\\gradlew :7048:deploy`, or `.\\gradlew :prototype:deploy`
Deploying will build your code (as above), and deploy it to the robot. You have to be connected to the robot for this to work. Just keep in mind that deploying _does not run any automated tests_.

## Unit Tests Commands

Via the VSCode Command Palette (`CTRL+SHFT+P`), type `Test` (or `Tasks: Run Test Task`) and run the Unit Test project

## New Robot Project

1. Copy an existing folder (team/prototype)
2. Edit [settings.gradle](settings.gradle), adding the new folder name to list at end of file:
    ```Gradle
    include 'reuse', '4360', '4818', '7048', 'prototype', 'new folder name'
    ```
3. The new project can be built and deployed manually via a Command Prompt with `.\\gradlew :new folder name:deploy`
4. Or the command can be added to [.vscode\tasks.json](.vscode\tasks.json)