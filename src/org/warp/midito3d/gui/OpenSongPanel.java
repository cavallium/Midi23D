package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;

public class OpenSongPanel extends SongPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4440013224587289302L;

	public final JImage songIcon;
	public final JLabel description;
	
	public OpenSongPanel() {
		GridBagConstraints c = new GridBagConstraints();
		
		super.setLayout(new GridBagLayout());
		songIcon = JImage.loadFromResources("song-icon.png");
		description = new JLabel("Please load a midi file");

		songIcon.setMinimumSize(new Dimension(50, 50));
		songIcon.setPreferredSize(new Dimension(100, 100));
		songIcon.setMaximumSize(new Dimension(100, 100));
		
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.setBackground(Color.white);

		c.gridx = 0;
		c.gridy = 0;
		add(songIcon, c);
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(description, c);
		
		description.setForeground(Color.DARK_GRAY);
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if (description != null) {
			description.setFont(f);
		}
	}
	
	
}
