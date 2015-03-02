/*
 * This class defines some static methods of image processing.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProc {

	public static BufferedImage scaleDown(BufferedImage img, int factor) {
		if (factor <= 0)
			throw new IllegalArgumentException();
		int newHeight = img.getHeight()/factor;
		int newWidth = img.getWidth()/factor;
		BufferedImage out = new BufferedImage(newWidth, newHeight, img.getType());
		for (int x = 0; x < newWidth; x++)
			for (int y = 0; y < newHeight; y++)
				out.setRGB(x, y, img.getRGB(x*factor, y*factor));
		return out;
	}
	
	public static BufferedImage grayScale(BufferedImage img) {
		//TODO implement this
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage out = new BufferedImage(width, height, img.getType());

		return null;
	}
	
	public static BufferedImage horizontalDerivative(BufferedImage img) {
		//TODO implement this
		return null;
	}

	public static BufferedImage verticalDerivative(BufferedImage img) {
		//TODO implement this
		return null;
	}
	public static BufferedImage gradientMagnitude(BufferedImage img) {
		//TODO implement this
		return null;
	}
	
	public static BufferedImage retargetSize(BufferedImage img, int width, int height) {		
		//TODO implement this
		return null;
	}
	
	
	public static BufferedImage showSeams(BufferedImage img, int width, int height) {
		//TODO implement this
		return null;
		
	}
	
}
		
	



