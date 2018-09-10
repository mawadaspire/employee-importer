package jo.aspire.task.generator;

import com.itextpdf.text.DocumentException;
import jo.aspire.task.dto.DownloadFileData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface EmployeeFileGenerator {

    void generate(List<DownloadFileData> downloadFileDataList, OutputStream outputStream) throws IOException, DocumentException;
}
