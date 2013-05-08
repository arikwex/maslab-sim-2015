package vision;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import core.Config;
import core.Config.BlockColor;

public class KeyConfigs implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
        private int[] start_drag = new int[2];
        private BlockColor color;
		private int curHueMin;
		private int curHueMax;
		private int curSatMin;
		private int curSatMax;
		private int curValMin;
		private int curValMax;
		private PreferenceChangeListener configListener;
		private Preferences configs;
        
        public KeyConfigs(){
        	super();
			configs = Preferences.userRoot();	
			System.out.println(" configs "+configs);
			configListener = new CameraConfigListener();
			configs.addPreferenceChangeListener(configListener);
			color = BlockColor.RED;
			getValues();
			System.out.println("Hello");
			
        }
		
		
        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        	System.out.println("key event");
            if (e.isControlDown()) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_UP:
                }
            } else {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_1:
                	color = BlockColor.RED;
                	System.out.println("Color changed to "+color); 
                	getValues();
                	break;
                case KeyEvent.VK_2:
                	color = BlockColor.BLUE;
                	System.out.println("Color changed to "+color); 
                	getValues();
                	break;
                case KeyEvent.VK_3:
                	color = BlockColor.GREEN;
                	System.out.println("Color changed to "+color); 
                	getValues();
                	break;
                case KeyEvent.VK_4:
                	color = BlockColor.YELLOW;
                	System.out.println("Color changed to "+color); 
                	getValues();
                	break;
                case KeyEvent.VK_Q:
                	curHueMin -= 1;
                	updateValues();
                	System.out.println("Setting "+color+" hue min to "+curHueMin);
                	break;
                case KeyEvent.VK_W:
                	curHueMin += 1;
                	updateValues();
                	System.out.println("Setting "+color+" hue min to "+curHueMin);
                	break;
                case KeyEvent.VK_E:
                	curHueMax -= 1;
                	updateValues();
                	System.out.println("Setting "+color+" hue max to "+curHueMin);
                	break;
                case KeyEvent.VK_R:
                	curHueMax += 1;
                	updateValues();
                	System.out.println("Setting "+color+" hue max to "+curHueMin);
                	break;
                case KeyEvent.VK_A:
                	curSatMin -= 1; 
                	updateValues();
                	System.out.println("Setting "+color+" sat min to "+curHueMin);
                	break;
                case KeyEvent.VK_S:
                	curSatMin += 1;
                	updateValues();
                	System.out.println("Setting "+color+" sat min to "+curHueMin);
                	break;
                case KeyEvent.VK_D:
                	curSatMax -= 1; 
                	updateValues();
                	System.out.println("Setting "+color+" sat max to "+curHueMin);
                	break;
                case KeyEvent.VK_F:
                	curSatMax += 1;
                	updateValues();
                	System.out.println("Setting "+color+" sat max to "+curHueMin);
                	break;
                case KeyEvent.VK_Z:
                	curValMin -= 1; 
                	updateValues();
                	System.out.println("Setting "+color+" val min to "+curHueMin);
                	break;
                case KeyEvent.VK_X:
                	curValMin += 1;
                	updateValues();
                	System.out.println("Setting "+color+" val min to "+curHueMin);
                	break;
                case KeyEvent.VK_C:
                	curValMax -= 1; 
                	updateValues();
                	System.out.println("Setting "+color+" val max to "+curHueMin);
                	break;
                case KeyEvent.VK_V:
                	curValMax += 1;
                	updateValues();
                	System.out.println("Setting "+color+" val max to "+curHueMin);
                	break;
                }
            }
        }

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyTyped(KeyEvent arg0) {
		}
		
		private void getValues(){
			switch (color){
			case RED:
				curHueMin = configs.getInt("RedHueMin", 180);
				curHueMax = configs.getInt("RedHueMax", 255);
				curSatMin = configs.getInt("RedSatMin", 180);
				curSatMax = configs.getInt("RedSatMax", 255);
				curValMin = configs.getInt("RedValMin", 180);
				curValMax = configs.getInt("RedValMax", 255);
				break;
			case BLUE:
				curHueMin = configs.getInt("BlueHueMin", 0);
				curHueMax = configs.getInt("BlueHueMax", 10);
				curSatMin = configs.getInt("BlueSatMin", 180);
				curSatMax = configs.getInt("BlueSatMax", 255);
				curValMin = configs.getInt("BlueValMin", 180);
				curValMax = configs.getInt("BlueValMax", 255);
				break;
			case GREEN:
				curHueMin = configs.getInt("GreenHueMin", 105);
				curHueMax = configs.getInt("GreenHueMax", 140);
				curSatMin = configs.getInt("GreenSatMin", 180);
				curSatMax = configs.getInt("GreenSatMax", 255);
				curValMin = configs.getInt("GreenValMin", 180);
				curValMax = configs.getInt("GreenValMax", 255);
				break;
			case YELLOW:
				curHueMin = configs.getInt("YellowHueMin", 20);
				curHueMax = configs.getInt("YellowHueMax", 40);
				curSatMin = configs.getInt("YellowSatMin", 180);
				curSatMax = configs.getInt("YellowSatMax", 255);
				curValMin = configs.getInt("YellowValMin", 180);
				curValMax = configs.getInt("YellowValMax", 255);
				break;
			default:
				break;
			}
		}

		
		private void updateValues(){
			System.out.println("for color " +color+" HueMin = "+curHueMin+" HueMax = "+curHueMax+" SatMin = "+curSatMin+" SatMax = "+curSatMax+" ValMin = "+curValMin+" ValMax = "+curValMax);
			curHueMin+= 256;
			curHueMax+= 256;
			curSatMin+= 256;
			curSatMax+= 256;
			curValMin+= 256;
			curValMax+= 256;
			
			switch (color){
			case RED:
				configs.putInt("RedHueMin", curHueMin%256);
				configs.putInt("RedHueMax", curHueMax%256);
				configs.putInt("RedSatMin", curSatMin%256);
				configs.putInt("RedSatMax", curSatMax%256);
				configs.putInt("RedValMin", curValMin%256);
				configs.putInt("RedValMax", curValMax%256);
				break;
			case BLUE:
				configs.putInt("BlueHueMin", curHueMin%256);
				configs.putInt("BlueHueMax", curHueMax%256);
				configs.putInt("BlueSatMin", curSatMin%256);
				configs.putInt("BlueSatMax", curSatMax%256);
				configs.putInt("BlueValMin", curValMin%256);
				configs.putInt("BlueValMax", curValMax%256);
				break;
			case GREEN:
				configs.putInt("GreenHueMin", curHueMin%256);
				configs.putInt("GreenHueMax", curHueMax%256);
				configs.putInt("GreenSatMin", curSatMin%256);
				configs.putInt("GreenSatMax", curSatMax%256);
				configs.putInt("GreenValMin", curValMin%256);
				configs.putInt("GreenValMax", curValMax%256);
				break;
			case YELLOW:
				configs.putInt("YellowHueMin", curHueMin%256);
				configs.putInt("YellowHueMax", curHueMax%256);
				configs.putInt("YellowSatMin", curSatMin%256);
				configs.putInt("YellowSatMax", curSatMax%256);
				configs.putInt("YellowValMin", curValMin%256);
				configs.putInt("YellowValMax", curValMax%256);
				break;
			default:
				System.out.println("no color");
				break;
			}
			getValues();
		}
		class CameraConfigListener implements PreferenceChangeListener{

			@Override
			public void preferenceChange(PreferenceChangeEvent arg0) {
				ObjectPositionDetect.getInstance().updateConfig();
				updateValues();
			}
			
		}
}
