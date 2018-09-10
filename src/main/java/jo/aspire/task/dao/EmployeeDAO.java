package jo.aspire.task.dao;


import jo.aspire.task.dto.DownloadFileData;
import jo.aspire.task.dto.EmployeeDTO;

import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {

    void saveAll(Iterable<EmployeeDTO> employeeIterator);

    Optional<EmployeeDTO> save(EmployeeDTO employee);

    Optional<List<EmployeeDTO>> findAll(int pageNumber, int pageSize);

    Optional<List<DownloadFileData>> findAllToDownload();

    Optional<EmployeeDTO> findByEmployeeId(long employeeId);

    Optional<EmployeeDTO> findByEmployeeName(String employeeName);

    Optional<List<EmployeeDTO>> findByDegree(String degree);

    Optional<List<EmployeeDTO>> findByStatus(String status);

    Optional<Double> getYearlySalary(long employeeId);

    Optional<Long> getAgeById(long employeeId);

}
