package jo.aspire.task;

import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.repository.MongoEmployeeRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@DataMongoTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        ,properties="spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration")
@ContextConfiguration(classes = TestConfig.class)
@EnableAutoConfiguration
public class AppControllerTest {

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
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("empInfo", createFile());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                map, headers);

        String response = testRestTemplate.postForObject("http://localhost:" + port + "/api/upload", requestEntity, String.class);
        repo
    }


    private File createFile() throws IOException {
        File tempFile = testFolder.newFile("test.csv");
        Files.write(Paths.get(tempFile.getAbsolutePath()), createFileContent());
        return tempFile;
    }

    private byte[] createFileContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1,Mohd,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("2,Fadi,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("3,Ahmad,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("4,Yazan,9999,M,12/11/1994,B,Amman");
        return stringBuilder.toString().getBytes();
    }
}