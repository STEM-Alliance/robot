package org.wfrobotics.reuse.utilities;

import java.util.logging.Level;

public class Tests
{
    static final double width = 4;
    static final double depth = 3;
    static final double robotS = .75;

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
        FastTrig.cos(359);  // FastTrig init cache
        debugFastTrig();
        debugHerdVector();
        debugNewChassisToWheelVectors();
        debugWheelVectorsToChassis();
        debugLogging();
    }

    public static void debugHerdVector()
    {
        // New Vector: Trig
        HerdVector a = new HerdVector(1, 60);
        long start;

        start = System.nanoTime();
        System.out.println("A.X: " + a.getX());
        System.out.println("A.Y: " + a.getY());
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
    }

    public static void debugNewChassisToWheelVectors()
    {
        double offsetX = width;
        double offsetY = depth;

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

        HerdVector v = new HerdVector(4, 60);
        //        double spin = robotS;
        HerdVector w = new  HerdVector(robotS, 90);
        long start = System.nanoTime();

        // v + w x r
        HerdVector wheelBR = v.add(w.cross(rBR));
        HerdVector wheelFR = v.add(w.cross(rFR));
        HerdVector wheelFL = v.add(w.cross(rFL));
        HerdVector wheelBL = v.add(w.cross(rBL));

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
            Tests.maxWheelMagnitudeLast = maxMag;
        }
        else
        {
            Tests.maxWheelMagnitudeLast = 1;
        }

        // Mirroring Y is purely to match our old swerve. Seems like an extra step beyond what math says we need
        wheelBR = new HerdVector(wheelBR.getMag(), -wheelBR.getAngle());
        wheelFR = new HerdVector(wheelFR.getMag(), -wheelFR.getAngle());
        wheelFL = new HerdVector(wheelFL.getMag(), -wheelFL.getAngle());
        wheelBL = new HerdVector(wheelBL.getMag(), -wheelBL.getAngle());

        double end = System.nanoTime();

        System.out.println("Mirrored BR: " + wheelBR);
        System.out.println("Mirrored FR: " + wheelFR);
        System.out.println("Mirrored FL: " + wheelFL);
        System.out.println("Mirrored BL: " + wheelBL);
        System.out.println("Wheel Calcs: " + ((end - start)/1000) + " ns");
        System.out.println("-------------------------------");

        Tests.wheelBR = wheelBR;
        Tests.wheelFR = wheelFR;
        Tests.wheelFL = wheelFL;
        Tests.wheelBL = wheelBL;
        Tests.rBR = rBR;
        Tests.rFR = rFR;
        Tests.rFL = rFL;
        Tests.rBL = rBL;
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
        System.out.println("-------------------------------");
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
        System.out.println("-------------------------------");
    }
}
