package jo.aspire.task.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
//@Table(schema = "faield-employees")
public class FailedEmployees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private String employeeData;

    public FailedEmployees() {
    }

    public FailedEmployees(String employeeData) {
        this.employeeData = employeeData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployeeData() {
        return employeeData;
    }

    public void setEmployeeData(String employeeData) {
        this.employeeData = employeeData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailedEmployees)) return false;
        FailedEmployees that = (FailedEmployees) o;
        return getId() == that.getId() &&
                Objects.equals(getEmployeeData(), that.getEmployeeData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployeeData());
    }
}
