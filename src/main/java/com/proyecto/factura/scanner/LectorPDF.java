package com.proyecto.factura.scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorPDF {

    public String leerTextoDesdePDF(String rutaArchivo) {
        try (PDDocument documento = PDDocument.load(new File(rutaArchivo))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        } catch (IOException e) {
            System.err.println("❌ Error leyendo PDF: " + rutaArchivo);
            e.printStackTrace();
            return null;
        }
    }

    public String extraerConsecutivo(String texto) {
        if (texto == null || texto.isBlank()) return null;

        // Regex: busca un grupo de exactamente 20 dígitos consecutivos
        Pattern patron = Pattern.compile("\\b\\d{20}\\b");
        Matcher matcher = patron.matcher(texto);

        if (matcher.find()) {
            return matcher.group(); // Devuelve el primer consecutivo encontrado
        }

        return null; // Si no se encuentra
    }

    public String extraerMontoTotal(String texto) {
        if (texto == null) return null;

        // Busca una línea tipo: "Total: 9.500,00" o "Total a pagar: ₡9500.00"
        Pattern patron = Pattern.compile("(total\\s*(a pagar)?[:]?\\s*([₡\\d.,]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patron.matcher(texto);

        if (matcher.find()) {
            return matcher.group(3); // devuelve solo el número
        }

        return null;
    }

    public boolean contienePalabraClaveFinanciera(String texto) {
        if (texto == null) return false;

        texto = texto.toLowerCase();
        return texto.contains("sinpe") || texto.contains("transferencia") || texto.contains("iban") || texto.contains("cuenta");
    }



}
