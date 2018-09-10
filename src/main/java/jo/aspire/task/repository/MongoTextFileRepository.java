package jo.aspire.task.repository;

import jo.aspire.task.entities.TextFileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoTextFileRepository extends MongoRepository<TextFileDocument,String> {
}
