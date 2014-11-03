Taurus Robotics Swerve Drive
======

2014 Offseason Swerve Drive project for Team 4818

1. Install [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
2. Install [eclipse luna](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1)
3. Configure JDK 7
  1. Window -> Preferences -> Java -> Installed JREs
  2. Add -> Standard VM -> Directory
  3. Choose the JDK you installed (e.g. C:\Program Files\Java\jdk1.7.0_71), click OK, click Finish
  4. Check the box next to the jdk you just added in the list of installed JREs, click OK
4. Install the [2014 sunspot SDK](http://www.team2168.org/tutorials/2014_Java_sunspotfrcsdk.zip) into C:\Users\<username>\sunspotfrcsdk
  1. Copy .sunspotfrc.properties into C:\Users\<username>
  2. Edit .sunspotfrc.properties to point to the correct path and the correct team number into 10.xx.yy.2
5. Clone the repo into the eclipse workspace, add existing project to eclipse
6. Configure eclipse to build the project
  1. Run -> External Tools -> External Tools Configuration 
  2. Select "Ant build", click the "New launch configuration" icon, and choose a name
  3. Set "Buildfile" to: ${workspace_loc:/${project_name}/build.xml}
  4. Click the Targets tab, check the box next to "jar-app"
7. Configure eclipse to deploy and run the project
  1. Run -> External Tools -> External Tools Configuration 
  2. Select "Ant build", click the "New launch configuration" icon, and choose a name
  3. Set "Buildfile" to: ${workspace_loc:/${project_name}/build.xml}
  4. Click the Targets tab, check the boxes next to "deploy" and "run" in that order
8. If it errors building, ${project_name} may need to be hard set to the project's name, i.e. Taurus)
9. If it errors on the preverify step, go to sunspotfrcsdk\bin and rename preverify (not preverify.exe) so eclipse can find the correct executable

