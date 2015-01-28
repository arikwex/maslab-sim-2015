package hardware.components;

public interface Servo {
	public void setValue(double v);
	public double getValue();
	public double getTransientValue();
}
