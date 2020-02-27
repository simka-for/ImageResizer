
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;

public class Main {
    private static final String srcFolder = "D:/image/source1";
    private static final String dstFolder = "D:/image/result1";
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private static Queue<File> filesQueue = new ConcurrentLinkedQueue<>();
    private static int newWidth = 300;
    private static long start = System.currentTimeMillis();

    public static void main(String[] args) throws InterruptedException {

        File srcDir = new File(srcFolder);
        filesQueue.addAll(Arrays.asList(Objects.requireNonNull(srcDir.listFiles())));


        for (int i = 0; i < processorCoreCount; i++) {
            new Thread(new ImageResizer(filesQueue, newWidth, dstFolder, start)).start();
        }

//        ExecutorService executorService = Executors.newFixedThreadPool(processorCoreCount);

//        executorService.submit(new ImageResizer(filesQueue, newWidth, dstFolder, start));
//        executorService.shutdown();
//        executorService.awaitTermination(1, TimeUnit.DAYS);
    }
}
