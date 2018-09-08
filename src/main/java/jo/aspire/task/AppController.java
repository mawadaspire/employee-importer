package jo.aspire.task;

import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.entities.EmployeeInfo;
import jo.aspire.task.filereader.CsvFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Response;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private CsvFileReader csvFileReader;

    @Autowired
    @Qualifier("rdbms")
    private EmployeeDAO rdbmsEmployeeDAO;


    @Autowired
    @Qualifier("mongo")
    private EmployeeDAO mongoEmployeeDAO;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
            availableProcessors());

    @GetMapping("/test")
    public ResponseEntity get() {
        return ResponseEntity.ok().build();
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("empInfo") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile(System.currentTimeMillis() + "", "csv");
        file.transferTo(tempFile);
        executorService.submit(() -> {
            try {
                csvFileReader.employees(tempFile, new CsvObserver(mongoEmployeeDAO));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });


        return "Saved: " + mongoEmployeeDAO.findAll().size();
    }

    @GetMapping("/find/{firstName}")
    public EmployeeInfo findByFirstName(@PathVariable("firstname") String firstName) {
        return null;
    }
}
