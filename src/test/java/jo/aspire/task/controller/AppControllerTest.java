package jo.aspire.task.controller;

import jo.aspire.task.Application;
import jo.aspire.task.TestConfig;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.entities.JsonFileEntity;
import jo.aspire.task.entities.TextFileDocument;
import jo.aspire.task.repository.JPAJsonFileRepository;
import jo.aspire.task.repository.MongoEmployeeRepository;
import jo.aspire.task.repository.MongoTextFileRepository;
import jo.aspire.task.response.EmployeeResponse;
import jo.aspire.task.response.SingleValueResponse;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
@EnableAutoConfiguration
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class AppControllerTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final int SIZE = 5;
    public static final String TEST_CSV = "test.csv";

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

    @Autowired
    private MongoTextFileRepository mongoTextFileRepository;

    @Autowired
    private JPAJsonFileRepository jpaJsonFileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        clearDataBase();

    }


    @Test
    public void givenEmployeeInfoFileWhenUploadThenDocumentsShouldBeInserted() throws Exception {
        insertData(createFile(TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        List<EmployeeDocument> all = mongoEmployeeRepository.findAll();
        assertEquals(SIZE, all.size());
        assertTrue(containName(all, "Mohd"));
        assertTrue(containName(all, "Ahmad"));
        assertTrue(containName(all, "Yazan"));
        assertTrue(containName(all, "Fadi"));

    }

    @Test
    public void givenInsertedEmployeesWhenFindByIdThenEmployeeShouldReturned() throws Exception {
        insertData(createFile(TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/id/1", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        EmployeeDTO data = response.getData().get(0);
        assertEmployee(data, "Mohd", "B", 1, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingIdThenShouldFail() throws Exception {
        insertData(createFile(TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/id/7", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertEquals("Employee With ID 7 Not Found", response.getMessage());
        assertNull(response.getData());

    }

    @Test
    public void givenInsertedEmployeesWhenFindByNameThenEmployeeShouldReturned() throws Exception {
        insertData(createFile(TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/name/Mohd", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        EmployeeDTO data = response.getData().get(0);
        assertEmployee(data, "Mohd", "B", 1, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingNameThenShouldFail() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/name/NotFoundName", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertEquals("Employee With Name NotFoundName Not Found", response.getMessage());
        assertNull(response.getData());

    }


    @Test
    public void givenInsertedEmployeesWhenFindByDegreeThenEmployeesShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/degree/B", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2, data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingDegreeThenShouldFail() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/degree/A", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertEquals("No Employees With Degree A Found", response.getMessage());
        assertNull(response.getData());

    }

    @Test
    public void givenInsertedEmployeesWhenFindByStatusThenEmployeesShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/status/S", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2, data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
    }

    @Test
    public void givenInsertedEmployeesWhenFindByNonExistingStatusThenShouldFail() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/status/Z", EmployeeResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertEquals("No Employees With Status Z Found", response.getMessage());
        assertNull(response.getData());

    }


    @Test
    public void givenInsertedEmployeesWhenFindByAllElementsPageThenEmployeesShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/0/5", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(SIZE, data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");
        assertEmployee(data.get(2), "Ahmad", "M", 3, "M");
        assertEmployee(data.get(3), "Yazan", "M", 4, "M");
    }

    @Test
    public void givenInsertedEmployeesWhenFindBySubsetOfElementsPageThenEmployeesShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/0/2", EmployeeResponse.class);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertNull(response.getMessage());
        List<EmployeeDTO> data = response.getData();
        assertEquals(2, data.size());
        assertEmployee(data.get(0), "Mohd", "B", 1, "S");
        assertEmployee(data.get(1), "Fadi", "B", 2, "S");

    }


    @Test
    public void givenInsertedEmployeesWhenFindByNonExistsPageThenNoEmployeesShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        EmployeeResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/4/2", EmployeeResponse.class);
        List<EmployeeDTO> data = response.getData();
        assertNull(data);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertEquals("No Employees Found", response.getMessage());
    }


    @Test
    public void givenInsertedEmployeesWhenFindYearlySalaryByIdForMasterAndMarriedThenYearlySalaryShouldReturnedWithBonus5Percent() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/ysalary/3", SingleValueResponse.class);
        assertNull(response.getMessage());
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals(126_000, Double.parseDouble(response.getValue()), 3);
    }

    @Test
    public void givenInsertedEmployeesWhenFindYearlySalaryByIdForDoctorAndMarriedThenYearlySalaryShouldReturnedWithBonus10Percent() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/ysalary/5", SingleValueResponse.class);
        assertNull(response.getMessage());
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals(132_000, Double.parseDouble(response.getValue()), 3);
    }

    @Test
    public void givenInsertedEmployeesWhenFindYearlySalaryByNonExistingIdThenShouldFail() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/ysalary/9", SingleValueResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertNull(response.getValue());
        assertEquals("No Employee Found With ID 9", response.getMessage());
    }

    @Test
    public void givenInsertedEmployeesWhenGetAgeByIdThenAgeShouldReturned() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/age/1", SingleValueResponse.class);
        assertNull(response.getMessage());
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals(getCurrentAge("12-11-1994"), Long.parseLong(response.getValue()));
    }

    @Test
    public void givenInsertedEmployeesWhenGetAgeByNonExistingIdThenShouldFail() throws Exception {
        insertData(createFile(UUID.randomUUID().toString()+TEST_CSV));
        Awaitility.await().until(() -> mongoEmployeeRepository.findAll().size(), equalTo(SIZE));
        SingleValueResponse response = testRestTemplate.getForObject(HTTP_LOCALHOST + port + "/api/find/age/99", SingleValueResponse.class);
        assertEquals("No Employee Found With ID 99", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getCode());
        assertNull(response.getValue());
    }

    @Test
    public void givenUploadTextFileWhenUploadThenFileShouldBeInserted() throws Exception {
        insertData(createFile("test.txt"));
        Awaitility.await().until(() -> mongoTextFileRepository.findAll().size(), equalTo(1));
        List<TextFileDocument> all = mongoTextFileRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(new String(createFileContent()).replace("\n", ""), all.get(0).getTextContent());
    }

    @Test
    @Transactional
    public void givenUploadJsonFileWhenUploadThenFileShouldBeInserted() throws Exception {
        entityManager.createQuery("DELETE FROM JsonFileEntity").executeUpdate();
        File tempFile = testFolder.newFile("test.json");
        String fileContent = "{\n" +
                "      \"employeeId\": 4,\n" +
                "      \"employeeName\": \"Yazan\",\n" +
                "      \"salary\": 10000,\n" +
                "      \"status\": \"M\",\n" +
                "      \"birthDate\": \"12-11-1994\",\n" +
                "      \"address\": null,\n" +
                "      \"addressesList\": [\n" +
                "        \"amman\",\n" +
                "        \"irbid\",\n" +
                "        \"zarqa\"\n" +
                "      ],\n" +
                "      \"degree\": \"M\"\n" +
                "    }";
        Files.write(Paths.get(tempFile.getAbsolutePath()), fileContent.getBytes());

        insertData(tempFile);
        Awaitility.await().until(() -> jpaJsonFileRepository.findAll().iterator().hasNext());
        JsonFileEntity jsonFileEntity = jpaJsonFileRepository.findById(1L).get();
        assertNotNull(jsonFileEntity);
        assertEquals(fileContent.trim().replace("\n", ""), jsonFileEntity.getFileContent().trim().replace("\n",""));
    }

    private long getCurrentAge(String bDate) {
        LocalDate birthDate = LocalDate.parse(bDate, DateTimeFormatter.ofPattern("M-d-yyyy"));
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }


    private void assertEmployee(EmployeeDTO data, String empName, String degree, int employeeId, String status) {
        assertEquals(empName, data.getEmployeeName());
        assertEquals(employeeId, data.getEmployeeId());
        assertEquals(10000, data.getSalary(), 3);
        assertEquals(status, data.getStatus());
        List<String> addressesList = data.getAddressesList();
        assertTrue(addressesList.contains("amman"));
        assertTrue(addressesList.contains("irbid"));
        assertTrue(addressesList.contains("zarqa"));
        assertEquals(degree, data.getDegree());
        assertEquals("12-11-1994", data.getBirthDate());
    }

    private boolean containName(List<EmployeeDocument> all, String empName) {
        return all.stream().filter(d -> d.getEmployeeName().equals(empName)).count() == 1;
    }


    private File createFile(String fileName) throws IOException {
        File tempFile = testFolder.newFile(fileName);
        Files.write(Paths.get(tempFile.getAbsolutePath()), createFileContent());
        return tempFile;
    }

    private byte[] createFileContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1,Mohd,10000,S,12-11-1994,B,[amman-irbid-zarqa]\n");
        stringBuilder.append("2,Fadi,10000,S,12-11-1994,B,[amman-irbid-zarqa]\n");
        stringBuilder.append("3,Ahmad,10000,M,12-11-1994,M,[amman-irbid-zarqa]\n");
        stringBuilder.append("4,Yazan,10000,M,12-11-1994,M,[amman-irbid-zarqa]\n");
        stringBuilder.append("5,Anas,10000,M,12-11-1994,D,[amman-irbid-zarqa]");
        return stringBuilder.toString().getBytes();
    }

    @After
    public void tearDown() throws Exception {
        clearDataBase();
    }

    private void clearDataBase() {
        Query employeeQuery = new Query();
        employeeQuery.addCriteria(Criteria.where("employeeName").exists(true));

        Query addressQuery = new Query();
        addressQuery.addCriteria(Criteria.where("address").exists(true));

        Query textQuery = new Query();
        textQuery.addCriteria(Criteria.where("textContent").exists(true));

        mongoTemplate.findAllAndRemove(employeeQuery, EmployeeDocument.class);
        mongoTemplate.findAllAndRemove(addressQuery, AddressDocument.class);
        mongoTemplate.findAllAndRemove(textQuery, TextFileDocument.class);
    }

    private void insertData(File file) throws IOException {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("empInfo", new FileSystemResource(file.getAbsolutePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                map, headers);
        testRestTemplate.postForObject(HTTP_LOCALHOST + port + "/api/upload", requestEntity, String.class);
    }
}