package org.wfrobotics.reuse.subsystems.motors;

public abstract class PositionMotor extends Motor
{
    protected double[] positions;
    
    public PositionMotor(int address) 
    {
        super(address); 
    }
    
    // Type T extends this motor, type B extends this builder. This builder extends the base builder (of those same types)
    public static abstract class Builder<T extends PositionMotor, B extends Builder<T, B>> extends Motor.Builder<T, B>
    {
        public Builder(int address) { super(address); }

        public B positions(double[] val) { motor.positions = val; return thisBuilder; }
    }
}
