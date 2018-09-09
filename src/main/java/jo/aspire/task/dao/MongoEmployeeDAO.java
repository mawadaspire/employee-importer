package jo.aspire.task.dao;

import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.repository.AddressRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private AddressRepository addressRepository;


    @Override
    public void saveAll(Iterable<EmployeeDTO> employeeIterator) {

    }

    @Override
    public Optional<EmployeeDTO> save(EmployeeDTO employee) {
        EmployeeDocument save = mongoEmployeeRepository.save(createEntity(employee));
//        employee.getAddressesList().forEach(a->{
//            addressRepository.save(createAddressDocument(a,save));
//        });
        if (Objects.nonNull(save))
            return Optional.of(createDTO(save));
        return Optional.empty();
    }

    private AddressDocument createAddressDocument(String address, EmployeeDocument save) {
        return new AddressDocument(save.getId(),address);
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
        List<String> result=new ArrayList<>();
        address.forEach(a->result.add(a.getAddress()));
        return result;
    }

    private EmployeeDocument createEntity(EmployeeDTO employee) {
        EmployeeDocument result = new EmployeeDocument();
        result.setAddress(createAddressesDocuments(employee.getAddressesList()));
        result.setBirthDate(employee.getBirthDate());
        result.setDegree(employee.getDegree());
        result.setEmployeeId(employee.getEmployeeId());
        result.setEmployeeName(employee.getEmployeeName());
        result.setSalary(employee.getSalary());
        result.setStatus(employee.getStatus());
        return result;
    }

    private List<AddressDocument> createAddressesDocuments(List<String> addressesList) {
        List<AddressDocument> result=new ArrayList<>();
        addressesList.forEach(a->result.add(new AddressDocument(a)));
        return result;
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
                project().and("salary").multiply(12)
        };
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<EmployeeDocument> result = mongoTemplate
                .aggregate(aggregation, "employees", EmployeeDocument.class);


        if (Objects.nonNull(result.getMappedResults()) && !result.getMappedResults().isEmpty()) {

            Double salary = result.getMappedResults().get(0).getSalary();
            return Optional.of(salary);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getAgeById(long employeeId) {
        EmployeeDocument byEmployeeId = mongoEmployeeRepository.findByEmployeeId(employeeId);
        if (Objects.nonNull(byEmployeeId))
            return Optional.of(calculateAge(byEmployeeId));
        return Optional.empty();
    }

    private long calculateAge(EmployeeDocument byEmployeeId) {
        LocalDate birthDate = LocalDate.parse(byEmployeeId.getBirthDate(), DateTimeFormatter.ofPattern("M-d-yyyy"));
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

}
