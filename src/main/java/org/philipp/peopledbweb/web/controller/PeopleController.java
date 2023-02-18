package org.philipp.peopledbweb.web.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.philipp.peopledbweb.biz.model.Person;
import org.philipp.peopledbweb.biz.service.PersonService;
import org.philipp.peopledbweb.data.FileStorageRepository;
import org.philipp.peopledbweb.data.PersonRepository;
import org.philipp.peopledbweb.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Controller
// Um Spring zu zeigen, dass es sich um controller / web stuff handelt.
@RequestMapping("/people")
// Jede Anfrage, die mit /people kam, soll mit dieser class gemapped werden
// d.h. Rufe ich localhost:8080/people auf, erhalte ich den Inhalt dieser Klasse
@Log4j2
// Für das Logging
public class PeopleController {

    public static final String DISPO = """
            attachment; filename="%s"
            """;
    private PersonRepository personRepository;
    private FileStorageRepository fileStorageRepository;
    private PersonService personService;

    // Wichtiger und einziger Konstruktor für Dependency Injection (dass dieser Controller personRepo braucht)
    // --> welche dann durch Spring gemanaged wird
    public PeopleController(PersonRepository personRepository, FileStorageRepository fileStorageRepository, PersonService personService) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
        this.personService = personService;
    }

    // Alle Daten, die dem Model zu Verfügung gestellt werden, können im HTML genutzt werden
    // In table über thymeleaf bspw. mit th:each="person : ${people}"
    // Wodurch man dann auf Iterable<Person> zugreifen kann
    @ModelAttribute("people")
    // Diese Methode liefert ein Page<Person> Objekt zurück in das Model, welches dort "people" heißt
    // Im html kann man dann auf das Page Objekt Model mit all seinen Funktionen per Thymeleaf zugreifen
    public Page<Person> getPeople(@PageableDefault(size = 8) Pageable page) {
        return personService.findAll(page);
    }

    @ModelAttribute
    // Folgende Methode ermöglicht, dass man im people.html mit Thymeleaf auf ein person Objekt zugreifen kann
    // Bzw. dieses neu erzeugt werden kann, über das Formular
    // In Formular: Im Formular th:object="${person}";Im Attribut th:field="*{firstName}"
    public Person getPerson() {
        Person person = new Person();
        return person;
    }

    @GetMapping
    // Diese Methode dient für HTTP GET Anfragen über Browser, um zu bestimmen, was ausgegeben wird
    public String showPeoplePage() {
        // Dieses return "people" verweist auf resources/templates/people.html!
        // Also Ziel dieser Methode, wenn Get auf /people kommt, rufe people.html auf
        // WICHTIG-> Dabei werden zuerst alle Daten des Models -> siehe @ModelAttribut("people") einbezogen
        // Und diese an Model model übergeben
        // Theoretisch stände hier, ohne @ModelAttribut:
        /*
         public String showPeoplePage(Model model) {
         model.addAttribut("people",getPeople());
         return "people" }
        */
        // Wodurch dem HTML /Thymeleaf letztlich die dynamischen Elemente geliefert werden
        return "/people"; // return view-file -> people.html in resources
    }

    @GetMapping(path = "/images/{picResource}")
    // Mapped ein Get Request mit dem Path people/images/{resource} -> was der get Anfrage für dem Picture entspricht
    // , wobei {resource} ein Path Variable ist, welche man via @PathVariable definiert über Methode
    public ResponseEntity<Resource> getResource(@PathVariable("picResource") String resource) {
        // Eigene Response erzeugen, die HTTP Response mit Bild zurücksendet
        return ResponseEntity
                .ok() // Zuerst immer http code 200 -> ok senden
                .header(HttpHeaders.CONTENT_DISPOSITION, format(DISPO, resource)) // header hinzufügen, der attachment/ picture erlaubt
                .body(fileStorageRepository.findByName(resource)); // Bytes des Files
    }

    @PostMapping(params = "action=save")
    // Wenn auf Seite people die post Methode verwendet wird, und einen POST Request mit name="action" value="safe"-> action=save enthält, dann führt er diese Methode aus
    public String savePerson(Model model, @Valid Person person, Errors errors, @RequestParam("photoFileName") MultipartFile photoFile) throws IOException {
        // Person kommt über getPerson() Methode
        // --> Wenn Formular submitted wird, dann werden die Attribute zu person gemapped und hier zu Person Objekt
        // @Valid prüft, dass Annotation Rules aus Person erfüllt sind
        // @RequestParam MultipartFile photoFile -> Spring prüft, ob über Post ein photoFile mitgesendet wurde
        // -> entspricht dem Varname unseres Upload-Files
        log.info(person); // Log4j
        log.info("Filename" + photoFile.getOriginalFilename());
        log.info("Filesize" +photoFile.getSize());
        log.info("Errors: " + errors);
        if (!errors.hasErrors()) { // Wenn Validation Rules passen, dann speichere die eingegebene Person aus Eingabe people html
            try {
                // Die SAVE Methode von Spring Data bzw. Hybernate prüft, ob ein Objekt bereits eine ID hat, oder nicht
                // Wenn nein, dann geschieht ein INSERT, Wenn ja, dann ein Update
                personService.save(person, photoFile.getInputStream());
                return "redirect:people"; // Damit die Seite people neu geladen wird und Eingabe Daten verschwinden
            } catch (StorageException e) {
                model.addAttribute("errorMsg", "System is currently unable to accept photo files at this time.");
                return "people";
            }
        }
        else {
            return "people";
            // Error Message in people.html als Element hinzufügbar
            // Dafür Thymeleaf th:errorclass="is-invalid" und th:errors="*{firstName} in people.html verwenden
        }

    }

    // Andere Möglichkeit, wie man mit Fehlern umgehen kann -> bspw. StorageException
