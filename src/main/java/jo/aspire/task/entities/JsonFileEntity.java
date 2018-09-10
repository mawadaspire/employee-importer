package jo.aspire.task.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class JsonFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String fileContent;

    public JsonFileEntity() {
    }

    public JsonFileEntity(String fileContent) {
        this.fileContent = fileContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonFileEntity)) return false;
        JsonFileEntity that = (JsonFileEntity) o;
        return getId() == that.getId() &&
                Objects.equals(getFileContent(), that.getFileContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFileContent());
    }
}
