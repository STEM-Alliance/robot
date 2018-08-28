package org.wfrobotics.robot.auto;

import java.util.regex.Pattern;

import edu.wpi.first.wpilibj.DriverStation;

public class MatchState2018
{
    public enum Side {
        Left, Right, Unknown
    };

    private static MatchState2018 instance = null;

    public Side SwitchNear = Side.Unknown;
    public Side Scale = Side.Unknown;
    public Side SwitchFar = Side.Unknown;

    private MatchState2018()
    {
        update();
    }

    public static MatchState2018 getInstance()
    {
        if (instance == null)
        {
            instance = new MatchState2018();
        }
        return instance;
    }

    public synchronized boolean update()
    {
        String gameData;

        // get the data from the driver station
        gameData = DriverStation.getInstance().getGameSpecificMessage();

        // if the string isn't empty, look for a match of
        // first three characters can be L or R; ie "LRR", "RLR", etc
        if (gameData != null && !gameData.isEmpty() && Pattern.compile("^([LR]{3})").matcher(gameData).find())
        {
            // order is near switch, scale, far switch
            SwitchNear = (gameData.charAt(0) == 'L') ? Side.Left : Side.Right;
            Scale = (gameData.charAt(1) == 'L') ? Side.Left : Side.Right;
            SwitchFar = (gameData.charAt(2) == 'L') ? Side.Left : Side.Right;
            return true;
        }
        // reset the info since we didn't get valid data
        SwitchNear = Side.Unknown;
        Scale = Side.Unknown;
        SwitchFar = Side.Unknown;
        return false;
    }
}
