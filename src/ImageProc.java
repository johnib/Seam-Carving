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
        int avg = (red + green + blue) / 3;
        return new Color(avg, avg, avg).getRGB();
    }
	
	public static BufferedImage horizontalDerivative(BufferedImage img) {
		//TODO implement this
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage out = new BufferedImage(width, height, img.getType());
        BufferedImage gray = grayScale(img);
        for (int x = 0; x < width - 1; x++)
            for (int y = 0; y < height; y++) {
                int g = gray.getRGB(x, y) & 0xFF;
                int g1 = gray.getRGB(x + 1, y) & 0xFF;
                int dx = (g - g1 + 255)/2;
                out.setRGB(x, y, new Color(dx, dx, dx).getRGB());
            }
		return out;
	}

	public static BufferedImage verticalDerivative(BufferedImage img) {
		//TODO implement this
        BufferedImage out = horizontalDerivative(rotateLeft(img));
        return rotateRight(out);
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

    // rotates the picture 90 degrees left.
    private static BufferedImage rotateLeft(BufferedImage img) {
        return rotate(img, 1);
    }

    // rotates the picture 90 degrees right.
    private static BufferedImage rotateRight(BufferedImage img) {
        return rotate(img, 0);
    }

    // rotates 90 deg left of @left == 1 and 90 deg right of @left == 0, otherwise returns the img as is.
    private static BufferedImage rotate(BufferedImage img, int left) {
        if (left != 0 && left != 1) return img;
        int width = img.getWidth();
        int height= img.getHeight();
        BufferedImage out = new BufferedImage(height, width, img.getType());
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.setRGB((left == 1) ? y : height - y - 1, (left == 1) ? width - x - 1 : x, img.getRGB(x, y));
        return out;
    }
}
		
	



