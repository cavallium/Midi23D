package org.warp.midito3d;

import javax.swing.UIManager;
import org.warp.midito3d.gui.MainWindow;

public class Main {
	
	public static void main(String[] args) {
		if (args.length <= 2) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
