package robot

public class StateEstimator {
    private DataCollection dc;

    Map worldMap;
    Map localMap;
    
    double botX;
    double botY;
    double botTheta;
    
    public StateEstimator(DataCollection dc) {
        this.dc = dc;    
    }
    
    public void step() {
        computePose();        
    }
    
    public void computePose() {
        double dl = dc.encoders.dl * METERS_PER_TICK;
        double dr = dc.encoders.dr * METERS_PER_TICK;

        if (dr == 0 && dl == 0) return; // we haven't moved at all
        
        double dTheta = (dl - dr)/WHEELBASE;

        botTheta += theta;
        botX += (dl+dr)*Math.cos(botTheta)/2.0;
        botY += (dl+dr)*Math.sin(botTtheta)/2.0;
    }
}
