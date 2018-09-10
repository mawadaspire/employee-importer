package jo.aspire.task.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "txtfiles")
public class TextFileDocument {

    @Id
    private String id;
    private String textContent;

    public TextFileDocument() {
    }

    public TextFileDocument(String textContent) {
        this.textContent = textContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileDocument)) return false;
        TextFileDocument that = (TextFileDocument) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getTextContent(), that.getTextContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTextContent());
    }
}
