package org.wfrobotics.utilities;

import org.wfrobotics.Vector;
import org.wfrobotics.subsystems.drive.swerve.Constants;
import org.wfrobotics.subsystems.drive.swerve.WheelManager.RobotCommand;

public class Tests
{
    public static void main(String[] args)
    {
        debugFastTrig();
        debugHerdVector();
    }
        
    public static void debugHerdVector()
    {
        // New Vector: Trig
        HerdVector a = new HerdVector(1, 60);
        Vector B = Vector.NewFromMagAngle(1, 60);
        long start;

        start = System.nanoTime();
        System.out.println("A.X: " + a.getX());
        System.out.println("A.Y: " + a.getY());
        System.out.println("Durration: " + ((System.nanoTime() - start)/1000) + " ns");
        start = System.nanoTime();
        System.out.println("B.X: " + B.getX());
        System.out.println("B.Y: " + B.getY());
        System.out.println("Durration: " + ((System.nanoTime() - start)/1000) + " ns");
        System.out.println();
        
        // New Vector: Auto-Wrap
        System.out.println("(1, 181) -->" + new HerdVector(1, 181));
        System.out.println("(1, -181) -->" + new HerdVector(1, -181));
        System.out.println("(1, 361) -->" + new HerdVector(1, 361));
        System.out.println();
        
        // New Vector: Auto-Pos-Mag
        System.out.println("(-1, 181) -->" + new HerdVector(-1, 181));
        System.out.println("(-1, -181) -->" + new HerdVector(-1, -181));
        System.out.println("(-1, 361) -->" + new HerdVector(-1, 361));
        System.out.println();
        
        // New Vector: Rotate
        a = new HerdVector(1, 0);
        HerdVector b = new HerdVector(2, -90);
        double gyro = -45;
        
        System.out.println("A commanded: " + a);
        System.out.println("A.X commanded: " + a.getX());
        System.out.println("Gyro: " + gyro);
        a = a.rotate(-gyro);
        System.out.println("A rotate to field relative: " + a);
        System.out.println("A.X rotate to field relative: " + a.getX());
        a = a.rotate(b);
        System.out.println("A rotate by B: " + a);
        a = a.rotate(-136);
        System.out.println("A rotate by -136: " + a);
        System.out.println();

        // New Vector: Scale
        a = new HerdVector(1, 0);
        b = new HerdVector(2, 0);
        
        System.out.println("A before scale B: " + a);
        a = a.scale(b);
        System.out.println("A after scale B: " + a);
        a = a.scale(-.25);
        System.out.println("A after scale mag: " + a);  // TODO DRL Should this produce a negative mag or rotate by 180?!?! Probably the latter?
        System.out.println();
        
        // New Vector: Clone, Angle Between
        a = new HerdVector(1, 0);
        System.out.println("A before clone mod: " + a);
        b = new HerdVector(a);
        
        b = b.rotate(45);
        System.out.println("B after clone mod: " + b);
        System.out.println("A after clone mod: " + a);
        System.out.println();
        
        // New Vector: Angle Between
        a = new HerdVector(1, 179);
        b = new HerdVector(a);
        
        System.out.println("Angle A relative to B: " + a.angleRelativeTo(b));
        System.out.println("Angle A relative to A: " + a.angleRelativeTo(a));
        b = b.rotate(2);
        System.out.println("Angle A relative to B: " + a.angleRelativeTo(b));
        System.out.println("Angle B relative to A: " + b.angleRelativeTo(a));
        System.out.println();
        
        // Addition
        a = new HerdVector(1, -135);
        b = new HerdVector(1, -135);
        
        System.out.println(a = a.add(b));
        System.out.println(a.add(b));
        System.out.println();
        
        // Cartesian Wheel Vectors
//        double CHASSIS_WIDTH = 24.75;
        double CHASSIS_WIDTH = 4;
//        double CHASSIS_DEPTH = 14.25;
        double CHASSIS_DEPTH = 3;
        double CHASSIS_SCALE = Math.sqrt(CHASSIS_WIDTH * CHASSIS_WIDTH + CHASSIS_DEPTH * CHASSIS_DEPTH);
        
        double[][] POSITIONS = {
                { -CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }, // front left
                { CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }, // front right
                { CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back right
                { -CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE } }; // back left
        Vector[] positions = new Vector[4];
        Vector robotVector = new Vector(1,2);
        double spin = 1;
        RobotCommand robotCommand = new RobotCommand(robotVector, spin);
        System.out.format("Robot Command (%.2f, %.2f, %.2f)\n", robotCommand.velocity.getMag(), robotCommand.velocity.getAngle(), robotCommand.spin);
        System.out.format("Robot Width: %.2f, Depth: %.2f\n\n", CHASSIS_WIDTH, CHASSIS_DEPTH);
        for (int index = 0; index < Constants.WHEEL_COUNT; index++)
        {
            positions[index] = new Vector(POSITIONS[index]);
            System.out.format("Old position %d: (%.2f, %.2f)\n", index, positions[index].getMag(), positions[index].getAngle());
        }
        
        start = System.nanoTime();
        Vector[] scaled = oldScaleWheelVectors(robotCommand, positions);

        System.out.println("Wheel Calcs Cartesian: " + ((System.nanoTime() - start)/1000) + " ns");        
        for (int index = 0; index < scaled.length; index++)
        {
            System.out.format("Old wheel %d: (%.2f, %.2f)\n", index, scaled[index].getMag(), scaled[index].getAngle());
        }
        System.out.println();
        
        // Polar Wheel Positions
        double offsetX = CHASSIS_WIDTH;
        double offsetY = CHASSIS_DEPTH;
        
        HerdVector rBR = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(offsetY, -offsetX) * 180 / Math.PI);
        HerdVector rFR = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(offsetY, offsetX) * 180 / Math.PI);
        HerdVector rFL = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(-offsetY, offsetX) * 180 / Math.PI);
        HerdVector rBL = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(-offsetY, -offsetX) * 180 / Math.PI);
        
        double unitVectorCorrection = 1 / rBR.getMag();
        rBR = rBR.scale(unitVectorCorrection);
        rFR = rFR.scale(unitVectorCorrection);
        rFL = rFL.scale(unitVectorCorrection);
        rBL = rBL.scale(unitVectorCorrection);
        
        System.out.println("New position BR: " + rBR);
        System.out.println("New position FR: " + rFR);
        System.out.println("New position FL: " + rFL);
        System.out.println("New position BL: " + rBL);
        
        // New Wheel Vectors
        HerdVector r = new HerdVector(robotVector.getMag(), robotVector.getAngle());
        
        start = System.nanoTime();
        
        // w x r
        HerdVector wXBR = new HerdVector(spin * rBR.getMag(), rBR.getAngle() + 90);
        HerdVector wXFR = new HerdVector(spin * rFR.getMag(), rFR.getAngle() + 90);
        HerdVector wXFL = new HerdVector(spin * rFL.getMag(), rFL.getAngle() + 90);
        HerdVector wXBL = new HerdVector(spin * rBL.getMag(), rBL.getAngle() + 90);
        
