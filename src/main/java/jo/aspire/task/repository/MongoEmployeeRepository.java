package jo.aspire.task.repository;

import jo.aspire.task.entities.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoEmployeeRepository extends MongoRepository<EmployeeDocument, String> {

    EmployeeDocument findByEmployeeId(long employeeId);

    EmployeeDocument findByEmployeeName(String employeeName);

    List<EmployeeDocument> findByDegree(String degree);

    List<EmployeeDocument> findByStatus(String status);
}
