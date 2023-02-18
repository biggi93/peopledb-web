package org.philipp.peopledbweb.biz.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    public void canParse() {
        String csvLine = "671135,Ms.,Argentina,O,Hern,F,argentina.hern@ntlworld.com,Earl Hern,Chrissy Hern,Tapley,10/15/1962,02:08:22 AM,57.92,53,4/1/1986,Q2,H1,1986,4,April,Apr,1,Tuesday,Tue,34.44,129174,8%,083-02-3078,202-572-0136,Washington,District of Columbia,Washington,DC,20411,South,aohern,vsUv&nFc?lM|2-";

        Person person = Person.parse(csvLine);

        assertThat(person.getDob()).isEqualTo(LocalDate.of(1962,10,15));

    }

}