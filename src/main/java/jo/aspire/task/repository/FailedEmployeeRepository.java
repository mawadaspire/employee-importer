package jo.aspire.task.repository;

import jo.aspire.task.entities.FailedEmployees;
import org.springframework.data.repository.CrudRepository;

public interface FailedEmployeeRepository extends CrudRepository<FailedEmployees, Long> {
    FailedEmployees findByEmployeeData(String employeeData);
}
