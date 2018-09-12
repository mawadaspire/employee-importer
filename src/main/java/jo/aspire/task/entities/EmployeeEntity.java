package jo.aspire.task.entities;


import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
//@Table(schema = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    List<AddressEntity> addressEntities;

    private long employeeId;
    @Column(unique = true)
    private String employeeName;
    private double salary;
    private String status;
    private String birthDate;
    private String degree;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<AddressEntity> getAddressEntities() {
        return addressEntities;
    }

    public void setAddressEntities(List<AddressEntity> addressEntities) {
        this.addressEntities = addressEntities;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeEntity)) return false;
        EmployeeEntity that = (EmployeeEntity) o;
        return getId() == that.getId() &&
                getEmployeeId() == that.getEmployeeId() &&
                Double.compare(that.getSalary(), getSalary()) == 0 &&
                Objects.equals(getAddressEntities(), that.getAddressEntities()) &&
                Objects.equals(getEmployeeName(), that.getEmployeeName()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getBirthDate(), that.getBirthDate()) &&
                Objects.equals(getDegree(), that.getDegree());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddressEntities(), getEmployeeId(), getEmployeeName(), getSalary(), getStatus(), getBirthDate(), getDegree());
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" +
                "id=" + id +
                ", addressEntities=" + addressEntities +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", degree='" + degree + '\'' +
                '}';
    }
}
