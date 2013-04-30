package data_collection;

import orc.Orc;
import orc.QuadratureEncoder;

public class EncoderPair {
    public long dt, prevTime, time;
    public long dLeft, prevLeft, left;
    public long dRight, prevRight, right;


    QuadratureEncoder leftEncoder;
    QuadratureEncoder rightEncoder;
    
    public EncoderPair(Orc orc) {
        leftEncoder = new QuadratureEncoder(orc, 0, false);
        rightEncoder = new QuadratureEncoder(orc, 1, true);
        
        sample();
        sample();
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
    
    public String toString() {
        return "L: " + left + " R: " + right + " DL: " + dLeft + " DR: " + dRight ; 
    }
}
