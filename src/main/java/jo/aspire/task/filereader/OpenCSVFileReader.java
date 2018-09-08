package jo.aspire.task.filereader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import jo.aspire.task.dto.EmployeeDTO;
import jo.aspire.task.entities.EmployeeInfo;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Iterator;

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
}
