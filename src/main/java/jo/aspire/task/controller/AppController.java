package jo.aspire.task.controller;


import jo.aspire.task.CsvObserver;
import jo.aspire.task.SendEmail;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dao.JsonFileDAO;
import jo.aspire.task.dao.TextFileDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.filereader.CsvFileReader;
import jo.aspire.task.migrate.DataMigrater;
import jo.aspire.task.response.EmployeeResponse;
import jo.aspire.task.response.SingleValueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private TextFileDAO textFileDAO;

    @Autowired
    private JsonFileDAO jsonFileDAO;

    @Autowired
    private DataMigrater dataMigrater;

    @Autowired
    private SendEmail sendEmail;


    // TODO: 9/10/18 i'am for testing purpose only so remove me
    @GetMapping("/migrate")
    public void migrate(){
        dataMigrater.migrate();
    }


    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
            availableProcessors());


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmployeeResponse uploadFile(@RequestParam("empInfo") MultipartFile file) throws IOException {


        String extension = getExtension(file.getOriginalFilename());
        File tempFile = File.createTempFile(System.currentTimeMillis() + "", extension);
        file.transferTo(tempFile);
        Runnable runnable = null;

        if ("csv".equals(extension)) runnable = () ->
                handleCsvFile(tempFile);

        else if ("txt".equals(extension)) runnable = () ->
                handleTextFile(tempFile);

        else if ("json".equals(extension))runnable=()->
            handleJsonFile(tempFile);

         else {
            return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), extension + " is NOT Supported");
        }

        executorService.submit(runnable);
        return new EmployeeResponse(HttpStatus.OK.toString(), "Will Notified By Email After Finish Proccessing");
    }

    private void handleTextFile(File file) {
        try {
            StringBuilder content = getFileContent(file);
            textFileDAO.save(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder getFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));

        lines.forEach(content::append);
        return content;
    }

    private void handleCsvFile(File file) {
        try {
            csvFileReader.employees(file, new CsvObserver(mongoEmployeeDAO,sendEmail));
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleJsonFile(File file) {
        StringBuilder content = null;
        try {
            content = getFileContent(file);
            jsonFileDAO.save(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping(value = "/find/id/{employeeId}")
    public EmployeeResponse findByEmployeeId(@PathVariable("employeeId") long employeeId) {
        Optional<EmployeeDTO> byEmployeeId = mongoEmployeeDAO.findByEmployeeId(employeeId);
        if (byEmployeeId.isPresent())
            return new EmployeeResponse(HttpStatus.OK.toString(), Collections.singletonList(byEmployeeId.get()));
        return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), "Employee With ID " + employeeId + " Not Found");

    }


    @GetMapping(value = "/find/name/{employeeName}")
    public EmployeeResponse findByEmployeeName(@PathVariable("employeeName") String employeeName) {
        Optional<EmployeeDTO> employee = mongoEmployeeDAO.findByEmployeeName(employeeName);
        if (employee.isPresent())
            return new EmployeeResponse(HttpStatus.OK.toString(), Collections.singletonList(employee.get()));
        return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), "Employee With Name " + employeeName + " Not Found");
    }

    @GetMapping(value = "/find/degree/{degree}")
    public EmployeeResponse findByDegree(@PathVariable("degree") String degree) {
        Optional<List<EmployeeDTO>> employee = mongoEmployeeDAO.findByDegree(degree);
        if (employee.isPresent())
            return new EmployeeResponse(HttpStatus.OK.toString(), employee.get());
        return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), "No Employees With Degree " + degree + " Found");
    }

    @GetMapping(value = "/find/status/{status}")
    public EmployeeResponse findByStatus(@PathVariable("status") String status) {
        Optional<List<EmployeeDTO>> employee = mongoEmployeeDAO.findByStatus(status);
        if (employee.isPresent())
            return new EmployeeResponse(HttpStatus.OK.toString(), employee.get());
        return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), "No Employees With Status " + status + " Found");
    }

    @GetMapping(value = "/find/{pageNumber}/{pageSize}")
    public EmployeeResponse findByDegree(@PathVariable("pageNumber") int pageNumber, @PathVariable("pageSize") int pageSize) {
        Optional<List<EmployeeDTO>> employee = mongoEmployeeDAO.findAll(pageNumber, pageSize);
        if (employee.isPresent())
            return new EmployeeResponse(HttpStatus.OK.toString(), employee.get());
        return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(), "No Employees Found");
    }


    @GetMapping(value = "/find/ysalary/{employeeId}")
    public SingleValueResponse findByYearlySalary(@PathVariable("employeeId") long employeeId) {
        Optional<Double> yearlySalary = mongoEmployeeDAO.getYearlySalary(employeeId);
        if (yearlySalary.isPresent())
            return new SingleValueResponse(HttpStatus.OK.toString(), yearlySalary.get().toString());
        return new SingleValueResponse(HttpStatus.BAD_REQUEST.toString(), "No Employee Found With ID " + employeeId, null);
    }

    @GetMapping("/find/age/{employeeId}")
    public SingleValueResponse getAgeById(@PathVariable("employeeId") long employeeId) {
        Optional<Long> ageById = mongoEmployeeDAO.getAgeById(employeeId);
        if (ageById.isPresent())
            return new SingleValueResponse(HttpStatus.OK.toString(), ageById.get().toString());
        return new SingleValueResponse(HttpStatus.BAD_REQUEST.toString(), "No Employee Found With ID " + employeeId, null);
    }

    private String getExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

}
