package org.wfrobotics.reuse.utilities;

import java.util.logging.Level;

public class Tests
{
    static final double width = 4;
    static final double depth = 3;
    static Vector robotV = Vector.NewFromMagAngle(1, 5);
    static final double robotS = -.1;

    static double maxWheelMagnitudeLast;
    static HerdVector wheelBR;
    static HerdVector wheelFR;
    static HerdVector wheelFL;
    static HerdVector wheelBL;
    static HerdVector rBR;
    static HerdVector rFR;
    static HerdVector rFL;
    static HerdVector rBL;

    public static void main(String[] args)
    {
        //FastTrig.cos(359);  // FastTrig init cache
        //printDivider();
        //debugFastTrig();
        //printDivider();
        //debugHerdVector();
        //printDivider();
        debugOldChassisToWheelVectors();
        printDivider();
        debugNewChassisToWheelVectors();
        printDivider();
        //debugWheelVectorsToChassis();
        //printDivider();
        try
        {
            debugLogging();
            printDivider();
        }
        catch (UnsatisfiedLinkError e)
        {
            // Not on Robot, change HerdLogger handler to target System.out
        }
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
        System.out.println("A.Y commanded: " + a.getY());
        System.out.println("Gyro: " + gyro);
        a = a.rotate(-gyro);
        System.out.println("A rotate to field relative: " + a);
        System.out.println("A.X rotate to field relative: " + a.getX());
        a = a.rotate(b);
        System.out.println("A rotate by B: " + a);
        a = a.rotate(-136);
        System.out.println("A rotate by -136: " + a);
        System.out.println();

        b = new HerdVector(1, -90);
        System.out.println("B.X commanded: " + b.getX());
        System.out.println("B.Y commanded: " + b.getY());
        System.out.println();

        // New Vector: Zero
        b = new HerdVector(0, 180);
        System.out.println("(0, 0): " + b);
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

        // Cross
        a = new HerdVector(1, 45);
        b = new HerdVector(1, -45);

        System.out.format(a + " cross " + b + " = " + a.cross(b) + "\n");
        b = b.rotate(180);
        System.out.format(a + " cross " + b + " = " + a.cross(b) + "\n");
        System.out.println();

        // Clamp
        HerdVector c = new HerdVector(.44, 10);
        double maxDelta = .05;
        HerdVector last = new HerdVector(.5, 10);
        double min = last.getMag() - maxDelta;
        double max = last.getMag() + maxDelta;

        System.out.println(min);
        System.out.println(max);
        System.out.println(c.clampToRange(min, max));
    }

    public static void debugNewChassisToWheelVectors()
    {
        double start, end;
        double spin = robotS;
        HerdVector v = new HerdVector(robotV.getMag(), robotV.getAngle() + 90);
        HerdVector w = new  HerdVector(spin, 90);
        System.out.format("Command (%.2f, %.2f, %.2f)\n\n", v.getMag(), v.getAngle(), spin);

        HerdVector rWidth = new HerdVector(width, 90);
        HerdVector rHeight = new HerdVector(depth, 0);

        rFR = rWidth.add(rHeight);
        rBR = rWidth.sub(rHeight);
        rFL = rWidth.rotate(180).add(rHeight);
        rBL = rWidth.rotate(180).sub(rHeight);

        double unitVectorCorrection = 1 / rBR.getMag();
        rFR = rFR.scale(unitVectorCorrection);
        rFL = rFL.scale(unitVectorCorrection);
        rBR = rBR.scale(unitVectorCorrection);
        rBL = rBL.scale(unitVectorCorrection);

        System.out.println("FR: " + rFR);
        System.out.println("FL: " + rFL);
        System.out.println("BR: " + rBR);
        System.out.println("BL: " + rBL);
        System.out.println();

        //        System.out.println("Cross FR: " + w.cross(rFR));
        //        System.out.println("Cross FL: " + w.cross(rFL));
        //        System.out.println("Cross BR: " + w.cross(rBR));
        //        System.out.println("Cross BL: " + w.cross(rBL));
        //        System.out.println();

        start = System.nanoTime();

        // v + w x r
        HerdVector wheelFR = v.add(w.cross(rFR));
        HerdVector wheelFL = v.add(w.cross(rFL));
        HerdVector wheelBR = v.add(w.cross(rBR));
        HerdVector wheelBL = v.add(w.cross(rBL));

        double maxMag = wheelFR.getMag();
        maxMag = (wheelFL.getMag() > maxMag) ? wheelFL.getMag(): maxMag;
        maxMag = (wheelBR.getMag() > maxMag) ? wheelBR.getMag(): maxMag;
        maxMag = (wheelBL.getMag() > maxMag) ? wheelBL.getMag(): maxMag;

        if (maxMag > 1)
        {
            wheelFR = wheelFR.scale(1 / maxMag);
            wheelFL = wheelFL.scale(1 / maxMag);
            wheelBR = wheelBR.scale(1 / maxMag);
            wheelBL = wheelBL.scale(1 / maxMag);
            Tests.maxWheelMagnitudeLast = maxMag;
        }
        else
        {
            Tests.maxWheelMagnitudeLast = 1;
        }

        end = System.nanoTime();

        System.out.println("FR: " + wheelFR);
        System.out.println("FL: " + wheelFL);
        System.out.println("BR: " + wheelBR);
        System.out.println("BL: " + wheelBL);
        System.out.println();

        double twist = -90;

        HerdVector oldFR = wheelFR.rotate(twist);
        HerdVector oldFL = wheelFL.rotate(twist);
        HerdVector oldBR = wheelBR.rotate(twist);
        HerdVector oldBL = wheelBL.rotate(twist);

        double mirrorY = -2;

        oldFR = oldFR.rotate(oldFR.getAngle() * mirrorY);
        oldFL = oldFL.rotate(oldFL.getAngle() * mirrorY);
        oldBR = oldBR.rotate(oldBR.getAngle() * mirrorY);
        oldBL = oldBL.rotate(oldBL.getAngle() * mirrorY);

        System.out.format("Translate (Twist %.0f, Mirror Y)\n", twist);
        System.out.println("FR old: " + oldFR);
        System.out.println("FL old: " + oldFL);
        System.out.println("BR old: " + oldBR);
        System.out.println("BL old: " + oldBL);
        System.out.println();

        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
        System.out.format("Wheel Mag wanted: %.2f\n", Tests.maxWheelMagnitudeLast);

        Tests.wheelFR = wheelFR;
        Tests.wheelFL = wheelFL;
        Tests.wheelBR = wheelBR;
        Tests.wheelBL = wheelBL;
    }

