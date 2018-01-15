package org.wfrobotics.robot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wpi.first.wpilibj.DriverStation;

public class MatchState {

    public enum Side {
        Left, Right, Unknown
    };

    public Side SwitchNear = Side.Unknown;
    public Side Scale = Side.Unknown;
    public Side SwitchFar = Side.Unknown;

    private MatchState()
    {
        update();
    }

    public static MatchState getInstance()
    {
        return new MatchState();
    }

    public boolean update()
    {
        String gameData;

        // get the data from the driver station
        gameData = DriverStation.getInstance().getGameSpecificMessage();

        // if the string isn't empty, look for a match of
        // first three characters can be L or R; ie "LRR", "RLR", etc
        if (gameData != null && 
                !gameData.isEmpty() &&
                Pattern.compile("^([LR]{3})").matcher(gameData).find())
        {
            // order is near switch, scale, far switch
            SwitchNear = (gameData.charAt(0) == 'L') ? Side.Left : Side.Right;
            Scale = (gameData.charAt(1) == 'L') ? Side.Left : Side.Right;
            SwitchFar = (gameData.charAt(2) == 'L') ? Side.Left : Side.Right;
            return true;
        }
        else
        {
            // reset the info since we didn't get valid data
            SwitchNear = Side.Unknown;
            Scale = Side.Unknown;
            SwitchFar = Side.Unknown;
            return false;
        }
    }
}
