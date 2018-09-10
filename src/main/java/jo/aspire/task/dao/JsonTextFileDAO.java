package jo.aspire.task.dao;

import jo.aspire.task.entities.JsonFileEntity;
import jo.aspire.task.repository.JPAJsonFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonTextFileDAO implements JsonFileDAO {

    @Autowired
    private JPAJsonFileRepository jpaJsonFileRepository;

    @Override
    public void save(String textContent) {
        jpaJsonFileRepository.save(new JsonFileEntity(textContent));
    }
}
