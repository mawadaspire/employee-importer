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
    private String employee;

    public FailedEmployees() {
    }

    public FailedEmployees(String employee) {
        this.employee = employee;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FailedEmployees)) return false;
        FailedEmployees that = (FailedEmployees) o;
        return getId() == that.getId() &&
                Objects.equals(getEmployee(), that.getEmployee());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployee());
    }
}
