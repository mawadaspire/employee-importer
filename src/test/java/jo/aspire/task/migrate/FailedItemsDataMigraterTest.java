package jo.aspire.task.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jo.aspire.task.Application;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.FailedEmployees;
import jo.aspire.task.repository.FailedEmployeeRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableAutoConfiguration
public class FailedItemsDataMigraterTest {


    @Autowired
    @Qualifier("failedItems")
    private DataMigrater dataMigrater;


    @Autowired
    @Qualifier("rdbms")
    private EmployeeDAO rdbmsEmployeeDAO;


    @Autowired
    @Qualifier("mongo")
    private EmployeeDAO mongoEmployeeDAO;

    @Autowired
    private FailedEmployeeRepository failedEmployeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenAlreadyFailedRecordWhenTryToMigrateAgainAfterFixedThenShouldBeMoved() throws Exception {
        Optional<EmployeeDTO> save = mongoEmployeeDAO.save(createEmployee());
        FailedEmployees failedEmployees = failedEmployeeRepository.save(new FailedEmployees(objectMapper.writeValueAsString(save.get())));

        Optional<List<EmployeeDTO>> notMigratedRecords = mongoEmployeeDAO.findNotMigratedRecords();
        assertTrue(notMigratedRecords.isPresent());
        dataMigrater.migrate(mongoEmployeeDAO, rdbmsEmployeeDAO);
        Optional<List<EmployeeDTO>> notMigratedRecordsAfterMigration = mongoEmployeeDAO.findNotMigratedRecords();
        assertFalse(notMigratedRecordsAfterMigration.isPresent());
        assertFalse(failedEmployeeRepository.findAll().iterator().hasNext());

    }

    private EmployeeDTO createEmployee() {
        EmployeeDTO result = new EmployeeDTO();
        result.setAddress("address");
        result.setBirthDate("12-11-1994");
        result.setDegree("M");
        result.setEmployeeId(1);
        result.setEmployeeName("empName");
        result.setSalary(12345);
        result.setStatus("S");
        result.setAddressesList(createaddresses());
        return result;
    }

    private List<String> createaddresses() {
        return Collections.singletonList("amman");
    }
}