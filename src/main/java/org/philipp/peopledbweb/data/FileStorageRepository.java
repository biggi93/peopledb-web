package org.philipp.peopledbweb.data;

import org.philipp.peopledbweb.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
public class FileStorageRepository {
    @Value("${STORAGE_FOLDER}") // Erzeugte eine Environment/ System Variable STORAGE_FOLDER, welche storageFolder setzt
    private String storageFolder; // Kann dadurch über eine Setting Datei gesetzt werden
    // Die ENV Var kann man über IntelliJ bspw. mitgeben ... Alt Shift F10

    public void save(String originalFilename, InputStream inputStream) {
        try {
            Path filePath = Path.of(storageFolder).resolve(originalFilename).normalize();
            Files.copy(inputStream, filePath);

        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public Resource findByName(String filename) {
        Path filePath = Path.of(storageFolder).resolve(filename).normalize();
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new StorageException(e);
        }
    }

    public void deleteAllByName(Collection<String> filenames) {
        try {
            // Null Werte Überspringen
            Set<String> existingFilenames = filenames.stream().filter(f -> f != null).collect(Collectors.toSet());

            for (String filename : existingFilenames) {
                Path filePath = Path.of(storageFolder).resolve(filename).normalize();
                Files.deleteIfExists(filePath);
                // Tritt bei 4. von 6 die Exception auf, dann sind alle 3 vorherigen trotzdem gelöscht!
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }
}
