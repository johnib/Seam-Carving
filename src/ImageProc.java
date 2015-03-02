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
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage out = new BufferedImage(width, height, img.getType());
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.setRGB(x, y, getGray(img.getRGB(x, y)));
		return out;
	}

    // returns the gray color of a given pixel's RGB value.
    private static int getGray(int rgb) {
        Color px = new Color(rgb);
        int red = px.getRed();
        int green = px.getGreen();
        int blue = px.getBlue();
        int min = red;
        if (green < min) min = green;
        if (blue < min) min = blue;
        return new Color(min, min, min).getRGB();
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
		
	



