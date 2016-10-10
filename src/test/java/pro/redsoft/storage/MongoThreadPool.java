package pro.redsoft.storage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import pro.redsoft.domain.FileInfo;


/**
 * Created by stail on 10/8/16.
 */
//@Component
public class MongoThreadPool implements Runnable {

    @Autowired
    @Qualifier("fileStorageGridFs")
    FileStorage fileStorage;

    private volatile static BlockingQueue<File> filesToSave = new LinkedBlockingQueue<>();
    private static ExecutorService executorService;
    private volatile static int filesInQueue;
    public volatile static boolean shouldTerminate;


    @Override
    public void run() {

        while (filesInQueue > 0 || !shouldTerminate) {
            File file = null;
            try {
                file = filesToSave.take();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            if (file != null) {
                this.submitTask(file);
            }
        }
    }

    static void init() {
        new Thread(new MongoThreadPool()).start();
    }

    static boolean addToQueue(File file) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(10);
            init();
        }

        try {
            filesToSave.put(file);
            filesInQueue++;
           // System.out.println(filesInQueue);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }


    private FileInfo save(File file) throws FileNotFoundException {

        FileInfo fileInfo = fileStorage.save(new FileInputStream(file), file.getName());
        System.out.println(file.getAbsolutePath());
        file.delete();

        return fileInfo;

    }

    private void submitTask(File file) {
        Callable<FileInfo> task = () -> {
            FileInfo fileInfo = null;
            if (file != null) {
                fileInfo = this.save(file);
                filesInQueue--;
            }
            return fileInfo;
        };
        executorService.submit(task);
    }
}
