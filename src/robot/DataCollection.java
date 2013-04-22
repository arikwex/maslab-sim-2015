package robot

public class DataCollection {
    private OrcController orc;

    public EncoderPair encoders;
    public ArrayList<Sonar> sonars;
    public Vision vision;
    public Delta delta;
    
    
    public DataCollection(OrcController orc) {
        this.orc = orc;    

        encoders = new EncoderPair();
        //sonars = new ArrayList<Sonar>;
        //vision = new Vision();
        //delta = new Delta();
    }
    
    public void step() {
        encoders.sample();
    }
}
