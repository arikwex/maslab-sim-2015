package core;

import orc.Orc;
import orc.QuadratureEncoder;

public class EncoderPair extends Thread {
    public long dt, prevTime, time;
    public long dLeft, prevLeft, left;
    public long dRight, prevRight, right;


    QuadratureEncoder leftEncoder;
    QuadratureEncoder rightEncoder;
	boolean ready;
    
    public EncoderPair(Orc orc) {
        leftEncoder = new QuadratureEncoder(orc, 0, false);
        rightEncoder = new QuadratureEncoder(orc, 1, true);
    }
    
    public void sample() {
      prevTime = time;
      prevLeft = left;
      prevRight = right;

      time = System.currentTimeMillis();
      right = rightEncoder.getPosition();
      left = leftEncoder.getPosition();

      dt = time - prevTime;
      dLeft = left-prevLeft;
      dRight = right-prevRight;
    }
    
    public void run(){
    	while (true){
    		sample();
    		ready = true;
    	}
    }
}
