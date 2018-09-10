package jo.aspire.task.dto;

public class DownloadFileData {
    private String name;
    private double salary;
    private double yearlySalary;

    public DownloadFileData() {
    }

    public DownloadFileData(String name, double salary, double yearlySalary) {
        this.name = name;
        this.salary = salary;
        this.yearlySalary = yearlySalary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getYearlySalary() {
        return yearlySalary;
    }
    public void setYearlySalary(double yearlySalary) {
        this.yearlySalary = yearlySalary;
    }

}
