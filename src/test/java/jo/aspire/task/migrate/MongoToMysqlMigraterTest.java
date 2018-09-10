package jo.aspire.task.migrate;

import jo.aspire.task.Application;
import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.entities.EmployeeEntity;
import jo.aspire.task.lookup.Degree;
import jo.aspire.task.lookup.Status;
import jo.aspire.task.repository.JPAEmployeeRepository;
import jo.aspire.task.repository.MongoAddressRepository;
import jo.aspire.task.repository.MongoEmployeeRepository;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableAutoConfiguration
public class MongoToMysqlMigraterTest {


    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;



    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Autowired
    private MongoAddressRepository mongoAddressRepository;

    @Autowired
    private DataMigrater dataMigrater;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    public void givenMongoDataDocumentsInsertedWhenMigrateAllRecordsShouldBeMovedToMysqlDataBase() {
        insertDocuments(5);
        dataMigrater.migrate();
        Awaitility.await().until(()->mongoEmployeeRepository.findAll().isEmpty());
        List<EmployeeEntity> all = jpaEmployeeRepository.findAll();
        assertEquals(5,all.size());
        assertTrue(mongoEmployeeRepository.findAll().isEmpty());
        assertTrue(mongoAddressRepository.findAll().isEmpty());
    }

    private void insertDocuments(int numberOfDocuments) {
        IntStream.range(0,numberOfDocuments).forEach(i->{
            EmployeeDocument employeeDocument=new EmployeeDocument();
            employeeDocument.setStatus(Status.M.name());
            employeeDocument.setSalary(1000);
            employeeDocument.setEmployeeName("Employee "+i);
            employeeDocument.setEmployeeId(i);
            employeeDocument.setDegree(Degree.PHD.name());
            employeeDocument.setBirthDate("12-11-1994");
            employeeDocument.setAddress(createAddresses());
            mongoEmployeeRepository.save(employeeDocument);
        });
    }

    private List<AddressDocument> createAddresses() {
        List<AddressDocument> result=new ArrayList<>();
        result.add(mongoAddressRepository.save(createAddressDocument(1)));
        result.add(mongoAddressRepository.save(createAddressDocument(2)));
        return result;
    }

    private AddressDocument createAddressDocument(int number) {
        AddressDocument result=new AddressDocument();
        result.setAddress("Address "+ number);
        return result;
    }
}