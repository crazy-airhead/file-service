package pro.redsoft.storage;

import com.mongodb.gridfs.GridFSDBFile;
import pro.redsoft.domain.FileInfo;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;


public interface FileStorage {

    FileInfo save(InputStream content, String fileName);


    Optional<InputStream> read(String fileId);


    Stream<GridFSDBFile> getAll();

}
