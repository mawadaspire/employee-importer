package jo.aspire.task.filereader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import jo.aspire.task.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class OpenCSVFileReader implements CsvFileReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenCSVFileReader.class);


    /**
     * parse uploaded csv file and parse it then return its records using observer
     * @param file the uploaded csv file
     * @param csvObserver observer to return values in it
     * @throws InterruptedException
     */
    @Override
    public void employees(File file, Observer csvObserver) throws InterruptedException {
        LOGGER.info("Starting parsing " + file.getName());
        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
            CsvToBean<EmployeeDTO> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(EmployeeDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Observable<Object> observable = Observable.create(sub -> {
                Iterator<EmployeeDTO> iterator = csvToBean.iterator();

                LOGGER.info("Start Sending records to obserable");
                iterator.forEachRemaining(employeeInfo -> {
                    employeeInfo.setAddressesList(createAddressesList(employeeInfo.getAddress()));
                    sub.onNext(employeeInfo);
                });
                if (!iterator.hasNext()) {
                    LOGGER.info("Finshed parsing " + file.getName());
                    sub.onComplete();
                }
            });
            observable.subscribe(csvObserver);

        } catch (IOException e) {
          LOGGER.error(e.getMessage());
        }
    }

    private List<String> createAddressesList(String address) {
        String strippedString=address.substring(1,address.length()-1);
        String[] addresses = strippedString.split("-");
        return  Arrays.asList(addresses);
    }
}
