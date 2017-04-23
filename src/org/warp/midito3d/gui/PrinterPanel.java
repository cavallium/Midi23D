package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box.Filler;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrinterPanel extends JPanel {

	private static final long serialVersionUID = 3730582196639810443L;

	public PrinterPanel() {
		GridBagConstraints c = new GridBagConstraints();
		
		super.setLayout(new GridBagLayout());
		
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));

		JComboBox<String> modeList = new JComboBox<String>(new String[]{"Z Axis", "XYZ Axes", "XYZ Axes + Extruder"});
		c.insets = new Insets(5,5,0,3);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		this.add(new JLabel("Model"), c);
		c.insets = new Insets(0,5,2,3);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		this.add(modeList, c);
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		this.add(new JPanel(), c);
		
		modeList.setSelectedIndex(1);
		modeList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}});
	}
}
