package hardware.components;

public interface Encoder extends SampleableDevice {
	public double getDeltaDistance();
	public double getVelocity();
}
