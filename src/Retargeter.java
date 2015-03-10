import java.awt.image.BufferedImage;

public class Retargeter {

    private final int width;
    private final int height;
    private final boolean isVertical;

    private final BufferedImage gray; // a copy of grey scaled image.
    private int[][] grayArr; // array of grey scaled values.

    // this is the matrix where each pixel knows to which i'th seam it belongs.
    private int[][] seams;
    private int[][] costMat;

    public Retargeter(BufferedImage m_img, boolean m_isVertical) {
        //TODO do initialization and preprocessing here
        width = m_img.getWidth();
        height = m_img.getHeight();
        isVertical = m_isVertical;

        gray = ImageProc.grayScale(m_img);
        grayArr = new int[height][width];
        seams = new int[height][width];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                grayArr[x][y] = gray.getRGB(x, y) & 0xFF;
                seams[x][y] = Integer.MAX_VALUE;
            }

        calculateSeamsOrderMatrix();
    }

    public void getSeamsOrderMatrix() {
        //you can implement this (change the output type)
    }

    public void getOrigPosMatrix() {
        //you can implement this (change the output type)
    }

    public BufferedImage retarget(int newSize) {
        //TODO implement this
        return null;
    }

    private void calculateSeamsOrderMatrix() {
        //TODO implement this - this calculates the order in which seams are extracted

        calculateCostsMatrix(width);

    }

    private void calculateCostsMatrix(int w) {
        //TODO implement this - cost matrix should be calculated for a given image width w
        // 	   to be used inside calculateSeamsOrderMatrix()

        costMat = new int[height][w + 2]; // with margins

        // copy first line of gray image to the temp array.
        System.arraycopy(grayArr[0], 0, costMat[0], 1, w);

        // set the margins to 1000 so they won't be chosen as the minimum.
        for (int y = 0; y < height; y++) {
            costMat[y][0] = 1000;
            costMat[y][w + 1] = 1000;
        }

        // fill the matrix from the second line according to the formula.
        for (int y = 1; y < height; y++)
            for (int x = 1; x <= w; x++)
                costMat[y][x] = grayArr[y][x - 1] + Math.min(costMat[y - 1][x - 1], Math.min(costMat[y - 1][x], costMat[y - 1][x + 1]));
    }

}
