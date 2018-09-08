package jo.aspire.task.filereader;

import io.reactivex.observers.DefaultObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsvFileReaderTest {

    private CsvFileReader csvFileReader;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        csvFileReader = new OpenCSVFileReader();
    }

    @Test
    public void givenCsvEmployeeContentWhenReadThenEmployeesShouldBeParsed() throws Exception {
        final int[] recordsRecived = {0};
        final int[] errorsCount = {0};
        final boolean[] completed = {false};
        csvFileReader.employees(createFile(), new DefaultObserver() {
            @Override
            public void onNext(Object o) {
                recordsRecived[0] += 1;
            }

            @Override
            public void onError(Throwable e) {
                errorsCount[0] += 1;
            }

            @Override
            public void onComplete() {
                completed[0] = true;
            }
        });
        assertEquals(4, recordsRecived[0]);
        assertEquals(0, errorsCount[0]);
        assertTrue(completed[0]);
    }

    private File createFile() throws IOException {
        File tempFile = testFolder.newFile("test.csv");
        Files.write(Paths.get(tempFile.getAbsolutePath()), createFileContent());
        return tempFile;
    }

    private byte[] createFileContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1,Mohd,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("2,Fadi,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("3,Ahmad,9999,M,12/11/1994,B,Amman\n");
        stringBuilder.append("4,Yazan,9999,M,12/11/1994,B,Amman");
        return stringBuilder.toString().getBytes();
    }
}