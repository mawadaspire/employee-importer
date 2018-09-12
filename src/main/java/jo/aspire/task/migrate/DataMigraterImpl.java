package jo.aspire.task.migrate;

import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.FailedEmployees;
import jo.aspire.task.repository.FailedEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DataMigraterImpl implements DataMigrater {

    private final AtomicLong dataMoved = new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigraterImpl.class);

    @Autowired
    private FailedEmployeeRepository failedEmployeeRepository;


    @Override
    public void migrate(EmployeeDAO from, EmployeeDAO to) {
        Optional<List<EmployeeDTO>> allDocuments = from.findNotMigratedRecords();
        allDocuments.ifPresent(employeeDTOS -> moveToMysql(employeeDTOS, from, to));
    }

    private void moveToMysql(List<EmployeeDTO> allDocuments, EmployeeDAO from, EmployeeDAO to) {
        Spliterator<EmployeeDTO> firstHalf = allDocuments.spliterator();
        Spliterator<EmployeeDTO> secondHalf = firstHalf.trySplit();
        Runnable firstHalfRunnable = () -> moveData(firstHalf, from, to);
        Runnable secondHalfRunnable = () -> moveData(secondHalf, from, to);
        LOGGER.info("Starting migrating " + allDocuments.size() + " Documents To MYSQL");
        new Thread(firstHalfRunnable).start();
        new Thread(secondHalfRunnable).start();

    }


    private void moveData(Spliterator<EmployeeDTO> data, EmployeeDAO from, EmployeeDAO to) {
        if (Objects.nonNull(data) && data.getExactSizeIfKnown() > 0) {
            data.forEachRemaining(record -> {
                try {
                    Optional<EmployeeDTO> save = to.save(record);
                    if (Objects.nonNull(save)) {
                        dataMoved.incrementAndGet();
                        markRecordAsMigrated(from, record.getEmployeeId());
                    } else {
                        LOGGER.error("Error While Migrating " + record.toString());
                        FailedEmployees byEmployeeData = failedEmployeeRepository.findByEmployeeData(record.toString());
                        if (Objects.isNull(byEmployeeData))
                            failedEmployeeRepository.save(new FailedEmployees(record.toString()));
                    }
                } catch (Exception e) {
                    LOGGER.error("Error While Migrating " + record.toString());
                    FailedEmployees byEmployeeData = failedEmployeeRepository.findByEmployeeData(record.toString());
                    if (Objects.isNull(byEmployeeData))
                        failedEmployeeRepository.save(new FailedEmployees(record.toString()));

                }
            });
        }
    }

    private void markRecordAsMigrated(EmployeeDAO from, long employeeId) {
        from.updateIsMigrated(true, employeeId);

    }
}
