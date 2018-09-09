package jo.aspire.task.dao;

import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.repository.JPAEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Qualifier("rdbms")
public class JPAEmployeeDAO implements EmployeeDAO {

    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Override
    public void saveAll(Iterable<EmployeeDTO> employeeIterator) {
    }

    @Override
    public Optional<EmployeeDTO> save(EmployeeDTO employee) {
        return null;
    }

    @Override
    public Optional<List<EmployeeDTO>> findAll(int pageNumber, int pageSize) {
       return null;
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeId(long employeeId) {
        return null;
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeName(String employeeName) {
        return null;
    }

    @Override
    public Optional<List<EmployeeDTO>> findByDegree(String degree) {
        return null;
    }

    @Override
    public Optional<List<EmployeeDTO>> findByStatus(String status) {
        return null;
    }

    @Override
    public Optional<Double> getYearlySalary(long employeeId) {
        return null;
    }

    @Override
    public  Optional<Long> getAgeById(long employeeId) {
        return null;
    }

}
