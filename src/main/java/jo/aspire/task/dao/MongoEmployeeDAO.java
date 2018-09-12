package jo.aspire.task.dao;

import jo.aspire.task.dto.DownloadFileData;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.lookup.Degree;
import jo.aspire.task.lookup.Status;
import jo.aspire.task.repository.MongoAddressRepository;
import jo.aspire.task.repository.MongoEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Component
@Qualifier("mongo")
public class MongoEmployeeDAO implements EmployeeDAO {

    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoAddressRepository mongoAddressRepository;


    @Override
    public void saveAll(Iterable<EmployeeDTO> employeeIterator) {

    }

    @Override
    public Optional<EmployeeDTO> save(EmployeeDTO employee) {
        List<AddressDocument> addresses = new ArrayList<>();
        employee.getAddressesList().forEach(a -> {
            addresses.add(mongoAddressRepository.save(createAddressDocument(a)));
        });
        EmployeeDocument save = mongoEmployeeRepository.save(createEntity(employee, addresses));
        if (Objects.nonNull(save))
            return Optional.of(createDTO(save));
        return Optional.empty();
    }

    @Override
    public Optional<List<EmployeeDTO>> findAll(int pageNumber, int pageSize) {
        Page<EmployeeDocument> page = mongoEmployeeRepository.findAll(PageRequest.of(pageNumber, pageSize));
        if (Objects.nonNull(page) && page.getContent().size() > 0) {
            List<EmployeeDTO> result = new ArrayList<>();
            page.getContent().forEach(emp -> result.add(createDTO(emp)));
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<EmployeeDTO>> findNotMigratedRecords() {
        List<EmployeeDocument> all = mongoEmployeeRepository.findAll();
        if (Objects.nonNull(all) && !all.isEmpty()) {
            List<EmployeeDocument> employeeDocuments = all.stream().filter(e -> !e.isMigrated()).collect(Collectors.toList());
            List<EmployeeDTO> result = new ArrayList<>();
            employeeDocuments.forEach(emp -> result.add(createDTO(emp)));
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DownloadFileData>> findAllToDownload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeId(long employeeId) {
        EmployeeDocument byEmployeeId = mongoEmployeeRepository.findByEmployeeId(employeeId);
        if (Objects.nonNull(byEmployeeId))
            return Optional.of(createDTO(byEmployeeId));
        return Optional.empty();
    }

    @Override
    public Optional<EmployeeDTO> findByEmployeeName(String employeeName) {
        EmployeeDocument employee = mongoEmployeeRepository.findByEmployeeName(employeeName);
        if (Objects.nonNull(employee))
            return Optional.of(createDTO(employee));
        return Optional.empty();
    }

    @Override
    public Optional<List<EmployeeDTO>> findByDegree(String degree) {
        List<EmployeeDocument> byDegree = mongoEmployeeRepository.findByDegree(degree);
        if (Objects.nonNull(byDegree) && !byDegree.isEmpty()) {
            List<EmployeeDTO> result = new ArrayList<>();
            byDegree.forEach(emp -> result.add(createDTO(emp)));
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<EmployeeDTO>> findByStatus(String status) {
        List<EmployeeDocument> byDegree = mongoEmployeeRepository.findByStatus(status);
        if (Objects.nonNull(byDegree) && !byDegree.isEmpty()) {
            List<EmployeeDTO> result = new ArrayList<>();
            byDegree.forEach(emp -> result.add(createDTO(emp)));
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Double> getYearlySalary(long employeeId) {
        AggregationOperation[] aggregationOperation = new AggregationOperation[]{
                match(Criteria.where("employeeId").is(employeeId)),
                project("salary", "status", "degree").and("salary").multiply(12)

        };
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<EmployeeDocument> result = mongoTemplate
                .aggregate(aggregation, "employees", EmployeeDocument.class);


        if (Objects.nonNull(result.getMappedResults()) && !result.getMappedResults().isEmpty()) {

            EmployeeDocument employeeDocument = result.getMappedResults().get(0);
            Double salary = addBonus(employeeDocument);
            return Optional.of(salary);
        }
        return Optional.empty();
    }

    private Double addBonus(EmployeeDocument employeeDocument) {
        BigDecimal salary = BigDecimal.valueOf(employeeDocument.getSalary());
        if (isMarried(employeeDocument) && isMasterDegree(employeeDocument))
            salary = salary.add(salary.multiply(BigDecimal.valueOf(0.05)));
        else if (isMarried(employeeDocument) && (isMasterDegree(employeeDocument) || isDoctorDegree(employeeDocument) || isProfessorDegreee(employeeDocument)))
            salary = salary.add(salary.multiply(BigDecimal.valueOf(0.10)));

        return salary.doubleValue();
    }


    private boolean isProfessorDegreee(EmployeeDocument employeeDocument) {
        return Degree.PHD.name().equals(employeeDocument.getDegree());
    }

    private boolean isDoctorDegree(EmployeeDocument employeeDocument) {
        return Degree.D.name().equals(employeeDocument.getDegree());
    }

    private boolean isMasterDegree(EmployeeDocument employeeDocument) {
        return Degree.M.name().equals(employeeDocument.getDegree());
    }

    private boolean isMarried(EmployeeDocument employeeDocument) {
        return Status.M.name().equals(employeeDocument.getStatus());
    }

    @Override
    public Optional<Long> getAgeById(long employeeId) {
        EmployeeDocument byEmployeeId = mongoEmployeeRepository.findByEmployeeId(employeeId);
        if (Objects.nonNull(byEmployeeId))
            return Optional.of(calculateAge(byEmployeeId));
        return Optional.empty();
    }

    @Override
    public void updateIsMigrated(boolean isMigrated, long employeeId) {
        mongoEmployeeRepository.updateEmployeeIsMigrated(isMigrated, employeeId);
    }

    private long calculateAge(EmployeeDocument byEmployeeId) {
        LocalDate birthDate = LocalDate.parse(byEmployeeId.getBirthDate(), DateTimeFormatter.ofPattern("M-d-yyyy"));
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }


    private AddressDocument createAddressDocument(String address) {
        return new AddressDocument(address);
    }

    private EmployeeDTO createDTO(EmployeeDocument save) {
        EmployeeDTO result = new EmployeeDTO();
        result.setAddressesList(createAddressesList(save.getAddress()));
        result.setBirthDate(save.getBirthDate());
        result.setDegree(save.getDegree());
        result.setEmployeeId(save.getEmployeeId());
        result.setEmployeeName(save.getEmployeeName());
        result.setSalary(save.getSalary());
        result.setStatus(save.getStatus());
        return result;
    }

    private List<String> createAddressesList(List<AddressDocument> address) {
        List<String> result = new ArrayList<>();
        address.forEach(a -> result.add(a.getAddress()));
        return result;
    }

    private EmployeeDocument createEntity(EmployeeDTO employee, List<AddressDocument> addresses) {
        EmployeeDocument result = new EmployeeDocument();
        result.setAddress(addresses);
        result.setBirthDate(employee.getBirthDate());
        result.setDegree(employee.getDegree());
        result.setEmployeeId(employee.getEmployeeId());
        result.setEmployeeName(employee.getEmployeeName());
        result.setSalary(employee.getSalary());
        result.setStatus(employee.getStatus());
        return result;
    }
}
