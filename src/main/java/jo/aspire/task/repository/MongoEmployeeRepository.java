package jo.aspire.task.repository;

import jo.aspire.task.entities.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoEmployeeRepository extends MongoRepository<EmployeeDocument, String> {

}
