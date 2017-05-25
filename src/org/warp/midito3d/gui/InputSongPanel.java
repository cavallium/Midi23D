package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import org.warp.midito3d.midi.MidiMusic;

public class InputSongPanel extends SongPanel {

	private static final long serialVersionUID = 4440013224587289302L;

	public final JImage songIcon;
	public final JLabel description;
	private final MidiMusic midi;
	
	public InputSongPanel(File fileName, MidiMusic music) {
		this.midi = music;
		
		GridBagConstraints c = new GridBagConstraints();
		
		super.setLayout(new GridBagLayout());
		
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.setBackground(Color.white);

		songIcon = JImage.loadFromResources("cd-icon.png");
		songIcon.setMinimumSize(new Dimension(64, 64));
		songIcon.setPreferredSize(new Dimension(64, 64));
		songIcon.setMaximumSize(new Dimension(64, 64));
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.weightx = 0;
		c.weighty = 0;
		add(songIcon, c);
		description = new JLabel(fileName.getName());
		description.setMinimumSize(new Dimension(10, 32-5-5));
		description.setMaximumSize(new Dimension(999999, 32-5-5));
		description.setVerticalAlignment(JLabel.BOTTOM);
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		add(description, c);
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(new JLabel(fileName.toString()), c);
		JLabel label = new JLabel("Song speed");
		label.setMinimumSize(new Dimension(130, 20));
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(label, c);
		final JSpinner speedAdj = new JSpinner(new SpinnerNumberModel(1, 0.125, 4, 0.125));
		speedAdj.setMinimumSize(new Dimension(75, 20));
		speedAdj.setPreferredSize(new Dimension(75, 20));
		speedAdj.setMaximumSize(new Dimension(75, 20));
		speedAdj.addChangeListener((ChangeEvent e)->{
				midi.setSpeedMultiplier((float)((double)speedAdj.getValue()));
		});
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(speedAdj, c);
		label = new JLabel("Tone multiplier");
		label.setMinimumSize(new Dimension(130, 20));
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(label, c);
		final JSpinner toneAdj = new JSpinner(new SpinnerNumberModel(1, 0.01, 100, 0.25));
		toneAdj.setMinimumSize(new Dimension(75, 20));
		toneAdj.setPreferredSize(new Dimension(75, 20));
		toneAdj.setMaximumSize(new Dimension(75, 20));
		toneAdj.addChangeListener((ChangeEvent e)->{
				midi.setToneMultiplier((float)((double)toneAdj.getValue()));
		});
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(toneAdj, c);
		
		description.setForeground(Color.DARK_GRAY);
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if (description != null) {
			description.setFont(new Font(f.getFontName(), Font.PLAIN, (int)(f.getSize()*1.5d)));
		}
	}
}
