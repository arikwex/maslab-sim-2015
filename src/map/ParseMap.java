package map;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import core.Config;

public class ParseMap {

	public static Map parseFile(String fileName) throws IOException, ParseException{
		Map m = new Map();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
    	
    	parseToken(br,"map");
    	parseToken(br,Config.SECTION_START);	
    	Point bottom_left = null;
    	Point top_right = null;
    	while(true){
    		String token = parseToken(br);
    		if(token.equals(Config.SECTION_END)){
    			break;
    		}
    		else if(token.equals("fiducials")){
    			m.fiducials = parseFiducials(br);
    		}
    		else if(token.equals("construction_objects")){
    			m.setBlocks(parseBlocks(br));
    		}
    		else if(token.equals("obstacles")){
    			m.obstacles = parseObstacles(br);
    		}
    		else if(token.equals("bottom_left")){
    			bottom_left = parsePoint(br);
    		}
    		else if(token.equals("top_right")){
    			top_right = parsePoint(br);
    		} 
    		else if(token.equals("robot_start")){
    			m.robotStart = parsePoint(br);
    			m.bot.pose.x = m.robotStart.x;
    			m.bot.pose.y = m.robotStart.y;
    		}
    		else if(token.equals("robot_goal")){
    			m.robotGoal = parsePoint(br);
    		}
    		else{
    			throw new ParseException("Unknown section token '"+token+"' in file.",0);
    		}
    	}
    	
    	if(bottom_left!=null && top_right!=null){
    		m.worldRect = new Rectangle2D.Double(bottom_left.getX(),
						     bottom_left.getY(),
						     top_right.getX()-bottom_left.getX(),
						     top_right.getY()-bottom_left.getY());
    		m.worldBounds = new ArrayList<Obstacle>();
    		Obstacle left = new Obstacle();
    		left.addVertex(bottom_left);
    		left.addVertex(new Point(bottom_left.x,top_right.y));
    		m.worldBounds.add(left);
    		Obstacle right = new Obstacle();
    		right.addVertex(top_right);
    		right.addVertex(new Point(top_right.x,bottom_left.y));
    		m.worldBounds.add(right);
    		Obstacle bottom = new Obstacle();
    		bottom.addVertex(bottom_left);
    		bottom.addVertex(new Point(top_right.x,bottom_left.y));
    		m.worldBounds.add(bottom);
    		Obstacle top = new Obstacle();
    		top.addVertex(top_right);
    		top.addVertex(new Point(bottom_left.x,top_right.y));
    		m.worldBounds.add(top);
    		
    		m.obstacles.addAll(m.worldBounds);
    	}
    	m.throwAwayBadBlocks();
    	return m;
	}

    /**
     * <p>Parses a token from the file.  Tokens are continuous non-whitespace characters</p>
     * <p>Ignores text that falls after {@link COMMENT} symbol</p>
     * @param br
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private static String parseToken(BufferedReader br) throws IOException, ParseException {
    	String result = "";
    	boolean leadingWhitespace = true;
    	while(true){
    		int c = br.read();
    		String s = Character.toString((char)c);
    		if(s.equals(Config.COMMENT)){
    			br.readLine();
    			continue;
    		}
    		else if(Character.isWhitespace(c)){
    			if(!leadingWhitespace){
    				break;
    			}
    		}
    		else{
    			leadingWhitespace = false;
    			result+=s;
    		}
    	}
    	return result;
    }

    private static String parseToken(BufferedReader br, String expected) throws IOException, ParseException {
    	String token = parseToken(br);
    	if(!token.equals(expected)){
    		throw new ParseException("Expected token '"+expected+"'.  Got '"+token+"' instead.",0);
    	}
    	return token;
    }

    /**
     * <p>Parses the fiducials section of the map file.</p>
     * <p>Expect section to be of format { num_fiducials x 0 {} 1 {} 2 {} ... } where each subsection is a fiducial</p>
     * 
     * @param br BufferedReader, lets us know where we are in the file
     * @return List<Fiducial> list of fiducials in the map file
     * @throws IOException
     * @throws ParseException
     */
    private static Fiducial[] parseFiducials(BufferedReader br) throws IOException, ParseException{
    	
    	parseToken(br,Config.SECTION_START);
    	parseToken(br,"num_fiducials");
    	int numFiducials = parseInt(br);
    	Fiducial[] fiducials = new Fiducial[numFiducials];
    	
    	for(int i=0;i<numFiducials;i++){
    		int index = parseInt(br);
    		if(index<0 || index>=numFiducials){
    			throw new ParseException("Fiducial Index out of range: Expected [0-"+(numFiducials-1)+"].  Got '"+index+"'.",0);
    		}
    		fiducials[index] = parseFiducial(br);
    	}
    	parseToken(br,Config.SECTION_END);

    	return fiducials;
    }
    
