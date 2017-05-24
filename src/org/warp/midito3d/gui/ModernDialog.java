package org.warp.midito3d.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

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
	
	static {
		if (!initialized) {
			try {
				PlatformImpl.startup(()->{});
				System.out.println("init");
			} catch (java.lang.RuntimeException ex) {
				old = true;
			}
			initialized = true;
		}
	}
	
	public ModernDialog() {
		if (old) {
			oldChooser = new JFileChooser();
		} else {
			newChooser = new FileChooser();
		}
	}
	
	public static void runLater(Runnable r) {
		if (old) {
			SwingUtilities.invokeLater(r);
		} else {
			Platform.runLater(r);
		}
	}
	
	public void setTitle(String text) {
		if (old) {
			oldChooser.setDialogTitle(text);
		} else {
			newChooser.setTitle(text);
		}
	}
	
	public void setExtensions(final ModernExtensionFilter... filters) {
		if (old) {
			oldChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return filters[0].name;
				}
				
				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return true;
					} else {
						String path = file.getAbsolutePath().toLowerCase();
						for (int j = 0; j < filters.length; j++) {
							for (int i = 0, n = filters[j].filters.length; i < n; i++) {
								String extension = filters[j].filters[i].substring(2);
								if ((path.endsWith(extension) && (path.charAt(path.length() 
										- extension.length() - 1)) == '.')) {
									return true;
								}
							}
						}
					}
					return false;
				}
			});
		} else {
			ExtensionFilter[] newFilters = new ExtensionFilter[filters.length];
			int i = 0;
			for (ModernExtensionFilter f : filters) {
				newFilters[i] = new ExtensionFilter(f.name, f.filters);
				i++;
			}
			newChooser.getExtensionFilters().setAll(newFilters);
		}
	}
	
	public File show(JFrame parent) {
		if (old) {
			if (oldChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				return oldChooser.getSelectedFile();
			} else {
				return null;
			}
		} else {
			return newChooser.showOpenDialog(null);
		}
	}
	
	public File showSaveDialog(JFrame parent) {
		if (old) {
			if (oldChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				return oldChooser.getSelectedFile();
			} else {
				return null;
			}
		} else {
			return newChooser.showSaveDialog(null);
		}
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
