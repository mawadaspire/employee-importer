package jo.aspire.task.filereader;

import io.reactivex.Observer;
import jo.aspire.task.CsvObserver;

import java.io.File;

public interface CsvFileReader {
    void employees( File tempFile, Observer csvObserver) throws InterruptedException;
}
