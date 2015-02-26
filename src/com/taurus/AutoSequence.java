package com.taurus;

public class AutoSequence implements AutoAction {
    
    AutoAction[] actions;
    int state = 0;
    
    public AutoSequence(AutoAction... actions)
    {
        this.actions = actions;
    }

    @Override
    public boolean execute()
    {
        if (state < actions.length)
        {
            if (actions[state].execute())
            {
                state++;
            }
        }
        
        return state >= actions.length;
    }

}
