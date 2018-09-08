package jo.aspire.task;

import io.reactivex.observers.DefaultObserver;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.entities.EmployeeInfo;
import org.springframework.stereotype.Component;


@Component
public class CsvObserver extends DefaultObserver {
    private final EmployeeDAO mongoEmployeeDAO;

    public CsvObserver(EmployeeDAO mongoEmployeeDAO) {
        this.mongoEmployeeDAO = mongoEmployeeDAO;
    }

    @Override
    public void onNext(Object o) {
        EmployeeInfo employeeInfo = (EmployeeInfo) o;
//        mongoEmployeeDAO.save(employeeInfo);
    }

    @Override
    public void onError(Throwable e) {
        System.out.println("e = " + e);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
