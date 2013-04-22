package robot

import orc.Orc;
import orc.QuadratureEncoder;

public class EncoderPair {
    public long dt, prevTime, time;
    public long deltaLeft, prevLeft, left;
    public long deltaRight, prevRight, right;


    QuadratureEncoder leftEncoder;
    QuadratureEncoder rightEncoder;
    
    public EncoderPair(OrcController orc) {
        leftEncoder = new QuadratureEncoder(orc, 0, false);
        rightEncoder = new QuadratureEncoder(orc, 1, true);
    }
    
    public void sample() {
      prevTime = time;
      prevLeft = left;
      prevRight = right;

      time = System.currentTimeMillis();
      right = right.getPosition();
      left = left.getPosition();

      dt = time - prevTime;
      deltaLeft = left-prevLeft;
      deltaRight = right-prevRight;
    }
}
