package jo.aspire.task.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.FailedEmployees;
import jo.aspire.task.repository.FailedEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("failedItems")
public class FailedItemsDataMigrater implements DataMigrater {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedItemsDataMigrater.class);


    @Autowired
    private FailedEmployeeRepository failedEmployeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void migrate(EmployeeDAO from, EmployeeDAO to) {
        Optional<List<EmployeeDTO>> allDocuments = from.findNotMigratedRecords();
        if (allDocuments.isPresent()) {
            List<EmployeeDTO> employeeDTOS = allDocuments.get();
            employeeDTOS.forEach(emp -> {
                try {
                    String empJson = objectMapper.writeValueAsString(emp);
                    FailedEmployees byEmployeeData = failedEmployeeRepository.findByEmployeeData(empJson);
                    if (Objects.nonNull(byEmployeeData)) {
                        EmployeeDTO employeeDTO = objectMapper.readValue(empJson, EmployeeDTO.class);
                        Optional<EmployeeDTO> save = to.save(employeeDTO);
                        save.ifPresent(employeeDTO1 -> {
                            from.updateIsMigrated(true, employeeDTO1.getEmployeeId());
                            failedEmployeeRepository.deleteById(byEmployeeData.getId());

                        });
                    }
                } catch (Exception e) {
                    LOGGER.error("Error While Migrating Failed Record " + emp.toString());
                    e.printStackTrace();
                }
            });
        }
    }
}
