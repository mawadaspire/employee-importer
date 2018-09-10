package jo.aspire.task.migrate;

import jo.aspire.task.entities.AddressDocument;
import jo.aspire.task.entities.AddressEntity;
import jo.aspire.task.entities.EmployeeDocument;
import jo.aspire.task.entities.EmployeeEntity;
import jo.aspire.task.repository.JPAEmployeeRepository;
import jo.aspire.task.repository.MongoAddressRepository;
import jo.aspire.task.repository.MongoEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MongoToMysqlMigrater implements DataMigrater {

    @Autowired
    private MongoEmployeeRepository mongoEmployeeRepository;

    @Autowired
    private JPAEmployeeRepository jpaEmployeeRepository;

    @Autowired
    private MongoAddressRepository mongoAddressRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private final CyclicBarrier cyclicBarrier;

    private final Runnable aggregateRunnable = this::CheckAfterMigrateFinish;

    private final AtomicLong dataMoved = new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoToMysqlMigrater.class);


    public MongoToMysqlMigrater() {
        cyclicBarrier = new CyclicBarrier(2, aggregateRunnable);
    }

    @Override
    @Transactional
    public void migrate() {
        List<EmployeeDocument> allDocuments = mongoEmployeeRepository.findAll();
        moveToMysql(allDocuments);

    }


    private void moveToMysql(List<EmployeeDocument> allDocuments) {
        Spliterator<EmployeeDocument> firstHalf = allDocuments.spliterator();
        Spliterator<EmployeeDocument> secondHalf = firstHalf.trySplit();
        Runnable firstHalfRunnable = () -> moveData(firstHalf);
        Runnable secondHalfRunnable = () -> moveData(secondHalf);
        LOGGER.info("Starting migrating " + allDocuments.size() + " Documents To MYSQL");
        executorService.execute(firstHalfRunnable);
        executorService.execute(secondHalfRunnable);

    }


    private void moveData(Spliterator<EmployeeDocument> data) {
        data.forEachRemaining(record -> {
            EmployeeEntity employeeEntity = createEmployeeEntity(record);
            EmployeeEntity save = jpaEmployeeRepository.save(employeeEntity);
            if (Objects.nonNull(save))
                dataMoved.incrementAndGet();
        });
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        } catch (BrokenBarrierException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private EmployeeEntity createEmployeeEntity(EmployeeDocument record) {
        EmployeeEntity result = new EmployeeEntity();
        result.setBirthDate(record.getBirthDate());
        result.setDegree(record.getDegree());
        result.setEmployeeId(record.getEmployeeId());
        result.setEmployeeName(record.getEmployeeName());
        result.setSalary(record.getSalary());
        result.setStatus(record.getStatus());
        result.setAddressEntities(createAddresses(record.getAddress()));
        return result;
    }

    private List<AddressEntity> createAddresses(List<AddressDocument> address) {
        List<AddressEntity> result = new ArrayList<>();
        address.forEach(a -> {
            result.add(new AddressEntity(a.getAddress()));
        });
        return result;
    }

    private void CheckAfterMigrateFinish() {
        int mongoDataSize = mongoEmployeeRepository.findAll().size();
        if (dataMoved.get() == mongoDataSize) {
            mongoEmployeeRepository.deleteAll();
            mongoAddressRepository.deleteAll();
        }

    }
}
