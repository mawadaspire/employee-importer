package jo.aspire.task.repository;

import jo.aspire.task.entities.JsonFileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAJsonFileRepository extends CrudRepository<JsonFileEntity,Long> {
}
