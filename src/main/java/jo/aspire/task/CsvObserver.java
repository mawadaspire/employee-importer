package jo.aspire.task;

import io.reactivex.observers.DefaultObserver;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CsvObserver extends DefaultObserver {
    private final EmployeeDAO mongoEmployeeDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvObserver.class);


    public CsvObserver(EmployeeDAO mongoEmployeeDAO) {
        this.mongoEmployeeDAO = mongoEmployeeDAO;
    }

    @Override
    public void onNext(Object o) {
        EmployeeDTO employeeInfo = (EmployeeDTO) o;
        mongoEmployeeDAO.save(employeeInfo);
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.error("Error While Receiving data from obserable with error" + e.getMessage());
    }

    @Override
    public void onComplete() {
        LOGGER.info("finish to receive data from observable");
    }
}
