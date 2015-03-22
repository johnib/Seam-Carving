/**
 * Computer Graphics - IDC
 * Assignment 1 - Seam Carving
 * This project has been implemented by Jonathan Yaniv ONLY.
 *
 *
 * This class is implemented a bit different, with slightly different signatures.
 * The procedure from creating a new Retargeter to return the retargeted image is as follows:
 *
 * 1. Set parameters, transpose the picture if needed (when not vertical).
 * 2. Calculate the gray scaled image.
 * 3. Repeat from 1 to k :
 *  a. Calculate cost matrix.
 *  b. Set the seam's pixels to k in the seamsMat.
 *  c. Shift gray scaled picture 1 pixel left (overwrite the seam).
 * 4. Cut or duplicate the k seams.
 * 5. return retargeted picture.
 */

/**
 * Important notes:
 *
 * I could not fix the bug where one seams overwrites another in the seamsMatrix.
 * Thus the retargeter will work fine for few seams, depending on the image and the seams location.
 * That is a bug that I could not fix on time, still the project behaves as expected (concept wise).
 */

import java.awt.image.BufferedImage;
public class Retargeter {

    /* this is the input image, transposed if m_isVertical */
    private final BufferedImage origPic;

    /* both width and height are of the origPic */
    private final int width;
    private final int height;
    private final boolean isVertical;

    private int[][] grayArr; // array of gray scaled values
    private int grayWidth;

    private int[][] costMat; // cost matrix
    private final int[][] seamsMat; // seams order matrix

    private static int HIGH_VALUE = Integer.MAX_VALUE >> 6;

    public Retargeter(BufferedImage m_img, boolean m_isVertical) {
        isVertical = m_isVertical;
        if (isVertical)
            origPic = ImageProc.transpose(m_img).getSubimage(0, 0, m_img.getHeight(), m_img.getWidth());
        else
            origPic = m_img.getSubimage(0, 0, m_img.getWidth(), m_img.getHeight());

        width = origPic.getWidth();
        height = origPic.getHeight();
        grayWidth = width;
        BufferedImage gray = ImageProc.grayScale(origPic);
        grayArr = new int[height][width];
        seamsMat = new int[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                grayArr[y][x] = gray.getRGB(x, y) & 0xFF;
                seamsMat[y][x] = 0;
            }
    }

    public int[][] getSeamsOrderMatrix() {
        return seamsMat;
    }

    /**
     * This function is the actual size changer.
     * The procedure goes like this:
     * 1. validate input.
     * 2. calculate Seams Order Matrix, given the new size.
     * 3. create new BufferedImage with the new size (wider/shorter).
     * 4. for each pixel in the original picture:
     *  4.1 if its seamsMat value == 0 copy it to the picture.
     *  4.2 else double/don't copy it increase offset.
     *
     * @param newSize the wanted new width of the picture. oldSize - newSize = k seams.
     * @return new re-sized BufferedImage
     */
    public BufferedImage retarget(int newSize) {
        int k = newSize - width;
        calculateSeamsOrderMatrix(Math.abs(k)); // process order matrix

        BufferedImage out = new BufferedImage(width + k, height, origPic.getType());
        int offset;
        for (int y = 0; y < height; y++) {
            offset = 0;
            for (int x = 0; x < width; x++) {
                if (seamsMat[y][x] == 0) // pixel not in seam
                    out.setRGB(x + offset, y, origPic.getRGB(x, y));
                else {
                    if (k > 0) { // duplicate seam
                        out.setRGB(x + offset++, y, origPic.getRGB(x, y));
                        out.setRGB(x + offset, y, origPic.getRGB(x, y));
                    } else // or remove seam
                        offset--;
                }
            }
        }
        return (isVertical) ? ImageProc.transpose(out) : out;
    }

    private void calculateSeamsOrderMatrix(int k) {
        int x, y, min, left, mid, right;
        for (int i = 1; i <= k; i++) {
            calculateCostsMatrix();

            // find min seam's end-pixel coordinate
            x = 1; y = height - 1; min = costMat[y][x];
            for (int j = 1; j <= grayWidth; j++)
                if (min > costMat[y][j] && seamsMat[y][j - 1] == 0) {
                    x = j;
                    min = costMat[y][j];
                }

            // mark whole seam in seams matrix
            for (; y > 0; y--) {
                seamsMat[y][x - 1] = i;
                left = costMat[y - 1][x - 1] + cl(y, x - 1);
                mid = costMat[y - 1][x] + cv(y, x - 1);
                right = costMat[y - 1][x + 1] + cl(y, x - 1);
                if (left <= mid && left <= right)
                    x--;
                else if (right <= mid && right <= left)
                    x++;
            }
            seamsMat[y][x - 1] = i; // do the same for the last pixel
            grayArr = shift(1); // remove the marked seam
        }

    }

    private void calculateCostsMatrix() {
        costMat = new int[height][grayWidth + 2];
        costMat[0][1] = 1000;
        costMat[0][grayWidth] = 1000;

        // calculate gradient values for the first line (base case)
        int offset = 0, dx, dy;
        for (int x = 1; x < grayWidth - 1; x++) {
            if (seamsMat[0][x] == 0) {
                dx = grayArr[0][x - 1] - grayArr[0][x + 1];
                dy = grayArr[0][x] - grayArr[0][x + 1];
                costMat[0][x + 1 + offset] = Math.abs(dx) + Math.abs(dy);
            } else
                offset--;
        }

        // dynamic programming part
        int left, mid, right;
        for (int y = 1; y < height; y++)
            for (int x = 1; x <= grayWidth; x++) {
                // assign
                left = costMat[y - 1][x - 1] + cl(y, x - 1);
                mid = costMat[y - 1][x] + cv(y, x - 1);
                right = costMat[y - 1][x + 1] + cr(y, x - 1);

                // check for minimum
                if (left <= mid && left <= right)
                    costMat[y][x] = left;
                else if (mid <= left && mid <= right)
                    costMat[y][x] = mid;
                else
                    costMat[y][x] = right;
            }
    }

    // Left seam
    private int cl(int y, int x) {
        if (x == 0 || x + 1 >= grayWidth) return HIGH_VALUE;
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]) + Math.abs(grayArr[y - 1][x] - grayArr[y][x - 1]);
    }

    // Right seam
    private int cr(int y, int x) {
        if (x == 0 || x + 1 >= grayWidth) return HIGH_VALUE;
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]) + Math.abs(grayArr[y - 1][x] - grayArr[y][x + 1]);
    }

    // Vertical seam
    private int cv(int y, int x) {
        if (x == 0 || x + 1 >= grayWidth) return HIGH_VALUE;
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]);
    }

    /**
     * This method shifts the picture one pixel right overwriting the marked pixel.
     * @param k amount of pixels to be shifted right.
     * @return an array representing the re-sized gray scaled image.
     */
    private int[][] shift(int k) {
        int[][] shifted = new int[height][grayArr[0].length - k];
        int offset;
        for (int y = 0; y < height; y++) {
            offset = 0;
            for (int x = 0; x < grayWidth; x++)
                if (seamsMat[y][x] == 0)
                    shifted[y][x + offset] = grayArr[y][x];
                else
                    offset--;
        }
        grayWidth--;
        return shifted;
    }
}
