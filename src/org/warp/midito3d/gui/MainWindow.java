package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.warp.midito3d.Midi23D;
import org.warp.midito3d.gui.printers.Model3Axes;
import org.warp.midito3d.gui.printers.PrinterModel;
import org.warp.midito3d.gui.printers.PrinterModelArea;
import org.warp.midito3d.gui.ModernDialog.ModernExtensionFilter;
import org.warp.midito3d.midi.MidiMusic;
import org.warp.midito3d.midi.MidiMusicEvent;
import org.warp.midito3d.midi.MidiParser;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Printer;
import org.warp.midito3d.printers.Printer3Axes;


public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -3506064352026393354L;
	
	static MainWindow INSTANCE;
	public SongPanel songPanel;

	public PrinterModel printerModel;
	public PrinterModelArea printerModelArea;
	public MidiMusic midi;
	public JButton exportBtn;
	
	public MainWindow() {
		INSTANCE = this;
		
		this.setLayout(new GridLayout(1, 1));
		
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel mainPanel = new JPanel(new GridLayout(1, 2));
		
		JPanel leftPanel = new JPanel(new GridBagLayout());
		JPanel rightPanel = new JPanel(new GridBagLayout());

		JLabel leftPanelName = new JLabel("Input song");
		songPanel = new OpenSongPanel();
		JButton openMidiButton = new JButton("Open...");
		exportBtn = new JButton("Export...");

		JLabel rightPanelName = new JLabel("Printer settings");
		PrinterPanel printerPanel = new PrinterPanel();
		
		this.add(mainPanel);

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);

		c.insets = new Insets(5,5,2,3);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		leftPanel.add(leftPanelName, c);
		c.insets = new Insets(0,5,0,3);
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		leftPanel.add(songPanel, c);
		c.insets = new Insets(5,5,5,3);
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		leftPanel.add(openMidiButton, c);
		c.insets = new Insets(5,5,5,3);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		leftPanel.add(exportBtn, c);

		c.insets = new Insets(5,2,2,5);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		rightPanel.add(rightPanelName, c);
		c.insets = new Insets(0,2,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		rightPanel.add(printerPanel, c);
		
		songPanel.setMinimumSize(new Dimension(150, 130));
		songPanel.setPreferredSize(new Dimension(350, 200));
		songPanel.setMaximumSize(new Dimension(350, 200));
		leftPanel.setBackground(Color.white);
		rightPanel.setBackground(Color.white);
		this.setBackground(Color.white);
		this.setMinimumSize(new Dimension(630, 370));
		this.setPreferredSize(new Dimension(640, 400));
		this.pack();
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		leftPanel.setFont(this.getFont());
		leftPanelName.setFont(this.getFont());
		songPanel.setFont(this.getFont());
		openMidiButton.setFont(this.getFont());
		openMidiButton.addActionListener((e)->{
			ModernDialog.runLater(()->{
				ModernDialog diag = new ModernDialog();
				diag.setTitle("Open Midi File");
				diag.setExtensions(new ModernExtensionFilter("Midi files", "*.midi", "*.mid"), new ModernExtensionFilter("All files", "*.*"));
				File f = diag.show(this);
				if (f != null && f.exists()) {
					importMidi(f);
				}
			});
		});
		exportBtn.setFont(this.getFont());
		exportBtn.addActionListener((e)->{
			exportMidiDialog();
		});
		exportBtn.setVisible(false);
		rightPanel.setFont(this.getFont());
		rightPanelName.setFont(this.getFont());
		printerPanel.setFont(this.getFont());
	}
	
	private synchronized void importMidi(File f) {
		try {
			MidiMusic mus = MidiParser.loadFrom(f.toString());
			mus.setSpeedMultiplier(2f);
			this.midi = mus;
			Container parent = songPanel.getParent();
			parent.remove(songPanel);
			songPanel = new InputSongPanel(f, mus);
			songPanel.setMinimumSize(new Dimension(150, 130));
			songPanel.setPreferredSize(new Dimension(350, 200));
			songPanel.setMaximumSize(new Dimension(350, 200));
			songPanel.setFont(this.getFont());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(0,5,0,3);
			c.weightx = 1;
			c.weighty = 1;
			c.gridy = 1;
			c.gridwidth = 2;
			parent.add(songPanel,c);
			exportBtn.setVisible(true);
			this.validate();
			this.repaint();
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void exportMidiDialog() {
		ModernDialog.runLater(()->{
			ModernDialog diag = new ModernDialog();
			diag.setTitle("Save G-CODE File");
			diag.setExtensions(new ModernExtensionFilter("G-CODE files", "*.gcode", "*.gco"), new ModernExtensionFilter("All files", "*.*"));
			File f = diag.showSaveDialog(this);
			if (f != null) {
				exportMidi(f);
			}
		});
	}
	
	private synchronized void exportMidi(File output) {
		Printer p = printerModel.createPrinterObject(printerModelArea);
		Midi23D midi23D = new Midi23D(p, midi, new GCodeOutput(output), false);
		try {
			midi23D.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void execute() {
		this.setVisible(true);
	}
	
	/*
		Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
		
		Graphics2D g2d = (Graphics2D) g;
		if (desktopHints != null) {
		    g2d.setRenderingHints(desktopHints);
		}

        *
        *
        *

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
	 */

}