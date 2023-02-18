package org.philipp.peopledbweb.biz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// lombok.Data erzeugt automatisch alle Getter und Setter und Hashcode etc. im Hintergrund!
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {
    @Id // Um Id für Spring Data zu markieren
    @GeneratedValue // Hybernate Framework, welches sich um Datenhaltung kümmert
    private Long id;

    // Annotation, welche prüft, dass Werte nicht null sind
    @NotEmpty(message = "First Name can not be empty")
    //@Pattern(regexp = "", message = "My message") // Für eigene Regex Regeln
    private String firstName;
    @NotEmpty(message = "Last Name can not be empty")
    private String lastName;

    // Annotation, welche prüft, dass Alter in der Vergangenheit liegt
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth must be specified")
    private LocalDate dob;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @DecimalMin(value = "1000.0", message = "Salary must be at least 1000 Euro")
    @NotNull(message = "Salary can not be empty")
    private BigDecimal salary;

    private String photoFileName;


    public static Person parse(String csvLine) {
        String[] fields = csvLine.split(",\\s*");
        LocalDate dob = LocalDate.parse(fields[10], DateTimeFormatter.ofPattern("M/d/yyyy"));
        return new Person(null, fields[2], fields[4], dob, fields[6], new BigDecimal(fields[25]), null);
    }

}
