package jo.aspire.task.dao;


import jo.aspire.task.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeDAO {

    void saveAll(Iterable<EmployeeDTO> employeeIterator);

    EmployeeDTO save(EmployeeDTO employee);

    List<EmployeeDTO> findAll();

}
