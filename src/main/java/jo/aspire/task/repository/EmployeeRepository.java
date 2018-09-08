package jo.aspire.task.repository;

import jo.aspire.task.entities.EmployeeInfo;

import java.util.Iterator;
import java.util.List;

public interface EmployeeRepository {

    void saveAll(Iterator<EmployeeInfo> employeeIterator);

    EmployeeInfo save(EmployeeInfo employee);

    List<EmployeeInfo> findAll();

    EmployeeInfo findByFirstName();

    EmployeeInfo findByLastName();

}
