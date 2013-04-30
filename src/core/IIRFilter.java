package core;

import java.util.LinkedList;

public class IIRFilter{
	LinkedList<Double> inputQueue;
	LinkedList<Double> outputQueue;
	
	double[] inputCoef;
	double[] outputCoef;
	
	double output;
	
	public IIRFilter(double[]  inputCoef, double[] outputCoef) {
		this.inputCoef = inputCoef;
		this.outputCoef = outputCoef;
		
		inputQueue = new LinkedList<Double>();
		outputQueue = new LinkedList<Double>();
	}

	public synchronized double update(double val) {
		inputQueue.addLast(val);
		if (inputQueue.size() > inputCoef.length)
			inputQueue.pop();
		
		outputQueue.addLast(output);
		if (outputQueue.size() > outputCoef.length)
			outputQueue.pop();
		
		output = 0;
		for(int i = 0; i < inputQueue.size(); i++)
			output += inputCoef[i] * inputQueue.get(i);
		for(int i = 0; i < outputQueue.size(); i++)
			output += outputCoef[i] * outputQueue.get(i);
		
		
		return output;
	}

	public double getVal() {
		return output;
	}
	
	public static void main(String[] args) {
		//IIRFilter iir = new IIRFilter(new double[]{.25, .25, .25, .25}, new double[]{0});
		IIRFilter iir = new IIRFilter(new double[]{.15, .15, .15}, new double[]{.55});
		for(int i = 0; i< 10; i++)
			System.out.println(iir.update(100.0));
		System.out.println(iir.update(0.0));
		for(int i = 0; i< 10; i++)
			System.out.println(iir.update(100.0));


	}
}
