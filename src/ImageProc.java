/*
 * This class defines some static methods of image processing.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProc {

    public static BufferedImage scaleDown(BufferedImage img, int factor) {
        if (factor <= 0)
            throw new IllegalArgumentException();
        int newHeight = img.getHeight() / factor;
        int newWidth = img.getWidth() / factor;
        BufferedImage out = new BufferedImage(newWidth, newHeight, img.getType());
        for (int x = 0; x < newWidth; x++)
            for (int y = 0; y < newHeight; y++)
                out.setRGB(x, y, img.getRGB(x * factor, y * factor));
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
        int avg = (px.getRed() + px.getGreen() + px.getBlue()) / 3;
        return new Color(avg, avg, avg).getRGB();
    }

    public static BufferedImage horizontalDerivative(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage out = new BufferedImage(width, height, img.getType());
        BufferedImage gray = grayScale(img);

        for (int x = 1; x < width - 1; x++)
            for (int y = 0; y < height; y++) { // calc dx as discretely as defined.
                int dx = ((gray.getRGB(x - 1, y) & 0xFF) - (gray.getRGB(x + 1, y) & 0xFF) + 255) / 2;
                out.setRGB(x, y, new Color(dx, dx, dx).getRGB());
            }

        // handle the sides edges of the picture
        for (int y = 0; y < height; y++) {
            out.setRGB(0, y, new Color(127, 127, 127).getRGB());
            out.setRGB(width - 1, y, new Color(127, 127, 127).getRGB());
        }

        return out;
    }

    public static BufferedImage verticalDerivative(BufferedImage img) {
        return transpose(horizontalDerivative(transpose(img)));
    }

    public static BufferedImage gradientMagnitude(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage out = new BufferedImage(width, height, img.getType());
        BufferedImage gray = grayScale(img);

        for (int x = 1; x < width - 1; x++)
            for (int y = 1; y < height - 1; y++) {
                int dx = (gray.getRGB(x - 1, y) & 0xFF) - (gray.getRGB(x + 1, y) & 0xFF);
                int dy = (gray.getRGB(x, y - 1) & 0xFF) - (gray.getRGB(x, y + 1) & 0xFF);
                int norma = (int) Math.sqrt(dx * dx + dy * dy);
                if (norma > 255)
                    out.setRGB(x, y, new Color(255, 255, 255).getRGB());
                else
                    out.setRGB(x, y, new Color(norma, norma, norma).getRGB());
            }

        for (int y = 1; y < height - 1; y++) {
            out.setRGB(0, y, new Color(0, 0, 0).getRGB());
            out.setRGB(width - 1, y, new Color(0, 0, 0).getRGB());
        }

        return out;
    }

    public static BufferedImage retargetSize(BufferedImage img, int width, int height) {
        //TODO implement this
        return null;
    }


    public static BufferedImage showSeams(BufferedImage img, int width, int height) {
        //TODO implement this
        return null;

    }

    private static BufferedImage transpose(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                out.setRGB(y, x, img.getRGB(x, y));

        return out;
    }
}
