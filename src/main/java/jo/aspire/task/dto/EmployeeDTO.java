package jo.aspire.task.dto;

import com.opencsv.bean.CsvBindByPosition;

public class EmployeeDTO {

    @CsvBindByPosition(position = 0)
    private long employeeId;
    @CsvBindByPosition(position = 1)
    private String employeeName;
    @CsvBindByPosition(position = 2)
    private double salary;
    @CsvBindByPosition(position = 3)
    private String status;
    @CsvBindByPosition(position = 4)
    private String birthDate;
    @CsvBindByPosition(position = 5)
    private String Degree;
    @CsvBindByPosition(position = 6)
    private String address;

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
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", Degree='" + Degree + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
