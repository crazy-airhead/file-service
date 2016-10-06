package pro.redsoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import pro.redsoft.storage.FileStorage;
import pro.redsoft.storage.FileStorageGridFs;


@Configuration
@PropertySource("classpath:/file-storage.properties")
@Import(MongoConfig.class)
public class FileStorageConfig {

    @Autowired
    MongoConfig mongoConfig;

    @Bean
    public FileStorage fileStorage() {
        try {
            return new FileStorageGridFs(mongoConfig.gridFsTemplate());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
