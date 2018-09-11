package jo.aspire.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendEmailImpl implements SendEmail {

    private static final Logger LOGGER= LoggerFactory.getLogger(SendEmailImpl.class);
    @Override
    public void send(Long numberOfRecords) {
        LOGGER.info("Email Sent");

    }
}
