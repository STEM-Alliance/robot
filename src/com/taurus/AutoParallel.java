package com.taurus;

public class AutoParallel implements AutoAction {
    
    AutoAction[] actions;
    boolean[] done;
    
    public AutoParallel(AutoAction... actions)
    {
        this.actions = actions;
        this.done = new boolean[actions.length];
    }

    @Override
    public boolean execute()
    {
        boolean allDone = true;
        
        for (int i = 0; i < this.actions.length; i++)
        {
            if (!this.done[i])
            {
                this.done[i] = this.actions[i].execute();
            }
            
            allDone = allDone && this.done[i];
        }
        
        return allDone;
    }

}
