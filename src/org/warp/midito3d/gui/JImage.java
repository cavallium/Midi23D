package org.warp.midito3d.gui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JImage extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7495812403804139277L;
	private BufferedImage image;

    public JImage(String path) {
    	if (path != null) {
    		try {                
    			image = ImageIO.read(new File(path));
    			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    		} catch (IOException ex) {
    			System.err.println("Image not found!");
    		}
    	}
    	this.setOpaque(false);
    }
    
    public static JImage loadFromResources(String path) {
    	JImage img = new JImage(null);
    	try {
			img.image = ImageIO.read(img.getClass().getClassLoader().getResource(path));
			img.setPreferredSize(new Dimension(img.image.getWidth(), img.image.getHeight()));
		} catch (IOException | NullPointerException | IllegalArgumentException e) {
			System.err.println("Image resource not found!");
		}
		return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(rh);
		
        super.paintComponent(g2d);
        int width = this.getWidth();
        int height = this.getHeight();
        int min = width<height?width:height;
        g2d.drawImage(image, this.getWidth()/2-min/2, this.getHeight()/2-min/2, min, min, this); // see javadoc for more info on the parameters      
    }

}