package jo.aspire.task.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "employees")
public class EmployeeDocument  {

    @Id
    private String id;
    private long employeeId;
    private String employeeName;
    private double salary;
    private String status;
    private String birthDate;
    private String degree;
    private boolean isMigrated;


    public boolean isMigrated() {
        return isMigrated;
    }

    public void setMigrated(boolean migrated) {
        isMigrated = migrated;
    }

    @DBRef
    private List<AddressDocument> address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<AddressDocument> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDocument> address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeDocument)) return false;
        EmployeeDocument that = (EmployeeDocument) o;
        return getEmployeeId() == that.getEmployeeId() &&
                Double.compare(that.getSalary(), getSalary()) == 0 &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getEmployeeName(), that.getEmployeeName()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getBirthDate(), that.getBirthDate()) &&
                Objects.equals(getDegree(), that.getDegree()) &&
                Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployeeId(), getEmployeeName(), getSalary(), getStatus(), getBirthDate(), getDegree(), getAddress());
    }
}
