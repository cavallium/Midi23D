package org.warp.midito3d.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.warp.midito3d.gui.printers.Model3Axes;
import org.warp.midito3d.gui.printers.Model4Axes;
import org.warp.midito3d.gui.printers.ModelZAxis;
import org.warp.midito3d.gui.printers.PrinterModel;
import org.warp.midito3d.gui.printers.PrinterModelArea;

public class PrinterPanel extends JPanel {

	private static final long serialVersionUID = 3730582196639810443L;
	private JImage modelImg;
	private JPanel motorList;
	private Component marginsImg;
	
	
	public PrinterPanel() {
		GridBagConstraints c = new GridBagConstraints();
		
		super.setLayout(new GridBagLayout());
		
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));

		modelImg = JImage.loadFromResources("3DPrinter128.png");
		modelImg.setPreferredSize(new Dimension(128, 128));
		modelImg.setMinimumSize(new Dimension(64, 128));
		modelImg.setMaximumSize(new Dimension(128, 128));
		c.insets = new Insets(5,5,0,3);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 5;
		c.gridheight = 2;
		this.add(modelImg, c);
		JLabel modelText = new JLabel("Model");
		modelText.setVerticalAlignment(SwingConstants.BOTTOM);
		c.insets = new Insets(5,5,0,3);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 5;
		c.gridheight = 1;
		this.add(modelText, c);
		JComboBox<PrinterModel> modeList = new JComboBox<PrinterModel>(new PrinterModel[]{new ModelZAxis(), new Model3Axes(), new Model4Axes()});
		modeList.setMinimumSize(new Dimension(130, 20));
		modeList.setPreferredSize(new Dimension(130, 20));
		c.insets = new Insets(0,5,2,3);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 1;
		this.add(modeList, c);
		JLabel motorListText = new JLabel("Motors");
		motorListText.setVerticalAlignment(SwingConstants.BOTTOM);
		motorListText.setHorizontalAlignment(SwingConstants.CENTER);
		Font f = motorListText.getFont();
		motorListText.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
		c.insets = new Insets(5,5,0,3);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		this.add(motorListText, c);
		motorList = new JPanel();
		motorList.setLayout(new BoxLayout(motorList, BoxLayout.Y_AXIS));
		motorList.setPreferredSize(new Dimension(1000, 128));
		motorList.setMinimumSize(new Dimension(64, 64));
		motorList.setBackground(new Color(0,0,0,0));
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 10;
		c.gridheight = 1;
		this.add(motorList, c);
		JPanel bottomPan = new JPanel();
		bottomPan.setMinimumSize(new Dimension(300,100));
		bottomPan.setMaximumSize(new Dimension(300,100));
		bottomPan.setLayout(null);
		bottomPan.setBackground(this.getBackground());
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 10;
		c.gridheight = 1;
		this.add(bottomPan, c);
		MainWindow.INSTANCE.printerModelArea = new PrinterModelArea(new int[]{200,200,10}, new int[]{15,15,2});
		final JSpinner zSize = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.size[2], 1, 1000, 1));
		zSize.setSize(45,20);
		zSize.setLocation(00,50);
		zSize.addChangeListener((ChangeEvent e)->{
				MainWindow.INSTANCE.printerModelArea.size[2] = (int) zSize.getValue();
		});
		bottomPan.add(zSize, c);
		final JSpinner xSize = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.size[0], 1, 1000, 1));
		xSize.setSize(45,20);
		xSize.setLocation(10,80);
		xSize.addChangeListener((ChangeEvent e)->{
			MainWindow.INSTANCE.printerModelArea.size[0] = (int) xSize.getValue();
		});
		bottomPan.add(xSize, c);
		final JSpinner ySize = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.size[1], 1, 1000, 1));
		ySize.setSize(45,20);
		ySize.setLocation(95,80);
		ySize.addChangeListener((ChangeEvent e)->{
				MainWindow.INSTANCE.printerModelArea.size[1] = (int) ySize.getValue();
		});
		bottomPan.add(ySize, c);
		final JSpinner yMargin = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.margins[1], 1, 1000, 1));
		yMargin.setSize(45,20);
		yMargin.setLocation(145,15);
		yMargin.addChangeListener((ChangeEvent e)->{
				MainWindow.INSTANCE.printerModelArea.margins[1] = (int) yMargin.getValue();
		});
		bottomPan.add(yMargin, c);
		final JSpinner zMargin = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.margins[2], 1, 1000, 1));
		zMargin.setSize(45,20);
		zMargin.setLocation(120,55);
		zMargin.addChangeListener((ChangeEvent e)->{
				MainWindow.INSTANCE.printerModelArea.margins[2] = (int) zMargin.getValue();
		});
		bottomPan.add(zMargin, c);
		final JSpinner xMargin = new JSpinner(new SpinnerNumberModel(MainWindow.INSTANCE.printerModelArea.margins[0], 1, 1000, 1));
		xMargin.setSize(45,20);
		xMargin.setLocation(235,15);
		xMargin.addChangeListener((ChangeEvent e)->{
				MainWindow.INSTANCE.printerModelArea.margins[0] = (int) xMargin.getValue();
		});
		bottomPan.add(xMargin, c);
		JImage areaImg = JImage.loadFromResources("PrinterArea100.png");
		areaImg.setSize(100, 100);
		areaImg.setLocation(25, 0);
		bottomPan.add(areaImg, c);
		marginsImg = JImage.loadFromResources("PrinterMargins100.png");
		marginsImg.setSize(100, 100);
		marginsImg.setLocation(160, 0);
		bottomPan.add(marginsImg, c);
		
		modeList.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					PrinterModel item = (PrinterModel) event.getItem();
					setPrinterModel(item);
				}
			}   
		});
		modeList.setSelectedIndex(1);
	}
	
	public void setPrinterModel(PrinterModel model) {
		MainWindow.INSTANCE.printerModel = model;
		motorList.removeAll();
		motorList.setMinimumSize(new Dimension(64, model.getMotorsCount()*25));
		motorList.setMaximumSize(new Dimension(1000, model.getMotorsCount()*25));
		GridBagConstraints c = new GridBagConstraints();
		for (int i = 0; i < model.getMotorsCount(); i++) {
			final int fi = i;
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			p.setBackground(new Color(0,0,0,0));

			JLabel motLabel = new JLabel(model.getMotorName(i));
			motLabel.setVerticalAlignment(SwingConstants.CENTER);
			motLabel.setMinimumSize(new Dimension(50,20));
			motLabel.setPreferredSize(new Dimension(50,20));
			motLabel.setMaximumSize(new Dimension(9999,20));
			c.insets = new Insets(0,5,0,3);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			p.add(motLabel, c);
			final JSpinner motVal = new JSpinner(new SpinnerNumberModel(model.getMotor(i).ppi, 1, 2000, 10));
			motVal.setMinimumSize(new Dimension(80,20));
			motVal.setPreferredSize(new Dimension(80,20));
			motVal.setMaximumSize(new Dimension(9999,20));
			motVal.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					model.getMotor(fi).ppi = (int) motVal.getValue();
					System.out.println(model.getMotor(fi).ppi);
				}
			});
			c.insets = new Insets(0,5,0,3);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 1;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			p.add(motVal, c);
			motorList.add(p);
		}
		motorList.revalidate();
	}
}