//    @ExceptionHandler(StorageException.class)
//    public String handleException(Model model) {
//       //blub
//    }


        @PostMapping(params = "action=delete")
        // Wenn ein Post Request mit name="action" value="delete" -> action=delete zurück kommt, dann führe Action aus
        // delete=true wird per Post gesendet, wenn der Delete Button geklickt wurde,
        // Wählt man bspw. Person mit ID 2 und 102 aus, und klickt den Delete Button, erhält man den Post Request:
        // selections=2&selections=102&delete=true
        public String deletePeople(@RequestParam Optional<List<Long>> selections) {
        // @RequestParam sorgt dafür, dass man den Inhalt des Request als Liste erhält, solange dieser gleich heißt
        // Also List.of(2,102) (weil: Requestbody: selections=2&selections=102&delete=true)
        // Optional List, damit keine Exception erscheint, wenn keine Selection ausgewählt wurde
        // ! WENN die Elemente im Request anders heißen, bspw checkselect=2&checkselect=102&delete=true
        // DANN muss man in Annotation @@RequestParam("checkselect") angeben, wenn man es in der Funktion als selections verwenden will
            log.info(selections);
            if (selections.isPresent()) {
                personService.deleteAllById(selections.get());
            }
            return "redirect:people";
        }

    @PostMapping(params = "action=edit")
    public String editPerson(@RequestParam Optional<List<Long>> selections, Model model) {
      log.info(selections);
        if (selections.isPresent()) {
            // Da mehrere Personen in people ausgewählt werden können, soll nur mit dem Ersten etwas gemacht werden
            // daher .get(0), was das erste Person Element aus der Liste selections ist
            Optional<Person> person = personRepository.findById(selections.get().get(0));

            // Folgende Methode übergibt people Seite die gefundene Person als Model
            // , wodurch man mit Thymeleaf wieder darauf zugreifen kann
            model.addAttribute("person", person);
        }
        // Die Seite wird dann mit dem Formular geladen, aus den Model person Daten
        // SAVE kann dann sowohl zum inserten, wie auch zum update verwendet werden
        // WENN ID vorhanden ist, dann performed save nämlich ein update
        // Hierfür hat man ein NICHT sichtbares ID Feld im Formular, welches durch model.addAttribute auch "heimlich" befüllt wird
        // ... mit der ID, welche vorher ausgewählt wurde
        return "people";
    }

    @PostMapping(params = "action=import")
    public String importCSV(MultipartFile csvFile, Model model) {
        log.info("File name: " + csvFile.getOriginalFilename());
        log.info("File size: " + csvFile.getSize());
        try {
            personService.importCSV(csvFile.getInputStream());
            model.addAttribute("csvSuccess", "Your CSV Mass Upload was successful! Please refresh to see results");

        } catch (IOException e) {
            throw new StorageException();
        }
        return "people";

    }

}