    /**
     * <p>Parses a single fiducial from the map file.</p>
     * <p>Expects section to be of format { position{...} top_color{...} bottom_color{...} top_radius x1 bottom_radius x2}</p> 
     * @param br BufferedReader, lets us know where we are in the file
     * @return Fiducial current fiducial we wanted to parse
     * @throws IOException
     * @throws ParseException
     */
    private static Fiducial parseFiducial(BufferedReader br) throws IOException, ParseException{
    	parseToken(br,Config.SECTION_START);
    	Fiducial fiducial = new Fiducial();
    	while(true){
    		String token = parseToken(br);
    		if(token.equals("position")){
    			fiducial.setPosition(parsePoint(br));
    		}
    		else if(token.equals("top_color")){
    			fiducial.setTopColor(parseColor(br));
    		}
    		else if(token.equals("bottom_color")){
    			fiducial.setBottomColor(parseColor(br));
    		}
    		else if(token.equals("top_radius")){
    			fiducial.setTopSize(parseDouble(br));
    		}
    		else if(token.equals("bottom_radius")){
    			fiducial.setBottomSize(parseDouble(br));
    		}
    		else if(token.equals(Config.SECTION_END)){
    			break;
    		}
    		else{
    			throw new ParseException("Unknown token in Fiducial: '"+token+"'",0);
    		}
    	}
    	return fiducial;
    }
    
    /**
     * <p>Parses the MapBlocks section of the map file</p>
     * <p>Expects section to be of format { num_MapBlocks x 0 {} 1 {} 2 {} ...} where each subsection is a block</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return List<MapBlocks> list of MapBlocks in the map file
     * @throws IOException
     * @throws ParseException
     */
    private static ArrayList<MapBlock> parseBlocks(BufferedReader br) throws IOException, ParseException{
    	
    	parseToken(br,Config.SECTION_START);
    	parseToken(br,"num_construction_objects");
    	int numBlocks = parseInt(br);
    	ArrayList<MapBlock> blocks = new ArrayList<MapBlock>(numBlocks); 
    	for (int i=0;i<numBlocks;i++){
    		int index = parseInt(br);
    		if(index<0 || index>=numBlocks){
    			throw new ParseException("MapBlock Index out of range: Expected [0-"+(numBlocks-1)+"].  Got '"+index+"'.",0);
    		}
    		//blocks.set(index,parseBlock(br));
    		blocks.add(parseBlock(br));
    	}
    	parseToken(br,Config.SECTION_END);
    	return blocks;
    }
    
    /**
     * <p>Parses a single MapBlock from the map file</p>
     * <p>Expects section to be of format {position{...} color{...} size x}</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return MapBlock current block we wanted to parse
     * @throws IOException
     * @throws ParseException
     */
    private static MapBlock parseBlock(BufferedReader br) throws IOException, ParseException{
    	
    	parseToken(br,Config.SECTION_START);
    	MapBlock block = new MapBlock() ;
		while(true){
    		String token = parseToken(br);
    		if(token.equals("position")){
    			block.setPoint(parsePoint(br));
    		}
    		else if(token.equals("color")){
    			block.setColor(parseColor(br));
    		}
    		else if(token.equals("size")){
    			block.setSize(parseInt(br));
    		}
    		else if(token.equals(Config.SECTION_END)){
    			break;
    		}
    		else{
    			throw new ParseException("Unknown token in MapBlock: '"+token+"'",0);
    		}
    	}
    	return block;
    }
    
    /**
     * <p>Parses the PolygonObstacle Section of the map file</p>
     * <p>Expects section to be of format { num_obstacles x 0 {} 1 {} 2 {} ...} where each subsection is a PolygonObstacle</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return List<PolygonObstacle> list of obstacles from the map file
     * @throws IOException
     * @throws ParseException
     */
    private static ArrayList<Obstacle> parseObstacles(BufferedReader br) throws IOException, ParseException{
    	parseToken(br,Config.SECTION_START);
    	parseToken(br,"num_obstacles");
    	int numObstacles = parseInt(br);
    	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    	for(int i=0;i<numObstacles;i++){
    		int index = parseInt(br);
    		if(index<0 || index>=numObstacles){
    			throw new ParseException("Obstacle Index out of range: Expected [0-"+(numObstacles-1)+"].  Got '"+index+"'.",0);
    		}
    		//obstacles.set(index,parseObstacle(br));
    		obstacles.add(parseObstacle(br));
    	}
    	return obstacles;
    }
    
