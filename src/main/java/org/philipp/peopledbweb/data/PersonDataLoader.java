package org.philipp.peopledbweb.data;

import org.philipp.peopledbweb.biz.model.Person;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// @Component // Wenn man @Component auskommentiert, wird diese Klasse nicht berücksichtigt
// Diese Klasse wurde nur verwendet, um Beispieldaten auf die Seite people zu bringen
public class PersonDataLoader implements ApplicationRunner {

    private PersonRepository personRepository;

    public PersonDataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Um Tabelle einmalig mit Daten zu befüllen
        if (personRepository.count() == 0) {
            List<Person> people = List.of(
//                    new Person(null, "Jake", "Snakakaka", LocalDate.of(1950, 1, 1), "dummy@sample.com", new BigDecimal("100000")),
//                    new Person(null, "Tom", "Smiththth", LocalDate.of(1960, 1, 1), "dummy@sample.com", new BigDecimal("200000")),
//                    new Person(null, "Anna", "Tjicic", LocalDate.of(1970, 1, 1), "dummy@sample.com", new BigDecimal("150000")),
//                    new Person(null, "John", "Akira", LocalDate.of(1980, 1, 1), "dummy@sample.com", new BigDecimal("180000")),
//                    new Person(null, "Djana", "Tranda", LocalDate.of(1990, 1, 1), "dummy@sample.com", new BigDecimal("190000"))

            );

            personRepository.saveAll(people);
        }
    }
}