    public static void debugWheelVectorsToChassis()
    {
        double maxMag = maxWheelMagnitudeLast;
        double start = System.nanoTime();

        // Undo Mirror
        HerdVector reconstructWheelBR = new HerdVector(wheelBR.getMag(), -wheelBR.getAngle());
        HerdVector reconstructWheelFR = new HerdVector(wheelFR.getMag(), -wheelFR.getAngle());
        HerdVector reconstructWheelFL = new HerdVector(wheelFL.getMag(), -wheelFL.getAngle());
        HerdVector reconstructWheelBL = new HerdVector(wheelBL.getMag(), -wheelBL.getAngle());

        //        System.out.println("UnMirrored BR: " + reconstructWheelBR);
        //        System.out.println("UnMirrored FR: " + reconstructWheelFR);
        //        System.out.println("UnMirrored FL: " + reconstructWheelFL);
        //        System.out.println("UnMirrored BL: " + reconstructWheelBL);

        // Undo scale
        reconstructWheelBR = reconstructWheelBR.scale(maxMag);
        reconstructWheelFR = reconstructWheelFR.scale(maxMag);
        reconstructWheelFL = reconstructWheelFL.scale(maxMag);
        reconstructWheelBL = reconstructWheelBL.scale(maxMag);

        // v + w x r
        //        System.out.println("Unscaled BR: " + reconstructWheelBR);
        //        System.out.println("Unscaled FR: " + reconstructWheelFR);
        //        System.out.println("Unscaled FL: " + reconstructWheelFL);
        //        System.out.println("Unscaled BL: " + reconstructWheelBL);

        // Summing the wheel vectors, the w x r's components cancel, leaving the chassis command scaled by four
        HerdVector frankenstein = new HerdVector(reconstructWheelBR);
        frankenstein = frankenstein.add(reconstructWheelFR);
        frankenstein = frankenstein.add(reconstructWheelFL);
        frankenstein = frankenstein.add(reconstructWheelBL);
        frankenstein = frankenstein.scale(.25);

        double end = System.nanoTime();

        //        // w x r
        //        HerdVector reconstructWxBR = reconstructWheelBR.sub(v);
        //        HerdVector reconstructWxFR = reconstructWheelFR.sub(v);
        //        HerdVector reconstructWxFL = reconstructWheelFL.sub(v);
        //        HerdVector reconstructWxBL = reconstructWheelBL.sub(v);
        //
        //        System.out.println("Reconstruct BR: " + reconstructWxBR);
        //        System.out.println("Reconstruct FR: " + reconstructWxFR);
        //        System.out.println("Reconstruct FL: " + reconstructWxFL);
        //        System.out.println("Reconstruct BL: " + reconstructWxBL);

        HerdVector wXr = reconstructWheelBR.scale(maxMag);
        wXr = reconstructWheelBR.sub(frankenstein);

        System.out.println("Reconstructing Robot Command");
        System.out.format("Robot Command (%.2f, %.2f, %.2f)\n", frankenstein.getMag(), frankenstein.getAngle(), wXr.getMag());
        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
    }

