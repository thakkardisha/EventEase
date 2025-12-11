package beans;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import entity.Bookings;
import entity.Tickets;
import ejb.interfaces.user.UserInterface;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Named
@ViewScoped
public class TicketDownloadBean implements Serializable {

    @EJB
    private UserInterface userBean;

    public void downloadTickets(Integer bookingId) {
        try {
            System.out.println("========== Downloading Tickets for Booking ID: " + bookingId + " ==========");

            // Get booking details
            Bookings booking = userBean.getAllBookings().stream()
                    .filter(b -> b.getbId().equals(bookingId))
                    .findFirst()
                    .orElse(null);

            if (booking == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Booking not found");
                return;
            }

            // Get tickets for this booking
            List<Tickets> tickets = userBean.getAllTickets().stream()
                    .filter(t -> t.getbId().getbId().equals(bookingId))
                    .toList();

            if (tickets.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "No Tickets", "No tickets found for this booking");
                return;
            }

            // Generate PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add tickets to PDF
            for (int i = 0; i < tickets.size(); i++) {
                Tickets ticket = tickets.get(i);
                addTicketToDocument(document, ticket, booking);

                // Add page break if not last ticket
                if (i < tickets.size() - 1) {
                    document.newPage();
                }
            }

            document.close();

            // Download PDF
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.responseReset();
            ec.setResponseContentType("application/pdf");
            ec.setResponseHeader("Content-Disposition",
                    "attachment; filename=\"tickets_booking_" + bookingId + ".pdf\"");
            ec.setResponseContentLength(baos.size());
            ec.getResponseOutputStream().write(baos.toByteArray());
            FacesContext.getCurrentInstance().responseComplete();

            System.out.println("Tickets PDF generated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Failed to generate tickets: " + e.getMessage());
        }
    }

    private void addTicketToDocument(Document document, Tickets ticket, Bookings booking)
            throws Exception {

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(168, 155, 191));
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        // Add border
        PdfPTable borderTable = new PdfPTable(1);
        borderTable.setWidthPercentage(90);
        borderTable.getDefaultCell().setBorder(Rectangle.BOX);
        borderTable.getDefaultCell().setBorderColor(new BaseColor(168, 155, 191));
        borderTable.getDefaultCell().setBorderWidth(2);
        borderTable.getDefaultCell().setPadding(20);

        // Content table
        PdfPTable contentTable = new PdfPTable(1);
        contentTable.setWidthPercentage(100);
        contentTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Title
        PdfPCell titleCell = new PdfPCell(new Phrase("EVENT TICKET", titleFont));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPaddingBottom(20);
        contentTable.addCell(titleCell);

        // Event Name
        PdfPCell eventNameCell = new PdfPCell(new Phrase(booking.geteId().geteName(), headerFont));
        eventNameCell.setBorder(Rectangle.NO_BORDER);
        eventNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        eventNameCell.setPaddingBottom(20);
        contentTable.addCell(eventNameCell);

        // Ticket Details Table
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 2});
        detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        detailsTable.getDefaultCell().setPaddingBottom(8);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        // Add details
        addDetailRow(detailsTable, "Ticket Number:", ticket.getticketNumber(), labelFont, valueFont);
        addDetailRow(detailsTable, "Date:",
                dateFormat.format(java.sql.Date.valueOf(booking.geteId().geteventDate())),
                labelFont, valueFont);
//        addDetailRow(detailsTable, "Time:", booking.geteId().getstartTime(), labelFont, valueFont);
        addDetailRow(detailsTable, "Venue:",
                booking.geteId().getvId().getvName() + ", " + booking.geteId().getvId().getvCity(),
                labelFont, valueFont);
        addDetailRow(detailsTable, "Ticket Type:", ticket.getticketType(), labelFont, valueFont);
        addDetailRow(detailsTable, "Price:", "₹" + ticket.getprice(), labelFont, valueFont);
        addDetailRow(detailsTable, "Booking Date:",
                dateFormat.format(booking.getbookingDate()), labelFont, valueFont);

        PdfPCell detailsCell = new PdfPCell(detailsTable);
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsCell.setPaddingBottom(20);
        contentTable.addCell(detailsCell);

        // QR Code
        try {
            Image qrImage = generateQRCodeImage(ticket.getqrCode());
            qrImage.scaleAbsolute(150, 150);
            PdfPCell qrCell = new PdfPCell(qrImage);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setPaddingTop(10);
            contentTable.addCell(qrCell);

            // QR Code label
            PdfPCell qrLabel = new PdfPCell(new Phrase("Scan this QR code at the venue", labelFont));
            qrLabel.setBorder(Rectangle.NO_BORDER);
            qrLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrLabel.setPaddingTop(10);
            contentTable.addCell(qrLabel);
        } catch (Exception e) {
            System.out.println("Error generating QR code: " + e.getMessage());
        }

        // Add content to border
        PdfPCell mainCell = new PdfPCell(contentTable);
        mainCell.setBorder(Rectangle.NO_BORDER);
        borderTable.addCell(mainCell);

        document.add(borderTable);

        // Footer
        Paragraph footer = new Paragraph("\nPlease present this ticket at the venue entrance", labelFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
    }

    private void addDetailRow(PdfPTable table, String label, String value,
            Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(8);
        table.addCell(valueCell);
    }

    private Image generateQRCodeImage(String qrCodeData) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 300, 300);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return Image.getInstance(baos.toByteArray());
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}
