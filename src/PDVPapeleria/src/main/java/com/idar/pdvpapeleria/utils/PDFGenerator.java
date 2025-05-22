package com.idar.pdvpapeleria.utils;

import VO.ProductoVO;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PDFGenerator {

    private static List<String> dividirNombre(String nombre, int maxLength) {
        List<String> partes = new ArrayList<>();
        if (nombre.length() <= maxLength) {
            partes.add(nombre);
            return partes;
        }
        
        int ultimoEspacio = nombre.substring(0, maxLength).lastIndexOf(' ');
        
        if (ultimoEspacio <= 0) {
            partes.add(nombre.substring(0, maxLength));
            partes.addAll(dividirNombre(nombre.substring(maxLength), maxLength));
        } else {
            partes.add(nombre.substring(0, ultimoEspacio).trim());
            partes.addAll(dividirNombre(nombre.substring(ultimoEspacio + 1), maxLength));
        }
        
        return partes;
    }
    
    private static void writeText(PDPageContentStream contentStream, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void generarTicketVenta(List<ProductoVO> productos, int folio, int total, String cajero) throws IOException {
        File ticketsDir = new File("Tickets");
        if (!ticketsDir.exists()) {
            ticketsDir.mkdir();
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            // Variable para controlar la posición Y
            float currentY = 700;
            
            // Crear primer contentStream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                // Configuración inicial
                contentStream.setFont(PDType1Font.COURIER_BOLD, 14);
                
                // Encabezado del ticket
                writeText(contentStream, 100, currentY, "PAPELERÍA IDAR");
                currentY -= 20;
                writeText(contentStream, 100, currentY, "Ticket #" + folio);
                currentY -= 20;
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                writeText(contentStream, 100, currentY, "Fecha: " + dtf.format(LocalDateTime.now()));
                currentY -= 20;
                writeText(contentStream, 100, currentY, "Cajero: " + cajero);
                currentY -= 30;

                contentStream.setFont(PDType1Font.COURIER, 14);
                writeText(contentStream, 100, currentY, "--------------------------------------------------");
                currentY -= 20;
                writeText(contentStream, 100, currentY, String.format("%-14s %5s %8s %10s", 
                        "Producto", "Cant.", "P.Unit.", "Subtotal"));
                currentY -= 20;
                writeText(contentStream, 100, currentY, "--------------------------------------------------");
                currentY -= 20;

                for (ProductoVO producto : productos) {
                    List<String> nombrePartes = dividirNombre(producto.getNombre(), 14);
                    
                    writeText(contentStream, 100, currentY, 
                        String.format("%-14s %5d %8d %10d", 
                            nombrePartes.get(0),
                            producto.getCantidad(),
                            producto.getPrecioDeVenta(),
                            producto.getSubtotal()));
                    currentY -= 15;
                    
                    for (int i = 1; i < nombrePartes.size(); i++) {
                        writeText(contentStream, 102, currentY, nombrePartes.get(i)); // Sangría de 2px
                        currentY -= 15;
                    }
                    
                    currentY -= 5;
                    
                    // Verificar si necesitamos nueva página
                    if (currentY < 50) {
                        // Cerrar el contentStream actual
                        contentStream.close();
                        
                        // Crear nueva página
                        PDPage newPage = new PDPage();
                        document.addPage(newPage);
                        
                        // Crear nuevo contentStream
                        contentStream = new PDPageContentStream(document, newPage);
                        currentY = 700;
                        contentStream.setFont(PDType1Font.COURIER, 12);
                    }
                }

                contentStream.setFont(PDType1Font.COURIER_BOLD, 14);
                writeText(contentStream, 100, currentY, "--------------------------------------------------");
                currentY -= 20;
                writeText(contentStream, 100, currentY, String.format("TOTAL: $%,d", total));
                currentY -= 30;
                writeText(contentStream, 100, currentY, "¡Gracias por su compra!");
            } finally {
                // Asegurarse de cerrar el contentStream
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            String fileName = "Tickets/ticket_" + folio + ".pdf";
            document.save(fileName);
            System.out.println("Ticket generado en: " + new File(fileName).getAbsolutePath());
        }
    }
}