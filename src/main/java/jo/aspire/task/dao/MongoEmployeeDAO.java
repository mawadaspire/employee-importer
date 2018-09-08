package jo.aspire.task.dao;

import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.repository.MongoEmployeeRepository;
import jo.aspire.task.entities.EmployeeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("mongo")
public class MongoEmployeeDAO implements EmployeeDAO {

    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;


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
