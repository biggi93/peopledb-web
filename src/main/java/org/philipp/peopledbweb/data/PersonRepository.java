package org.philipp.peopledbweb.data;

import lombok.extern.log4j.Log4j2;
import org.philipp.peopledbweb.biz.model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CrudRepository<Person, Long> {
    // Spring liefert per Runtime alle CRUD Methoden durch dieses Interface!

    // Möglichkeit, seine eigene Query zu Verfügung zu stellen
    @Query(nativeQuery = true, value = "select photo_file_name from person where id in :ids")
    public Set<String> findFilenamesByIds(@Param("ids") Iterable<Long> ids);

}
