package jo.aspire.task.migrate;

import jo.aspire.task.dao.EmployeeDAO;

public interface DataMigrater {

    void migrate(EmployeeDAO from,EmployeeDAO to);
}
