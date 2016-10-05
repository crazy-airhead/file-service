package pro.redsoft.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private Environment environment;

    @Override
    protected String getDatabaseName() {
        return environment.getProperty("mongo.db.name");
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(environment.getProperty("mongo.db.url"));
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }
}

