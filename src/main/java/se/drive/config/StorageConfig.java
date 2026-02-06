package se.drive.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StorageConfig {

    @Value("${storage.root}")
    private String rootPath;

}