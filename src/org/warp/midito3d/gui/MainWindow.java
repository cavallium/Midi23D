package org.warp.midito3d.gui;

import java.awt.Color;
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
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.warp.midito3d.midi.MidiMusic;
import org.warp.midito3d.midi.MidiParser;

import com.seaglasslookandfeel.SeaGlassLookAndFeel;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -3506064352026393354L;
	
	private static MainWindow INSTANCE;
	public SongPanel songPanel;
	
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

		c.insets = new Insets(5,2,2,5);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		rightPanel.add(rightPanelName, c);
		c.insets = new Insets(0,2,5,5);
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
		this.setMinimumSize(new Dimension(400, 200));
		this.setPreferredSize(new Dimension(760, 360));
		this.pack();
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		leftPanel.setFont(this.getFont());
		leftPanelName.setFont(this.getFont());
		songPanel.setFont(this.getFont());
		openMidiButton.setFont(this.getFont());
		rightPanel.setFont(this.getFont());
		rightPanelName.setFont(this.getFont());
		printerPanel.setFont(this.getFont());
	}
	
	public void execute() {
		this.setVisible(true);
	}
	
	public static void openSong(String file) {
		MidiMusic music = MidiParser.loadFrom(file);
		INSTANCE.songPanel = new InputSongPanel(music);
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