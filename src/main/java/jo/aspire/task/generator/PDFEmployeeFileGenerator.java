package jo.aspire.task.generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jo.aspire.task.dto.DownloadFileData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PDFEmployeeFileGenerator implements EmployeeFileGenerator {

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);


    @Override
    public void generate(List<DownloadFileData> downloadFileDataList, OutputStream outputStream) throws IOException, DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        addContent(document, downloadFileDataList);
        document.close();

    }

    private static void addContent(Document document, List<DownloadFileData> downloadFileDataList) throws DocumentException {
        Anchor anchor = new Anchor("First Chapter", catFont);
        anchor.setName("First Chapter");


        Chapter main = new Chapter(1);


        createTable(main, downloadFileDataList);

        document.add(main);


    }

    private static void createTable(Section subCatPart, List<DownloadFileData> downloadFileDataList)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        PdfPCell c1 = new PdfPCell(new Phrase("Employee Name"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Salary"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Yearly Salary"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);


        downloadFileDataList.forEach(data -> {
            table.addCell(data.getName());
            table.addCell(data.getSalary() + "");
            table.addCell(data.getSalary() * 12 + "");
        });


        subCatPart.add(table);

    }
}