//        System.out.println("bR X w: " + bRcrossW);
//        System.out.println("fR X w: " + fRcrossW);
//        System.out.println("fL X w: " + fLcrossW);
//        System.out.println("bL X w: " + bLcrossW);
//
//        System.out.format("bR X w X: %.2f Y: %.2f\n", bRcrossW.getX(), bRcrossW.getY());
//        System.out.format("fR X w X: %.2f Y: %.2f\n", fRcrossW.getX(), fRcrossW.getY());
//        System.out.format("fL X w X: %.2f Y: %.2f\n", fLcrossW.getX(), fLcrossW.getY());
//        System.out.format("bL X w X: %.2f Y: %.2f\n", bLcrossW.getX(), bLcrossW.getY());
        
        // v + w x r
        HerdVector wheelBR = r.add(wXBR);
        HerdVector wheelFR = r.add(wXFR);
        HerdVector wheelFL = r.add(wXFL);
        HerdVector wheelBL = r.add(wXBL);
        
        double maxMag = wheelBR.getMag();        
        maxMag = (wheelFR.getMag() > maxMag) ? wheelFR.getMag(): maxMag;
        maxMag = (wheelFL.getMag() > maxMag) ? wheelFL.getMag(): maxMag;
        maxMag = (wheelBL.getMag() > maxMag) ? wheelBL.getMag(): maxMag;
        
        if (maxMag > 1)
        {
            wheelBR = wheelBR.scale(1 / maxMag);
            wheelFR = wheelFR.scale(1 / maxMag);
            wheelFL = wheelFL.scale(1 / maxMag);
            wheelBL = wheelBL.scale(1 / maxMag);
        }
        
        // Mirroring Y is purely to match our old swerve. Seems like an extra step beyond what math says we need
        wheelBR = new HerdVector(wheelBR.getMag(), -wheelBR.getAngle());
        wheelFR = new HerdVector(wheelFR.getMag(), -wheelFR.getAngle());
        wheelFL = new HerdVector(wheelFL.getMag(), -wheelFL.getAngle());
        wheelBL = new HerdVector(wheelBL.getMag(), -wheelBL.getAngle());
        
        System.out.println("Wheel Calcs Polar: " + ((System.nanoTime() - start)/1000) + " ns");
        System.out.println("New Wheel BR: " + wheelBR);
        System.out.println("New Wheel FR: " + wheelFR);
        System.out.println("New Wheel FL: " + wheelFL);
        System.out.println("New Wheel BL: " + wheelBL);
    }
    
    private static Vector[] oldScaleWheelVectors(RobotCommand robot, Vector[] positions)
    {
        Vector[] WheelsUnscaled = new Vector[Constants.WHEEL_COUNT];
        Vector[] WheelsScaled = new Vector[Constants.WHEEL_COUNT];
        double MaxWantedVeloc = 0;
        double VelocityRatio;
        boolean ENABLE_VELOCITY_LIMIT = true;        

        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            WheelsUnscaled[i] = new Vector(robot.velocity.getX() - robot.spin * positions[i].getY(),
                    -(robot.velocity.getY() + robot.spin * positions[i].getX()));

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        VelocityRatio = (ENABLE_VELOCITY_LIMIT) ? oldGetVelocityLimit(MaxWantedVeloc):1;

        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            // Scale values for each wheel
            WheelsScaled[i] = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());
        }

        return WheelsScaled;
