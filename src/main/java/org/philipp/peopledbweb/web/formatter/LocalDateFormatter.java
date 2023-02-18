package org.philipp.peopledbweb.web.formatter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
// Diese Spring Annotation kombiniert mit Spring Formatter sorgt dafür
// ,dass die Annotation alle Beans (Klassen) im Controller durchsucht, die eine LocalDate haben
// , und dieses über die print Methode angepasst wird
// , oder bei Eingabe von Daten durch parse geparsed wird
public class LocalDateFormatter implements Formatter<LocalDate> {

    //private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd. MMMM, yyyy"); // Funktioniert nur mit thymeleaf-extras-java8time:3.0.4.RELEASE
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text,dateTimeFormatter);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return dateTimeFormatter.format(object);
    }
}