    public static void debugOldChassisToWheelVectors()
    {
        double start, end;
        // Cartesian Wheel Vectors
        double CHASSIS_WIDTH = width;
        double CHASSIS_DEPTH = depth;
        double CHASSIS_SCALE = Math.sqrt(CHASSIS_WIDTH * CHASSIS_WIDTH + CHASSIS_DEPTH * CHASSIS_DEPTH);

        double[][] POSITIONS = {
                { CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back right
                { -CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back left
                { CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE },  // front right
                { -CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }}; // front left
        String[] names = {"FR","FL", "BR","BL"};
        Vector[] positions = new Vector[4];
        Vector velocity = Vector.NewFromMagAngle(robotV.getMag(), robotV.getAngle());  // Positive x-axis
        double spin = robotS;

        System.out.format("Width: %.2f, Depth: %.2f\n", CHASSIS_WIDTH, CHASSIS_DEPTH);
        System.out.format("Command (%.2f, %.2f, %.2f)\n\n", velocity.getMag(), velocity.getAngle(), spin);

        for (int index = 0; index < 4; index++)
        {
            positions[index] = new Vector(POSITIONS[index]);
            System.out.format("%s: (%.2f, %.2f)\n", names[index], positions[index].getMag(), positions[index].getAngle());
        }
        System.out.println();

        start = System.nanoTime();
        Vector[] scaled = oldScaleWheelVectors(velocity, spin, positions);

        end = System.nanoTime();
        for (int index = 0; index < scaled.length; index++)
        {
            System.out.format("%s: (%.2f, %.2f)\n", names[index], scaled[index].getMag(), scaled[index].getAngle());
        }
        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
    }

    private static Vector[] oldScaleWheelVectors(Vector velocity, double spin, Vector[] positions)
    {
        Vector[] WheelsUnscaled = new Vector[4];
        Vector[] WheelsScaled = new Vector[4];
        double MaxWantedVeloc = 0;
        double VelocityRatio;

        for (int i = 0; i < 4; i++)
        {
            WheelsUnscaled[i] = new Vector(velocity.getX() - spin * positions[i].getY(),
                    -(velocity.getY() + spin * positions[i].getX()));

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        VelocityRatio = oldGetVelocityLimit(MaxWantedVeloc);

        for (int i = 0; i < 4; i++)
        {
            WheelsScaled[i] = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());
        }

        return WheelsScaled;
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
    }

    public static void debugLogging()
    {
        double start;
        double end;
        HerdVector v = new HerdVector(1, 45);

        HerdLogger log = new HerdLogger("WrapperTest");
        log.setLevel(Level.INFO);
        HerdLogger log2 = new HerdLogger(Tests.class);
        log2.setLevel(Level.WARNING);
        HerdLogger log3 = new HerdLogger(Tests.class);

        start = System.nanoTime();
        log.debug("test2", 1);
        log.info("test:", .33333);
        log.debug("test3: ", v);
        log.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        start = System.nanoTime();
        log2.debug("test2", 1);
        log2.info("test:", .33333);
        log2.debug("test3: ", v);
        log2.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");

        start = System.nanoTime();
        log3.debug("test2", 1);
        log3.info("test:", .33333);
        log3.debug("test3: ", v);
        log3.warning("test3: ", v);
        end = System.nanoTime();
        System.out.println("Test Duration: " + ((end - start)/1000) + " ns");
    }

    public static void printDivider()
    {
        System.out.println("-------------------------------\n");
        System.out.println("-------------------------------");
    }

    private static class Vector
    {
        private double mag;
        private double ang;

        public Vector()
        {
            mag = 0;
            ang = 0;
        }

        public Vector(double[] position)
        {
            setXY(position[0], position[1]);
        }

        public Vector(double x, double y)
        {
            setXY(x, y);
        }

        public static Vector NewFromMagAngle(double mag, double ang)
        {
            Vector r = new Vector();
            r.mag = mag;
            r.ang = ang;
            return r;
        }

        public void setXY(double x, double y)
        {
            mag = Math.sqrt(x * x + y * y);
            ang = Math.toDegrees(Math.atan2(y, x));
        }

        public double getX()
        {
            double realAngle = Math.toRadians(wrapToRange(ang, 0, 360));
            return Math.cos(realAngle) * mag;
        }

        public double getY()
        {
            double realAngle = Math.toRadians(wrapToRange(ang, 0, 360));
            return Math.sin(realAngle) * mag;
        }

        public double getMag()
        {
            return mag;
        }

        public double getAngle()
        {
            return ang;
        }

        public Vector clone()
        {
            return Vector.NewFromMagAngle(mag, ang);
        }

        public static final double wrapToRange(double value, double min, double max)
        {
            return wrapToRange(value - min, max - min) + min;
        }

        public static final double wrapToRange(double value, double max)
        {
            return ((value % max) + max) % max;
        }
    }
}
