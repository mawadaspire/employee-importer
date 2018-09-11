package jo.aspire.task.schedulers;

import jo.aspire.task.migrate.DataMigrater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AppScedulers {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppScedulers.class);

    @Autowired
    private DataMigrater dataMigrater;


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void reportCurrentTime() {
        LOGGER.info("starting migrating data at " + LocalDateTime.now().toString());
        dataMigrater.migrate();
        LOGGER.info("finished migrating data at " + LocalDateTime.now().toString());

    }
}