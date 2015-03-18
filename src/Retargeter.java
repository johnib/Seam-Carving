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

    private final BufferedImage origPic; // working copy of the picture
    private int[][] grayEdit; // working copy of grayArr
    private int[][] seamsMat;
    private int[][] costMat;

    public Retargeter(BufferedImage m_img, boolean m_isVertical) {
        //TODO do initialization and pre processing here
        isVertical = m_isVertical;
        if (isVertical)
            origPic = ImageProc.transpose(m_img).getSubimage(0, 0, m_img.getHeight(), m_img.getWidth());
        else
            origPic = m_img.getSubimage(0, 0, m_img.getWidth(), m_img.getHeight());

        width = origPic.getWidth();
        height = origPic.getHeight();
        gray = ImageProc.grayScale(m_img);
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

    /**
     * This function is the actual size changer.
     * The procedure goes like this:
     * 1. validate input.
     * 2. calculate Seams Order Matrix, given the new size.
     * 3. create new BufferedImage with the new size (wider/shorter).
     * 4. for each pixel in the original picture:
     *  4.1 if its seamsMat value == 0 copy it to the picture.
     *  4.2 else copy it twice and increase offset.
     * @param newSize the wanted new width of the picture. oldSize - newSize = k seams.
     * @return new re-sized BufferedImage
     */
    public BufferedImage retarget(int newSize) {
        //TODO implement this
        if (newSize < 1 || newSize > 2 * width) {
            System.out.println("newSize is off limits.\nreturns original.");
            return origPic;
        }

        int k = newSize - width;
        calculateSeamsOrderMatrix(Math.abs(k));

        // from now on i assume that SeamsOrderMatrix is valid
        BufferedImage out = new BufferedImage(k, height, origPic.getType());
        int offset;
        for (int y = 0; y < height; y++) {
            offset = 0;
            for (int x = 0; x < width; x++) {
                if (seamsMat[y][x] == 0) // pixel not in seam
                    out.setRGB(x + offset, y, origPic.getRGB(x, y));
                else {
                    if (k > 0) {
                        out.setRGB(x + offset++, y, origPic.getRGB(x, y));
                        out.setRGB(x + offset, y, origPic.getRGB(x, y));
                    } else
                        offset--;
                }
            }
        }
        // remember to return the transpose if it's vertical
        return (isVertical) ? ImageProc.transpose(out) : out;
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
        //TODO avoid allocating memory for the costMat.
        // copy first line of grayScaled to the costMat.
        costMat = new int[height][width + 2];
        //TODO change to gradient pixels.
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

//    // gets the index of the i'th seam to be removed or duplicated.
//    private void shiftPic(int i) {
//        if (i > width || i < 1) {
//            System.out.println("shiftPic received wrong seam index.\nreturns.");
//            return;
//        }
//
//        BufferedImage out = new BufferedImage(width - 1, height, origPic.getType());
//        for (int y = 0; y < height; y++)
//            for (int x = 0; x < width; x++) {
//                if (seamsMat[y][x] != i) {
//                    out.setRGB(x, y, origPic.getRGB(x, y));
//                }
//            }
//    }
}
