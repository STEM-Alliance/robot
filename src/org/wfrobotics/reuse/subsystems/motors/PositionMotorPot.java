package org.wfrobotics.reuse.subsystems.motors;

public class PositionMotorPot extends PositionMotor
{
    private PositionMotorPot(int address) 
    {
        super(address); 
    }

    public double get()
    {
//        return motor.get();
        return 0;
    }
    
    public static class Builder extends PositionMotor.Builder<PositionMotorPot, Builder>
    {
        public Builder(int address) { super(address); }

        @Override
        protected PositionMotorPot createMotor(int address)
        {
            return new PositionMotorPot(address);
        }

        @Override
        protected Builder thisBuilder()
        {
            return this;
        }
    }
}