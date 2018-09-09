package jo.aspire.task.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "address")
public class AddressDocument {
    @Id
    private String id;
    private String employeeDocumentId;
    private String address;


    public AddressDocument() {
    }

    public String getEmployeeDocumentId() {
        return employeeDocumentId;
    }

    public void setEmployeeDocumentId(String employeeDocumentId) {
        this.employeeDocumentId = employeeDocumentId;
    }


    public AddressDocument(String address) {
        this.address = address;
    }

    public AddressDocument(String employeeDocumentId, String address) {
        this.employeeDocumentId = employeeDocumentId;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressDocument)) return false;
        AddressDocument that = (AddressDocument) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getEmployeeDocumentId(), that.getEmployeeDocumentId()) &&
                Objects.equals(getAddress(), that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployeeDocumentId(), getAddress());
    }
}
