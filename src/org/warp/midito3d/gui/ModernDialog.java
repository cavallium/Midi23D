package org.warp.midito3d.gui;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * Modern dialog
 * @version 1.0.0
 * @author Andrea Cavalli
 *
 */
public class ModernDialog {

	private boolean awtChooser;
	private JFileChooser jchooser;
	private ModernExtensionFilter[] awtChooserFilters;
	private String awtChooserTitle;

	public ModernDialog() {
		try {
			awtChooser = true;
		} catch (Throwable ex) {
			jchooser = new JFileChooser();
		}
	}
	
	public static void runLater(Runnable r) {
		SwingUtilities.invokeLater(r);
	}
	
	public void setTitle(String text) {
		if (awtChooser) {
			awtChooserTitle = text;
		} else {
			jchooser.setDialogTitle(text);
		}
	}
	
	public void setExtensions(final ModernExtensionFilter... filters) {
		if (awtChooser) {
			awtChooserFilters = filters;
		} else {
			jchooser.setFileFilter(new FileFilter() {

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
		}
	}

	private FileDialog createAwtChooser(JFrame parent, boolean load) {
		FileDialog awtChooser = new java.awt.FileDialog(parent, awtChooserTitle, load ? FileDialog.LOAD : FileDialog.SAVE);
		awtChooser.setMultipleMode(false);
		awtChooser.setFilenameFilter((dir, name) -> {
			for (ModernExtensionFilter filter : awtChooserFilters) {
				for (int i = 0, n = filter.filters.length; i < n; i++) {
					String extension = filter.filters[i].substring(2);
					if ((name.endsWith(extension) && (name.charAt(name.length() - extension.length() - 1)) == '.')) {
						return true;
					}
				}
			}
			return false;
		});
		return awtChooser;
	}
	
	public File show(JFrame parent) {
		if (awtChooser) {
			FileDialog awtChooser = createAwtChooser(parent, true);
			awtChooser.setLocationRelativeTo(null);
			awtChooser.setVisible(true);
			File[] result = awtChooser.getFiles();
			if (result.length == 0) {
				return null;
			} else {
				return result[0];
			}
		} else {
			if (jchooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				return jchooser.getSelectedFile();
			} else {
				return null;
			}
		}
	}

	public File showSaveDialog(JFrame parent) {
		if (awtChooser) {
			FileDialog awtChooser = createAwtChooser(parent, false);
			awtChooser.setVisible(true);
			File[] result = awtChooser.getFiles();
			if (result.length == 0) {
				return null;
			} else {
				return result[0];
			}
		} else {
			if (jchooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				return jchooser.getSelectedFile();
			} else {
				return null;
			}
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
