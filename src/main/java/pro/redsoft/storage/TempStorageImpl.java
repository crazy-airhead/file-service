package pro.redsoft.storage;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import pro.redsoft.domain.FileInfo;


public class TempStorageImpl implements FileStorage, Runnable{

    public static Queue<File> filesToSave = new LinkedBlockingQueue<>();
    public static int filesInQueue;
    public static boolean  shootDown;


    @Autowired
    private FileStorage fileStorage;


    public TempStorageImpl(FileStorage fileStorage){
        this.fileStorage = fileStorage;
    }


    public FileInfo save (InputStream content, String fileName) {

        File file = null;
        OutputStream outputStream;

        try {
            file =File.createTempFile(fileName,".tmp");
            outputStream = new FileOutputStream(file);
            IOUtils.copy(content,outputStream);
            filesInQueue+=1;
            content.close();
            outputStream.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        filesToSave.offer(file);

        return new FileInfo();
    }


    public void init(){
        new Thread(new TempStorageImpl(fileStorage));

    }


    @Override
    public void run(){
        while (!shootDown || (shootDown && filesInQueue>0)){
            File file = filesToSave.poll();

            if(file !=null){
                try {
                    this.saveToMongo(file);
                    filesInQueue -=1;
                }
                catch (FileNotFoundException ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    private void saveToMongo(File file) throws FileNotFoundException{

        fileStorage.save(new FileInputStream(file),file.getName());

        System.out.println(file.getAbsolutePath());
        file.delete();

    }

    @Override
    public Optional<GridFSDBFile> read(String fileId) {
        return null;
    }

    @Override
    public Stream<GridFSDBFile> getAll() {
        return null;
    }

    @Override
    public void deleteTestFiles(String fileName) {

    }
}
