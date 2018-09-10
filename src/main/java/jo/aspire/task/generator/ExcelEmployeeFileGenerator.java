package jo.aspire.task.generator;

import jo.aspire.task.dto.DownloadFileData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelEmployeeFileGenerator implements EmployeeFileGenerator {


    public static final String EMPLOYEES = "Employees";
    private static final String[] columns = {"Employee Name", "Salary", "Yearly Salary"};


    @Override
    public void generate(List<DownloadFileData> downloadFileDataList, OutputStream outputStream) throws IOException {
        Workbook workbook = prepareWorkBookWithHeader();
        populateSheetWithData(downloadFileDataList,workbook, outputStream);

    }

    private void populateSheetWithData(List<DownloadFileData> allEmployeeData, Workbook workbook, OutputStream outputStream) throws IOException {



            int rowNum = 1;


            Sheet sheet = workbook.getSheet(EMPLOYEES);

            for (DownloadFileData employeeData : allEmployeeData) {


                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(employeeData.getName());

                row.createCell(1)
                        .setCellValue(employeeData.getSalary());


                row.createCell(1)
                        .setCellValue(employeeData.getSalary() * 12);

            }

            workbook.write(outputStream);

        outputStream.flush();


    }

    private Workbook prepareWorkBookWithHeader() {
        Workbook workbook = new XSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet(EMPLOYEES);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(cellStyle);
        }
        return workbook;
    }
}
