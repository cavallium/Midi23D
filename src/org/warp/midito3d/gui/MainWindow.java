package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.warp.midito3d.FilenameUtils;
import org.warp.midito3d.Midi23D;
import org.warp.midito3d.gui.printers.PrinterModel;
import org.warp.midito3d.gui.printers.PrinterModelArea;
import org.warp.midito3d.music.Music;
import org.warp.midito3d.music.midi.MidiParser;
import org.warp.midito3d.music.mp3.Mp3Parser;
import org.warp.midito3d.gui.ModernDialog.ModernExtensionFilter;
import org.warp.midito3d.printers.GCodeOutput;
import org.warp.midito3d.printers.Printer;

import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.DecoderException;


public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -3506064352026393354L;
	
	static MainWindow INSTANCE;
	public SongPanel songPanel;

	public PrinterModel printerModel;
	public PrinterModelArea printerModelArea;
	public Music music;
	public JButton exportBtn;
	
	public MainWindow() {
		INSTANCE = this;
		
		try {
			this.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("3DPrinter128.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.setTitle("Midi23D");
		this.setLayout(new GridLayout(1, 1));
		
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		JPanel leftPanel = new JPanel(new GridBagLayout());
		JPanel rightPanel = new JPanel(new GridBagLayout());

		JLabel leftPanelName = new JLabel("Input song");
		songPanel = new OpenSongPanel();
		JButton openMidiButton = new JButton("Open...");
		exportBtn = new JButton("Export...");

		JLabel rightPanelName = new JLabel("Printer settings");
		PrinterPanel printerPanel = new PrinterPanel();
		
		this.add(mainPanel);

		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.5;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(leftPanel,c);
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 2;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		mainPanel.add(rightPanel,c);

		c.insets = new Insets(5,5,2,3);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
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
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		leftPanel.add(openMidiButton, c);
		c.insets = new Insets(5,5,5,3);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		leftPanel.add(exportBtn, c);

		c.insets = new Insets(5,2,2,5);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
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
				diag.setExtensions(new ModernExtensionFilter("Midi files", "*.midi", "*.mid"), /*new ModernExtensionFilter("Mp3 or WAV files", "*.mp3", "*.wav"),*/ new ModernExtensionFilter("All files", "*.*"));
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
			String ext = FilenameUtils.getExtension(f.toString());
			Music mus = null;
			if (ext.length() > 0) {
				if (ext.equals("mid") || ext.equals("midi")) {
					System.out.println("Importing midi file...");
					mus = MidiParser.loadFrom(f.toString(), true);
					System.out.println("Imported successfully.");
				} else /*if (ext.equals("mp3"))*/ {
					System.out.println("Importing mp3 file...");
					mus = Mp3Parser.loadFrom(f.toString(), true);
					System.out.println("Imported successfully.");
				}
			}
			if (mus == null) {
				throw new java.lang.UnsupportedOperationException();
			}
			this.music = mus;
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
		} catch (InvalidMidiDataException | IOException | UnsupportedAudioFileException | DecoderException | BitstreamException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void exportMidiDialog() {
		ModernDialog.runLater(()->{
			ModernDialog diag = new ModernDialog();
			diag.setTitle("Save G-CODE File");
			diag.setExtensions(new ModernExtensionFilter("G-CODE files", "*.gco", "*.gcode"), new ModernExtensionFilter("All files", "*.*"));
			File f = diag.showSaveDialog(this);
			if (f != null) {
				exportMidi(f);
			}
		});
	}
	
	private synchronized void exportMidi(File output) {
		Printer p = printerModel.createPrinterObject(printerModelArea);
		Midi23D midi23D = new Midi23D(p, music, new GCodeOutput(output), false);
		try {
			midi23D.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void execute() {
		this.setLocationRelativeTo(null);
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