    /**
     * <p>Parses a single PolygonObstacle from the map file</p>
     * <p>Expects section to be of format { num_points x {} {} {} ... } where each subsection is a PolygonObstacle and the number of PolygonObstacles matches num_points</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return PolygonObstacle we were trying to parse
     * @throws IOException
     * @throws ParseException
     */
    private static Obstacle parseObstacle(BufferedReader br) throws IOException, ParseException{
    	
    	parseToken(br,Config.SECTION_START);
    	parseToken(br,"num_points");
    	int numPoints = parseInt(br);
    	if(numPoints<3){
    		throw new ParseException("Cannot have fewer than 3 points in obstacles.  This one only has "+numPoints+" points.",0);
    	}
    	
    	Obstacle po = new Obstacle();
    	
    	for (int i=0;i<numPoints;i++){
    		parseInt(br);//Don't need to store the index.
    		Point v = parsePoint(br);
    		po.addVertex(new Point(v.getX(), v.getY()));
    	}
    	parseToken(br,Config.SECTION_END);
    	
    	po.close();
    	return po;
    }
    
    /**
     * <p>Parses length=n vector from the map file</p>
     * <p>Expects vector to be of format { x y z ... } </p>
     * <p>Should use {@link GrandChallengeMap.parsePoint2D} if applicable instead</p>
     * @param br
     * @return double[n] vector we wanted parsed
     * @throws IOException
     * @throws ParseException
     */
    private static double[] parseVectorNd(BufferedReader br, int n) throws IOException, ParseException {
    	if(n<1){
    		throw new ParseException("Tried to parse a Vector of length "+n+".",0);
    	}
    	double[] result = new double[n];
    	parseToken(br,Config.SECTION_START);
    	for(int i=0;i<n;i++){
    		result[i] = parseDouble(br);
    	}
    	parseToken(br,Config.SECTION_END);
    	return result;
    }
    
    /**
     * <p>Parses length=2 vector from the map file</p>
     * <p>Expects vector to be of format { x y } </p>
     * @param br
     * @return double[2] vector we wanted parsed
     * @throws IOException
     * @throws ParseException
     */
    private static Point parsePoint(BufferedReader br) throws IOException, ParseException {
    	double[] vec = parseVectorNd(br,2);
	return new Point(vec[0],vec[1]);
    }

    /**
     * <p>Parses an int from the map file</p>
     * <p> basically just a wrapper around the string->int conversion.</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return the int
     * @throws IOException
     * @throws ParseException
     */
    private static int parseInt(BufferedReader br) throws IOException, ParseException {
    	String token = parseToken(br);
    	try{
    		return Integer.parseInt(token);
    	}catch(NumberFormatException nfe){
    		throw new ParseException("Expected Integer.  Got '"+token+"'.",0);
    	}
    }
    
    
    /**
     * <p>Parses a double from the map file</p>
     * <p> basically just a wrapper around the string->double conversion.</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return the double
     * @throws IOException
     * @throws ParseException
     */
    private static double parseDouble(BufferedReader br) throws IOException, ParseException {
    	String token = parseToken(br);
    	
    	try{
    		return Double.parseDouble(token);
    	}catch(NumberFormatException nfe){
    		throw new ParseException("Expected Double.  Got '"+token+"'.",0);
    	}
    }
    
    /**
     * <p>Parses a color-string from the map file</p>
     * @param br BufferedReader, lets us know where we are in the file
     * @return the corresponding color
     * @throws IOException
     * @throws ParseException
     */
    private static Color parseColor(BufferedReader br) throws IOException, ParseException {

	String token = parseToken(br).toLowerCase();

	if(token.equals("red")){
	    return Color.RED;
	}
	else if(token.equals("blue")){
	    return Color.BLUE;
	}
	else if(token.equals("yellow")){
	    return Color.YELLOW;
	}
	else if(token.equals("orange")){
	    return Color.ORANGE;
	}
	else if(token.equals("green")){
	    return Color.GREEN;
	}
    return Color.BLACK;
    }
    
}
