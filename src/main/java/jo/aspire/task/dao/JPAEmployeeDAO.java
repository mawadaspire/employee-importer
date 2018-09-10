package jo.aspire.task.dao;

import jo.aspire.task.dto.DownloadFileData;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.AddressEntity;
import jo.aspire.task.entities.EmployeeEntity;
import jo.aspire.task.repository.JPAEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("rdbms")
public class JPAEmployeeDAO implements EmployeeDAO {

    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Override
    public void saveAll(Iterable<EmployeeDTO> employeeIterator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmployeeDTO> save(EmployeeDTO employee) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<List<EmployeeDTO>> findAll(int pageNumber, int pageSize) {
        List<EmployeeEntity> employeesList = jpaEmployeeRepository.findAll();
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        employeesList.forEach(e -> employeeDTOS.add(createDTO(e)));
        if (!employeeDTOS.isEmpty())
            return Optional.of(employeeDTOS);
        return Optional.empty();
    }

    @Override
    public Optional<List<DownloadFileData>> findAllToDownload() {
        List<EmployeeEntity> employeesList = jpaEmployeeRepository.findAll();
        List<DownloadFileData> employeeToDownload = new ArrayList<>();
        employeesList.forEach(e -> employeeToDownload.add(new DownloadFileData(e.getEmployeeName(),e.getSalary(),e.getSalary()*12)));
        if (!employeeToDownload.isEmpty())
            return Optional.of(employeeToDownload);
        return Optional.empty();
    }

    private EmployeeDTO createDTO(EmployeeEntity employeeEntity) {
        EmployeeDTO result = new EmployeeDTO();
        result.setAddressesList(createAddressesList(employeeEntity.getAddressEntities()));
        result.setBirthDate(employeeEntity.getBirthDate());
        result.setDegree(employeeEntity.getDegree());
        result.setEmployeeId(employeeEntity.getEmployeeId());
        result.setEmployeeName(employeeEntity.getEmployeeName());
        result.setSalary(employeeEntity.getSalary());
        result.setStatus(employeeEntity.getStatus());
        return result;
    }

    private List<String> createAddressesList(List<AddressEntity> address) {
        List<String> result = new ArrayList<>();
        address.forEach(a -> result.add(a.getAddress()));
        return result;
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeId(long employeeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeName(String employeeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<List<EmployeeDTO>> findByDegree(String degree) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<List<EmployeeDTO>> findByStatus(String status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Double> getYearlySalary(long employeeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Long> getAgeById(long employeeId) {
        throw new UnsupportedOperationException();
    }

}
