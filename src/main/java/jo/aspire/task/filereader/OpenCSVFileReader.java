package jo.aspire.task.filereader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.EmployeeInfo;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class OpenCSVFileReader implements CsvFileReader {


    @Override
    public void employees(File tempFile, Observer csvObserver) throws InterruptedException {

        try (Reader reader = new InputStreamReader(new FileInputStream(tempFile))) {
            CsvToBean<EmployeeDTO> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(EmployeeDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            Observable<Object> observable = Observable.create(sub -> {
                Iterator<EmployeeDTO> iterator = csvToBean.iterator();
                iterator.forEachRemaining(employeeInfo -> {
                    employeeInfo.setAddressesList(createAddressesList(employeeInfo.getAddress()));
                    sub.onNext(employeeInfo);
                });
                if (!iterator.hasNext())
                    sub.onComplete();
            });
            observable.subscribe(csvObserver);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> createAddressesList(String address) {
        String strippedString=address.substring(1,address.length()-1);
        String[] addresses = strippedString.split("-");
        return  Arrays.asList(addresses);
    }
}
