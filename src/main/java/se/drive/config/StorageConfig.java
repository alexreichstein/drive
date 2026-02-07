package se.drive.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration för fillagring.
 * Läser in sökvägen till lagringskatalogen från application.properties.
 */
@Getter
@Configuration
public class StorageConfig {

    /**
     * Rotkatalog där uppladdade filer sparas.
     * Definieras i application.properties som "storage.root"
     */
    @Value("${storage.root}")
    private String rootPath;
}