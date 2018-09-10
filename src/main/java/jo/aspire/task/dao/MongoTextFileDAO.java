package jo.aspire.task.dao;

import jo.aspire.task.entities.TextFileDocument;
import jo.aspire.task.repository.MongoTextFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoTextFileDAO implements TextFileDAO {

    @Autowired
    private MongoTextFileRepository mongoTextFileRepository;

    @Override
    public void save(String textContent) {
        mongoTextFileRepository.save(new TextFileDocument(textContent));
    }
}
