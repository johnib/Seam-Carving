/**
 * Computer Graphics - IDC
 * Assignment 1 - Seam Carving
 * This project has been implemented by Jonathan Yaniv ONLY.
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
        int width = img.getWidth(), height = img.getHeight(), max = 1, mapped;
        int[][] gradient = new int[height][width];
        BufferedImage out = new BufferedImage(width, height, img.getType());
        BufferedImage gray = grayScale(img);

        for (int y = 1; y < height - 1; y++) {
            gradient[y][0] = 0;
            gradient[y][width - 1] = 0;
        }

        for (int x = 1; x < width - 1; x++)
            for (int y = 1; y < height - 1; y++) {
                int dx = (gray.getRGB(x - 1, y) & 0xFF) - (gray.getRGB(x + 1, y) & 0xFF);
                int dy = (gray.getRGB(x, y - 1) & 0xFF) - (gray.getRGB(x, y + 1) & 0xFF);
                gradient[y][x] = (int) Math.sqrt(dx * dx + dy * dy);
                if (max < gradient[y][x]) max = gradient[y][x];
            }

        // map the values to [0,255]
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                mapped = gradient[y][x] * 255 / max;
                out.setRGB(x, y, new Color(mapped, mapped, mapped).getRGB());
            }

        return out;
    }

    public static BufferedImage retargetSize(BufferedImage img, int width, int height) {
        if (width == img.getWidth() && height == img.getHeight()) return img;
        if (width < 1 || height < 1 || width > 2 * img.getWidth() || height > 2 * img.getHeight()) {
            System.out.println("newSize is off limits.\nreturns original.");
            return img;
        }

        BufferedImage current = img;
        if (width != img.getWidth())
            current = new Retargeter(current, false).retarget(width);

        if (height != img.getHeight())
            current = new Retargeter(current, true).retarget(height);

        return current;
    }


    public static BufferedImage showSeams(BufferedImage img, int width, int height) {
        if (width == img.getWidth() && height == img.getHeight()) return img;
        if (width < 1 || height < 1 || width > 2 * img.getWidth() || height > 2 * img.getHeight()) {
            System.out.println("newSize is off limits.\nreturns original.");
            return img;
        }

        int[][] seams;
        int orig_w = img.getWidth(), orig_h = img.getHeight();
        BufferedImage out = img.getSubimage(0, 0, orig_w, orig_h);
        Retargeter retargeter;
        if (width != orig_w) {
            retargeter = new Retargeter(img, false);
            retargeter.retarget(width);
            seams = retargeter.getSeamsOrderMatrix();
            for (int x = 0; x < orig_w; x++)
                for (int y = 0; y < orig_h; y++)
                    if (seams[y][x] != 0)
                        out.setRGB(x, y, new Color(255, 0, 0).getRGB());
        }

        if (height != orig_h) {
            retargeter = new Retargeter(img, true);
            retargeter.retarget(height);
            seams = transpose(retargeter.getSeamsOrderMatrix());
            for (int x = 0; x < orig_w; x++)
                for (int y = 0; y < orig_h; y++)
                    if (seams[y][x] != 0)
                        out.setRGB(x, y, new Color(0, 212, 0).getRGB());
        }
        return out;

    }

    static BufferedImage transpose(BufferedImage img) {
        BufferedImage out = new BufferedImage(img.getHeight(), img.getWidth(), img.getType());
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                out.setRGB(y, x, img.getRGB(x, y));

        return out;
    }

    static int[][] transpose(int[][] arr) {
        int[][] out = new int[arr[0].length][arr.length];
        for (int x = 0; x < arr.length; x++)
            for (int y = 0; y < arr[0].length; y++)
                out[y][x] = arr[x][y];

        return out;
    }
}
