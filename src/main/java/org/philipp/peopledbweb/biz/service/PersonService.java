package org.philipp.peopledbweb.biz.service;

import org.philipp.peopledbweb.biz.model.Person;
import org.philipp.peopledbweb.data.FileStorageRepository;
import org.philipp.peopledbweb.data.PersonRepository;
import org.philipp.peopledbweb.exception.StorageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final FileStorageRepository fileStorageRepository;

    public PersonService(PersonRepository personRepository, FileStorageRepository fileStorageRepository) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    // Folgende Methoden sind über ALT + EINFG als Delegate Methods erstellt worden
    // Diese sind 'Durchlauferhitzer' basierend der orginalen Repository Methoden
    @Transactional // sorgt dafür, dass nur wenn alles(gesamte Methode) erfolgreich war, die Person in DB committed wird
    // Also muss auch der FileUpload erfolgreich sein, damit Datenbank committed wird
    public Person save(Person person, InputStream photoStream) {

        try {
            if (photoStream.available() != 0) {
                fileStorageRepository.save(person.getPhotoFileName(), photoStream);


            } else {
                person.setPhotoFileName(null);
            }
        } catch (IOException e) {
            throw new StorageException();
        }
        Person savedPerson = personRepository.save(person);
        return savedPerson;
    }



    public Optional<Person> findById(Long aLong) {
        return personRepository.findById(aLong);
    }

    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }


    public void deleteAllById(Iterable<Long> ids) {
//        Iterable<Person> peopleToDelete = personRepository.findAllById(ids);
//        Stream<Person> peopleStream = StreamSupport.stream(peopleToDelete.spliterator(), false);
//        Set<String> filenames = peopleStream
//                .map(Person::getPhotoFileName)
//                .collect(Collectors.toSet());
        // Leichter mit eigener Crud Methode bzw. eigener Abfrage
        Set<String> filenames = personRepository.findFilenamesByIds(ids);

        personRepository.deleteAllById(ids);
        fileStorageRepository.deleteAllByName(filenames);
    }


    public void importCSV(InputStream csvFileStream) {
        // Use Case, dass dies ein ZIP File ist

        try {
            // Als ZIP erfassen
            ZipInputStream zipInputStream = new ZipInputStream(csvFileStream);
            zipInputStream.getNextEntry();
            // Um von Byte Stream auf Char Stream zu konvertieren
            InputStreamReader inputStreamReader = new InputStreamReader(zipInputStream);
            // Um die Daten in Stream of Lines zu konvertieren
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            bufferedReader.lines()
                    .skip(100)
                    .limit(20)
                    .map(Person::parse)
                    .forEach(personRepository::save);
        } catch (IOException e) {
            throw new StorageException();
        }
    }
}
