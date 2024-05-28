package org.warp.midito3d;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.UIScale;
import javax.swing.UIManager;
import org.warp.midito3d.gui.MainWindow;

public class Main {
	
	public static void main(String[] args) {
		if (args.length <= 2) {
			try {
				FlatLightLaf.setup();
		    } catch (Exception e) {
		    	e.printStackTrace();
		       // handle exception
		    }
			new MainWindow().execute();
		} else {
			CommandLineManager.execute(args);
		}
	}
}
