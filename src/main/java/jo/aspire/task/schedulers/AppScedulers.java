package jo.aspire.task.schedulers;

import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.migrate.DataMigrater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AppScedulers {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppScedulers.class);

    @Autowired
    @Qualifier("dailyMigrater")
    private DataMigrater dataMigrater;




    @Autowired
    @Qualifier("failedItems")
    private DataMigrater failedItems;


    @Autowired
    @Qualifier("mongo")
    private EmployeeDAO fromEmployeeDAO;

    @Autowired
    @Qualifier("rdbms")
    private EmployeeDAO toEmployeeDAO;



    @Scheduled(cron = "0 0 0 * * ?")
    public void migrateEveryMidNight() {
        LOGGER.info("starting migrating data at " + LocalDateTime.now().toString());
        dataMigrater.migrate(fromEmployeeDAO,toEmployeeDAO);
        LOGGER.info("finished migrating data at " + LocalDateTime.now().toString());
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void migrateFailedItemsEveryHour() {
        LOGGER.info("starting migrating Failed data at " + LocalDateTime.now().toString());
        failedItems.migrate(fromEmployeeDAO,toEmployeeDAO);
        LOGGER.info("finished migrating Failed data at " + LocalDateTime.now().toString());
    }
}