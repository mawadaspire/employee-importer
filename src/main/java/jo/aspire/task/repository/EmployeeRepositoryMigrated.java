package jo.aspire.task.repository;

public interface EmployeeRepositoryMigrated {

    long updateEmployeeIsMigrated(boolean isMigrated, long employeeId);

}