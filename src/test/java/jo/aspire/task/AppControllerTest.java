package jo.aspire.task;

import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.repository.MongoEmployeeRepository;
import jo.aspire.task.response.EmployeeResponse;
import jo.aspire.task.response.SingleValueResponse;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
@ContextConfiguration(classes = TestConfig.class)
@EnableAutoConfiguration
public class AppControllerTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;


    @Before
    public void setUp() throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeName").exists(true));
        mongoTemplate.findAllAndRemove(query, EmployeeDocument.class);
    }

    @Test
    public void givenEmployeeInfoFileWhenUploadThenDocumentsShouldBeInserted() throws Exception {
        insertData();


        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        List<EmployeeDocument> all = mongoEmployeeRepository.findAll();
        assertEquals(4, all.size());
        assertTrue(containName(all, "Mohd"));
        assertTrue(containName(all, "Ahmad"));
        assertTrue(containName(all, "Yazan"));
        assertTrue(containName(all, "Fadi"));

    }

    private void insertData() throws IOException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("empInfo", new FileSystemResource(createFile().getAbsolutePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                map, headers);
        testRestTemplate.postForObject(HTTP_LOCALHOST + port + "/api/upload", requestEntity, String.class);
    }

    @Test
    public void givenInsertedEmployeesWhenFindByIdThenEmployeeShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/id/1", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        EmployeeDTO data = response.getData().get(0);
        assertEmployee(data, "Mohd", "B", 1, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingIdThenShouldFail() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/id/7", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertEquals("Employee With ID 7 Not Found",response.getMessage());
        assertNull(response.getData());

    }

    @Test
    public void givenInsertedEmployeesWhenFindByNameThenEmployeeShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/name/Mohd", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        EmployeeDTO data = response.getData().get(0);
        assertEmployee(data, "Mohd", "B", 1, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingNameThenShouldFail() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/name/NotFoundName", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertEquals("Employee With Name NotFoundName Not Found",response.getMessage());
        assertNull(response.getData());

    }


    @Test
    public void givenInsertedEmployeesWhenFindByDegreeThenEmployeesShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/degree/B", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2,data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingDegreeThenShouldFail() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/degree/A", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertEquals("No Employees With Degree A Found",response.getMessage());
        assertNull(response.getData());

    }

    @Test
    public void givenInsertedEmployeesWhenFindByStatusThenEmployeesShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/status/S", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2,data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingStatusThenShouldFail() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/status/Z", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertEquals("No Employees With Status Z Found",response.getMessage());
        assertNull(response.getData());

    }


    @Test
    public void givenInsertedEmployeesWhenFindByAllElementsPageThenEmployeesShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/0/4", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(4,data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
        assertEmployee(data.get(2), "Ahmad", "M", 3, "M");
        assertEmployee(data.get(3), "Yazan", "M", 4, "M");
    }

    @Test
    public void givenInsertedEmployeesWhenFindBySubsetOfElementsPageThenEmployeesShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/0/2", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2,data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");

    }


    @Test
    public void givenInsertedEmployeesWhenFindByNonExistsPageThenNoEmployeesShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/4/2", EmployeeResponse.class);
        List<EmployeeDTO> data = response.getData();
        assertNull(data);
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertEquals("No Employees Found",response.getMessage());
    }


    @Test
    public void givenInsertedEmployeesWhenFindYearlySalaryByIdThenYearlySalaryShouldReturned() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/ysalary/1", SingleValueResponse.class);
        assertNull(response.getMessage());
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertEquals(120_000,Double.parseDouble(response.getValue()),3);
    }

    @Test
    public void givenInsertedEmployeesWhenFindYearlySalaryByNonExistingIdThenShouldFail() throws Exception {
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/ysalary/9", SingleValueResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertNull(response.getValue());
       assertEquals("No Employee Found With ID 9",response.getMessage());
    }

    @Test
    public void givenInsertedEmployeesWhenGetAgeByIdThenAgeShouldReturned() throws Exception{
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/age/1", SingleValueResponse.class);
        assertNull(response.getMessage());
        assertEquals(HttpStatus.OK.toString(),response.getCode());
        assertEquals(getCurrentAge("12-11-1994"),Long.parseLong(response.getValue()));
    }

    @Test
    public void givenInsertedEmployeesWhenGetAgeByNonExistingIdThenShouldFail() throws Exception{
        insertData();
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(4));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/age/99", SingleValueResponse.class);
        assertEquals("No Employee Found With ID 99",response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(),response.getCode());
        assertNull(response.getValue());
    }

    private long getCurrentAge(String bDate) {
        LocalDate birthDate = LocalDate.parse(bDate, DateTimeFormatter.ofPattern("M-d-yyyy"));
        return ChronoUnit.YEARS.between(birthDate,LocalDate.now());
    }


    private void assertEmployee(EmployeeDTO data, String empName, String degree, int employeeId, String status) {
        assertEquals(empName, data.getEmployeeName());
        assertEquals(employeeId, data.getEmployeeId());
        assertEquals(10000, data.getSalary(), 3);
        assertEquals(status, data.getStatus());
        List<String> addressesList = data.getAddressesList();
        assertTrue( addressesList.contains("amman"));
        assertTrue( addressesList.contains("irbid"));
        assertTrue( addressesList.contains("zarqa"));
        assertEquals(degree, data.getDegree());
        assertEquals("12-11-1994", data.getBirthDate());
    }

    private boolean containName(List<EmployeeDocument> all, String empName) {
        return all.stream().filter(d -> d.getEmployeeName().equals(empName)).count() == 1;
    }


    private File createFile() throws IOException {
        File tempFile = testFolder.newFile("test.csv");
        Files.write(Paths.get(tempFile.getAbsolutePath()), createFileContent());
        return tempFile;
    }

    private byte[] createFileContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1,Mohd,10000,S,12-11-1994,B,[amman-irbid-zarqa]\n");
        stringBuilder.append("2,Fadi,10000,S,12-11-1994,B,[amman-irbid-zarqa]\n");
        stringBuilder.append("3,Ahmad,10000,M,12-11-1994,M,[amman-irbid-zarqa]\n");
        stringBuilder.append("4,Yazan,10000,M,12-11-1994,M,[amman-irbid-zarqa]");
        return stringBuilder.toString().getBytes();
    }
}