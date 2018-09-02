package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.config.MatchState2018.Side;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SwitchChoice extends ConditionalCommand
{
    private final MatchState2018 state = MatchState2018.getInstance();
    private final Side primarySide;

    /** Do command if our's is on the right, otherwise skip this command */
    public SwitchChoice(Side primarySide, Command onPrimarySide)
    {
        super(onPrimarySide);
        this.primarySide = primarySide;
    }

    /** Do first command if our's is on the right, otherwise do the second command */
    public SwitchChoice(Side primarySide, Command onPrimarySide, Command onSecondarySide)
    {
        super(onPrimarySide, onSecondarySide);
        this.primarySide = primarySide;
    }

    protected boolean condition()
    {
        return state.SwitchNear == primarySide;
    }
}
