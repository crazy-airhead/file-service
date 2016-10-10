package pro.redsoft.storage;

import com.mongodb.gridfs.GridFSDBFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import pro.redsoft.domain.FileInfo;

@Component
public class TempStorageImpl implements FileStorage {

    public static List<File>  listOfFiles = new LinkedList<>();

    @Override
    public FileInfo save(InputStream content, String fileName) {

        File file = createTempFile(content, fileName);
        FileInfo fileInfo = new FileInfo();

        fileInfo.setFilename(fileName);
        //MongoThreadPool.addToQueue(file);
        listOfFiles.add(file);
        System.out.println(listOfFiles.size());

        return fileInfo;
    }


    private File createTempFile(InputStream content, String fileName) {
        File file = null;
        OutputStream outputStream;

        try {
            file = File.createTempFile(fileName, ".tmp");
            outputStream = new FileOutputStream(file);
            IOUtils.copy(content, outputStream);
            //this.writeToFile(content,outputStream);
            outputStream.flush();
            content.close();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return file;
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

    private void writeToFile(InputStream inputStream,OutputStream outputStream){
        byte[] buffer = new byte[1024];

        int i=0;
        try {
            while (i!=-1){
                i = inputStream.read(buffer);
                outputStream.write(buffer);
                outputStream.flush();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

    }

}
