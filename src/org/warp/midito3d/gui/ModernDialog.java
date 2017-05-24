package org.warp.midito3d.gui;

import java.io.File;

import javax.swing.JFileChooser;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import com.sun.javafx.application.PlatformImpl;

public class ModernDialog {
	private JFileChooser oldChooser;
	private FileChooser newChooser;
	public static boolean initialized = false;
	public static boolean old = false;
	
	public ModernDialog() {
		if (!initialized) {
			try {
				PlatformImpl.startup(()->{});
			} catch (java.lang.RuntimeException ex) {
				old = true;
			}
		}
		
	}
	
	public static void runLater(Runnable r) {
		
	}
	
	public void setTitle(String text) {
		if (old) {
			
		} else {
			newChooser.
		}
	}
	
	public void setExtensions(ModernExtensionFilter... filters) {
		
	}
	
	public File show() {
		return null;
	}
	
	public File showSaveDialog() {
		return null;
	}
	
	public static class ModernExtensionFilter {
		public String[] filters;
		public String name;
		
		public ModernExtensionFilter(String name, String... filters){
			this.filters = filters;
			this.name = name;
		}
	}
}
