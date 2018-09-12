package jo.aspire.task.repository;

import com.mongodb.client.result.UpdateResult;
import jo.aspire.task.entities.EmployeeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

//Impl postfix of the name on it compared to the core repository interface
public class EmployeeRepositoryMigratedImpl implements EmployeeRepositoryMigrated {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public long updateEmployeeIsMigrated(boolean isMigrated, long employeeId) {

        Query query = new Query(Criteria.where("employeeId").is(employeeId));
        Update update = new Update();
        update.set("isMigrated", isMigrated);

        UpdateResult result = mongoTemplate.updateFirst(query, update, EmployeeDocument.class);

        if(result!=null)
            return result.getMatchedCount();
        else
            return 0;

    }
}