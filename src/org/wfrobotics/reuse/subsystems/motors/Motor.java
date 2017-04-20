package org.wfrobotics.reuse.subsystems.motors;

//import com.ctre.CANTalon;

// Generics let the builder superclass accessors return this() without knowledge of who subclassed it
// See http://egalluzzo.blogspot.com/2010/06/using-inheritance-with-fluent.html
public abstract class Motor
{
//    protected final CANTalon motor;
    protected int address;  // TODO swap for talon
    protected double tolerance;
    
    protected Motor(int address)
    {
//        motor = new CANTalon(address);  // TODO swap for talon
        this.address = address;
    }
    
    protected abstract double get();
    
    public String toString()
    {
//        return motor.getAddress();  // TODO swap for talon
        return "Motor #" + address;
    }
    
    // T is the type extending this motor. B is the type extending this builder (of those same types)
    public abstract static class Builder<T extends Motor, B extends Builder<T, B>>
    {        
        // Concrete child access
        protected T motor;
        protected B thisBuilder;
        protected abstract T createMotor(int address);
        protected abstract B thisBuilder();
        
        public Builder(int address)
        {
            motor = createMotor(address);
            thisBuilder = thisBuilder();
        }
        
        public B tolerance(double val) { motor.tolerance = val; return thisBuilder; }
        public T build() { return motor; }
    }
}