package jo.aspire.task.migrate;

import jo.aspire.task.Application;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.lookup.Degree;
import jo.aspire.task.lookup.Status;
import jo.aspire.task.repository.FailedEmployeeRepository;
import jo.aspire.task.repository.JPAEmployeeRepository;
import jo.aspire.task.repository.MongoAddressRepository;
import jo.aspire.task.repository.MongoEmployeeRepository;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableAutoConfiguration
public class DataMigraterImplTest {


    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;


    @Autowired
    @Qualifier("mongo")
    private EmployeeDAO fromEmployeeDao;

    @Autowired
    @Qualifier("rdbms")
    private EmployeeDAO toEmployeeDao;

    @Autowired
    private MongoAddressRepository mongoAddressRepository;

    @Autowired
    private DataMigrater dataMigrater;


    @Autowired
    private FailedEmployeeRepository failedEmployeeRepository;

    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Before
    public void setUp() throws Exception {
        mongoAddressRepository.deleteAll();
        failedEmployeeRepository.deleteAll();
        mongoEmployeeRepository.deleteAll();
        jpaEmployeeRepository.deleteAll();

    }

    @Test
    public void givenMongoDataDocumentsInsertedWhenMigrateAllRecordsShouldBeMovedToMysqlDataBase() {
        List<EmployeeDocument> employeeDocuments = insertDocuments(5);
        mongoEmployeeRepository.saveAll(employeeDocuments);
        dataMigrater.migrate(fromEmployeeDao, toEmployeeDao);
        Awaitility.await().until(() -> toEmployeeDao.findAll().get().size(), equalTo(5));
        List<EmployeeDTO> all = toEmployeeDao.findAll().get();
        assertEquals(5, all.size());
        assertEquals(5, fromEmployeeDao.findAll().get().size());
        assertEquals(5, toEmployeeDao.findAll().get().size());
        assertFalse(failedEmployeeRepository.findAll().iterator().hasNext());
    }


    @Test
    public void givenMongoDataDocumentsInsertedWithInvalidRecordInMysqlWhenMigrateAllRecordsShouldBeMovedToMysqlDataBaseAndOneRecordInFailedTable() {
        List<EmployeeDocument> employeeDocuments = insertDocuments(5);
        EmployeeDocument employeeDocument = employeeDocuments.get(1);
        employeeDocument.setSalary(300);
        employeeDocuments.add(employeeDocument);
        mongoEmployeeRepository.saveAll(employeeDocuments);
        dataMigrater.migrate(fromEmployeeDao, toEmployeeDao);
        Awaitility.await().until(() -> toEmployeeDao.findAll().get().size(), equalTo(5));
        List<EmployeeDTO> all = toEmployeeDao.findAll().get();
        assertEquals(5, all.size());
        assertEquals(6, fromEmployeeDao.findAll().get().size());
        assertEquals(5, toEmployeeDao.findAll().get().size());
        long count = StreamSupport.stream(failedEmployeeRepository.findAll().spliterator(), false).count();
        assertEquals(1, count);
    }

    private List<EmployeeDocument> insertDocuments(int numberOfDocuments) {
        List<EmployeeDocument> employeeDocuments = new ArrayList<>();
        IntStream.range(0, numberOfDocuments).forEach(i -> {
            EmployeeDocument employeeDocument = new EmployeeDocument();
            employeeDocument.setStatus(Status.M.name());
            employeeDocument.setSalary(1000);
            employeeDocument.setEmployeeName("Employee " + i);
            employeeDocument.setEmployeeId(i);
            employeeDocument.setDegree(Degree.PHD.name());
            employeeDocument.setBirthDate("12-11-1994");
            employeeDocument.setAddress(createAddresses());
            employeeDocuments.add(employeeDocument);
        });
        return employeeDocuments;
    }

    private List<AddressDocument> createAddresses() {
        List<AddressDocument> result = new ArrayList<>();
        result.add(mongoAddressRepository.save(createAddressDocument(1)));
        result.add(mongoAddressRepository.save(createAddressDocument(2)));
        return result;
    }

    private AddressDocument createAddressDocument(int number) {
        AddressDocument result = new AddressDocument();
        result.setAddress("Address " + number);
        return result;
    }
}