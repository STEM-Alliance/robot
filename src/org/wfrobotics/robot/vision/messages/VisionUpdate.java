package org.wfrobotics.robot.vision.messages;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.vision.util.TargetInfo;
import org.wfrobotics.robot.vision.util.VisionMessage;

/** Describes vision stuff (ex: rectangles) the camera is detecting **/
public class VisionUpdate extends VisionMessage
{
    public final int mode;
    public final TargetInfo target;

    private VisionUpdate(int mode, TargetInfo t)
    {
        this.mode = mode;
        target = t;
    }

    public static VisionUpdate fromMessage(String s)
    {
        String[] vals = s.split(",");
        if (s.length() < 6)
        {
            new HerdLogger(TargetInfo.class).error("Vision", "Malformed message");
        }
        else if (vals[0] != sGetType())
        {
            new HerdLogger(TargetInfo.class).error("Vision", "Not a " + VisionUpdate.class.toString());
        }

        int coprocessorMode = Integer.valueOf(vals[1]);

        // TODO do a for loop, assuming remainder of msg are targets
        // TODO make an arrayList of targets if we need to support > 1
        double x = Double.valueOf(vals[2]);
        double y = Double.valueOf(vals[3]);
        double w = Double.valueOf(vals[4]);
        double h = Double.valueOf(vals[5]);
        TargetInfo t = new TargetInfo(x, y, w, h);

        return new VisionUpdate(coprocessorMode, t);
    }

    public String getType()
    {
        return sGetType();
    }

    public String getMessage()
    {
        return target.toString();
    }

    public static String sGetType()
    {
        return "VisionUpdate";
    }
}
