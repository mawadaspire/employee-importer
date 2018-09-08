package jo.aspire.task.dao;

import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.repository.JPAEmployeeRepository;
import jo.aspire.task.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("rdbms")
public class JPAEmployeeDAO implements EmployeeDAO {

    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Override
    public void saveAll(Iterable<EmployeeDTO> employeeIterator) {
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employee) {
        return null;
    }

    @Override
    public List<EmployeeDTO> findAll() {
       return null;
    }

}
