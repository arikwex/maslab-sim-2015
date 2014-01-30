package vision;

import java.util.ArrayList;

// TODO: Replace this Vision singleton class with actual vision code
public class Vision implements VisionInterface {

    private static Vision instance;
    
    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }
        return instance;
    }
    
    @Override
    public ArrayList<Ball> getBalls() {
        // TODO Auto-generated method stub
        return null;
    }
}