//        return WheelsUnscaled;
    }
    
    public static double oldGetVelocityLimit(double MaxWantedVeloc)
    {
        double velocityMaxAvailable = 1;
        double velocityRatio = 1;

        // Determine ratio to scale all wheel velocities by
        velocityRatio = velocityMaxAvailable / MaxWantedVeloc;

        velocityRatio = (velocityRatio > 1) ? 1:velocityRatio;

        return velocityRatio;
    }
    
    public static void debugFastTrig()
    {
        long start;
        long durMathSin;
        long durSin;
        long durCos;
        long durSin2;
        double sum = 0;
        
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += Math.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durMathSin = System.nanoTime() - start;
        
        System.out.println("Fast Sin:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durSin = System.nanoTime() - start;

        System.out.println("Fast Cos:");
        start = System.nanoTime();
        for (double index = -2.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durCos = System.nanoTime() - start;
        
        System.out.println("Fast Sin 2:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += FastTrig.sin(index);
        }
        System.out.println(sum);
        sum = 0;
        durSin2 = System.nanoTime() - start;
        
        System.out.println("Math Sin Duration: " + durMathSin / 1000 + " us");
        System.out.println("Fast Sin Duration: " + durSin / 1000 + " us");
        System.out.println("Fast Cos Duration: " + durCos / 1000 + " us");
        System.out.println("Fast Sin Duration 2: " + durSin2 / 1000 + " us");
        
        System.out.println(FastTrig.cos(-30));
        System.out.println(FastTrig.cos(330));
        System.out.println();
    }
}
