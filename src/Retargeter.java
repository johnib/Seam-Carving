import java.awt.image.BufferedImage;

/**
 * This class is implemented a bit different, with slightly different signatures.
 * The procedure from creating a new Retargeter to return the retargeted image is as follows:
 *
 * 1. Set parameters, transpose the picture if needed (when not vertical).
 * 2. Calculate the gray scaled image.
 * 3. Repeat k times:
 *  a. Calculate cost matrix.
 *  b. Set the seam's pixels to k in the seamsMat
 *  c. Update the used pixels to high value in the gray scacle
 *      (this way they won't be picked up again on next seams).
 * 4. Cut or duplicate the k seams.
 * 5. return retargeted picture.
 */

public class Retargeter {

    private final int width;
    private final int height;
    private final boolean isVertical;
    private final BufferedImage gray; // a copy of gray scaled image.
    private final int[][] grayArr; // non-touchable array of gray scaled values

    private final BufferedImage pic; // working copy of the picture
    private int[][] grayEdit; // working copy of grayArr
    private int[][] seamsMat;
    private int[][] costMat;

    public Retargeter(BufferedImage m_img, boolean m_isVertical) {
        //TODO do initialization and pre processing here

        isVertical = m_isVertical;
        if (m_isVertical) {
            width = m_img.getHeight();
            height = m_img.getWidth();
            gray = ImageProc.transpose(ImageProc.grayScale(m_img));
        } else {
            width = m_img.getWidth();
            height = m_img.getHeight();
            gray = ImageProc.grayScale(m_img);
        }

        pic = m_img.getSubimage(0, 0, width, height);
        grayArr = new int[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                grayArr[y][x] = gray.getRGB(x, y) & 0xFF;
    }

    public void getSeamsOrderMatrix() {
        //you can implement this (change the output type)
    }

    public void getOrigPosMatrix() {
        //you can implement this (change the output type)
    }

    public BufferedImage retarget(int newSize) {
        //TODO implement this
        if (newSize < 1 || newSize > 2 * width) {
            System.out.println("newSize is off limits.\nreturns original.");
            return pic;
        }

        calculateSeamsOrderMatrix(Math.abs(newSize - width));

        // remember to return the transpose if it's vertical
        return null;
    }

    private void calculateSeamsOrderMatrix(int k) {
        //TODO implement this - this calculates the order in which seams are extracted
        int x, y, min, tempX;
        for (int i = 1; i <= k; i++) {
            // calculate the cost matrix
            calculateCostsMatrix();

            // find min seam's end-pixel coordinate
            x = 0; y = height - 1; min = costMat[y][x];
            for (int j = 0; j < width; j++)
                if (min > costMat[y][j]) {
                    x = j;
                    min = costMat[y][j];
                }

            // mark whole seam in seams matrix
            for (; y > 0; y--) {
                seamsMat[y][x] = i;
                tempX = (costMat[y - 1][x - 1] < costMat[y - 1][x]) ? x - 1 : x;
                tempX = (costMat[y - 1][x + 1] < costMat[y - 1][tempX]) ? x + 1 : tempX;
                x = tempX;

                // 'disable' the seam's pixels
                grayArr[y][x] = Integer.MAX_VALUE;
            }
            seamsMat[y][x] = i;
            grayArr[y][x] = Integer.MAX_VALUE;
        }
    }

    private void calculateCostsMatrix() {
        //TODO implement this - cost matrix should be calculated for a given image width w

        // copy first line of grayScaled to the costMat.
        costMat = new int[height][width + 2];
        System.arraycopy(grayArr[0], 0, costMat[0], 1, width);

        // wrap shoulders with high value.
        for (int y = 0; y < height; y++) {
            costMat[y][0] = Integer.MAX_VALUE;
            costMat[y][width - 1] = Integer.MAX_VALUE;
        }

        int left, mid, right;
        for (int y = 1; y < height; y++)
            for (int x = 1; x <= width; x++) {
                // assign
                left = costMat[y - 1][x - 1];
                mid = costMat[y - 1][x];
                right = costMat[y - 1][x + 1];

                // check for minimum
                if (left <= mid && left <= right)
                    costMat[y][x] = left + cl(x, y);
                else if (mid <= left && mid <= right)
                    costMat[y][x] = mid + cv(x, y);
                else
                    costMat[y][x] = right + cr(x, y);
            }
    }

    // Left seam
    private int cl(int y, int x) {
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]) + Math.abs(grayArr[y - 1][x] - grayArr[y][x - 1]);
    }

    // Right seam
    private int cr(int y, int x) {
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]) + Math.abs(grayArr[y - 1][x] - grayArr[y][x + 1]);
    }

    // Vertical seam
    private int cv(int y, int x) {
        return Math.abs(grayArr[y][x + 1] - grayArr[y][x - 1]);
    }

    // gets the index of the i'th seam to be removed or duplicated.
    private void shiftPic(int i) {
        if (i > width || i < 1) {
            System.out.println("shiftPic received wrong seam index.\nreturns.");
            return;
        }

        BufferedImage out = new BufferedImage(width - 1, height, pic.getType());
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                if (seamsMat[y][x] != i) {
                    out.setRGB(x, y, pic.getRGB(x, y));
                }
            }
    }
}
