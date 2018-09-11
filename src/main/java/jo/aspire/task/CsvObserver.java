package jo.aspire.task;

import io.reactivex.observers.DefaultObserver;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class CsvObserver extends DefaultObserver {
    private final EmployeeDAO mongoEmployeeDAO;
    private final SendEmail sendEmail;
    private final AtomicLong numberOfRecords=new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvObserver.class);


    public CsvObserver(EmployeeDAO mongoEmployeeDAO, SendEmail sendEmail) {
        this.mongoEmployeeDAO = mongoEmployeeDAO;
        this.sendEmail = sendEmail;
    }

    @Override
    public void onNext(Object o) {
        EmployeeDTO employeeInfo = (EmployeeDTO) o;
        Optional<EmployeeDTO> save = mongoEmployeeDAO.save(employeeInfo);
        if(Objects.nonNull(save))
            numberOfRecords.incrementAndGet();
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.error("Error While Receiving data from obserable with error" + e.getMessage());
    }

    @Override
    public void onComplete() {
        LOGGER.info("finish to receive data from observable And Sending Email");
        sendEmail.send(numberOfRecords.longValue());
    }
}
