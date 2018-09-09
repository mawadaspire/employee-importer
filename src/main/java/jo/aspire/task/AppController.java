package jo.aspire.task;


import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.filereader.CsvFileReader;
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


    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
            availableProcessors());


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 9/9/18 check extensions
    public EmployeeResponse uploadFile(@RequestParam("empInfo") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile(System.currentTimeMillis() + "", "csv");
        file.transferTo(tempFile);
        String extension = getExtension(file.getOriginalFilename());
        Runnable runnable = null;
        if ("csv".equals(extension)) runnable = () -> {
            try {
                csvFileReader.employees(tempFile, new CsvObserver(mongoEmployeeDAO));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        else if ("txt".equals(extension)) {

        } else if ("json".equals(extension)) {

        } else {
            return new EmployeeResponse(HttpStatus.BAD_REQUEST.toString(),extension + " is NOT Supported");
        }
        executorService.submit(runnable);
        return new EmployeeResponse(HttpStatus.OK.toString(), "Will Notified By Email After Finish Proccessing");
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
        return new SingleValueResponse(HttpStatus.BAD_REQUEST.toString(), "No Employee Found With ID "+employeeId,null);
    }

@GetMapping("/find/age/{employeeId}")
    public SingleValueResponse getAgeById(@PathVariable("employeeId") long employeeId) {
    Optional<Long> ageById = mongoEmployeeDAO.getAgeById(employeeId);
    if(ageById.isPresent())
        return new SingleValueResponse(HttpStatus.OK.toString(), ageById.get().toString());
    return new SingleValueResponse(HttpStatus.BAD_REQUEST.toString(), "No Employee Found With ID "+employeeId,null);
    }

    private String getExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

}
