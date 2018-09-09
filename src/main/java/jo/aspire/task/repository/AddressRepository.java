package jo.aspire.task.repository;

import jo.aspire.task.entities.AddressDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends MongoRepository<AddressDocument,String> {
}
