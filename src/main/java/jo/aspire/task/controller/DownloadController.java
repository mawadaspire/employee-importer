package jo.aspire.task.controller;

import com.itextpdf.text.DocumentException;
import jo.aspire.task.CsvObserver;
import jo.aspire.task.dao.EmployeeDAO;
import jo.aspire.task.dto.DownloadFileData;
import jo.aspire.task.generator.ExcelEmployeeFileGenerator;
import jo.aspire.task.generator.PDFEmployeeFileGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/download")
public class DownloadController {


    @Autowired
    @Qualifier("rdbms")
    private EmployeeDAO employeeDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvObserver.class);


    @GetMapping("/{type}")
    public void downloadExcel(@PathVariable("type") String type, HttpServletResponse response) {
        Optional<List<DownloadFileData>> allToDownload = employeeDAO.findAllToDownload();
        try {
        if ("excel".equalsIgnoreCase(type)) {
            response.setContentType("application/xls");
            response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");


                if (allToDownload.isPresent())
                    new ExcelEmployeeFileGenerator().generate(allToDownload.get(), response.getOutputStream());



        } else if ("pdf".equalsIgnoreCase(type)) {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=employees.pdf");
            if (allToDownload.isPresent())
                new PDFEmployeeFileGenerator().generate(allToDownload.get(), response.getOutputStream());
        } else {
            try {
                response.getWriter().write("Unsupported requested Format");
            } catch (IOException e) {
                LOGGER.error("Error While Writing Writing response for invalid requested format");
            }
        }
        } catch (IOException e) {
            LOGGER.error("Error While Writing "+type+" File " + e.getMessage());
        } catch (DocumentException e) {
            LOGGER.error("Error While Writing "+type+" File " + e.getMessage());
        }
    }


}
