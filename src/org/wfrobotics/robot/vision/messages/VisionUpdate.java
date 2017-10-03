package org.wfrobotics.robot.vision.messages;

import java.util.ArrayList;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.vision.util.TargetInfo;
import org.wfrobotics.robot.vision.util.VisionMessage;

/** Describes vision stuff (ex: rectangles) the camera is detecting **/
public class VisionUpdate extends VisionMessage
{
    public final int mode;
    public final ArrayList<TargetInfo> targets;

    private VisionUpdate(int mode, ArrayList<TargetInfo> targets)
    {
        this.mode = mode;
        this.targets = targets;
    }

    public static VisionUpdate fromMessage(String s)
    {
        String[] vals = s.split(",");
        int coprocessorMode;
        ArrayList<TargetInfo> targets = new ArrayList<TargetInfo>();

        if (s.length() < 2)
        {
            new HerdLogger(TargetInfo.class).error("Vision", "Malformed message");
        }
        else if (vals[0] != sGetType())
        {
            new HerdLogger(TargetInfo.class).error("Vision", "Not a " + VisionUpdate.class.toString());
        }

        coprocessorMode = Integer.valueOf(vals[1]);

        for (int index = 2; index < s.length() - 3; index += 4)
        {
            double x = Double.valueOf(vals[index]);
            double y = Double.valueOf(vals[index + 1]);
            double w = Double.valueOf(vals[index + 2]);
            double h = Double.valueOf(vals[index + 3]);
            targets.add(new TargetInfo(x, y, w, h));
        }

        return new VisionUpdate(coprocessorMode, targets);
    }

    public String getType()
    {
        return sGetType();
    }

    public String getMessage()
    {
        return targets.toString();
    }

    public static String sGetType()
    {
        return "VisionUpdate";
    }
}
