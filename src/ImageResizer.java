import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;

public class ImageResizer implements Runnable{

    private int newWidth;
    private long start;
    private String dstFolder;
    private Queue<File> files;

    public ImageResizer(Queue<File> files, int newWidth, String dstFolder, long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.start = start;
        this.dstFolder = dstFolder;
    }

    @Override
    public void run() {
        int width;
        File file;
        try {
            while (files.peek() != null) {
                file = files.poll();
                BufferedImage image = ImageIO.read(file);
                BufferedImage newImage;
                if (image == null) {
                    continue;
                }
                if (image.getWidth() >= (newWidth * 4)) {
                    width = 4 * newWidth;
                    BufferedImage tempImageX4 = neighbor(image, width);
                    width /= 2;
                    BufferedImage tempImage = neighbor(tempImageX4, width);
                    width = newWidth;
                    newImage = neighbor(tempImage, width);
                } else if (image.getWidth() >= (newWidth * 2)) {
                    width = 2 * newWidth;
                    BufferedImage tempImage = neighbor(image, width);
                    width = newWidth;
                    newImage = neighbor(tempImage, width);
                } else {
                    newImage = neighbor(image, newWidth);
                }
                createFile(newImage, file.getName());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("ImageResize completed: " + (System.currentTimeMillis() - start) + " ms");
    }

    private BufferedImage neighbor(BufferedImage image, int width) throws IOException {
        int height = (int) Math.round(
                image.getHeight() / (image.getWidth() / (double) width)
        );
        BufferedImage newImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB
        );
        double widthStep = (double) image.getWidth() / width;
        double heightStep = (double) image.getHeight() / height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB((int) (x * widthStep), (int) (y * heightStep));
                newImage.setRGB(x, y, rgb);
            }
        }
        return newImage;
    }

    private void createFile(BufferedImage image, String fileName) throws IOException {
        Files.deleteIfExists(Path.of(dstFolder + "/" + fileName));
        File newFile = new File(dstFolder + "/" + fileName);
        ImageIO.write(image, "jpg", newFile);
    }
}
