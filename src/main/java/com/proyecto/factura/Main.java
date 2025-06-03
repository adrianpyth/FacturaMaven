package com.proyecto.factura;

import com.proyecto.factura.correo.LectorCorreo;
import com.proyecto.factura.scanner.LectorPDF;
import com.proyecto.factura.scanner.XMLReader;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // 1. Conexión a la base de datos
        Connection conexion = null;
        try {
            String url = "jdbc:sqlserver://DESKTOP-ESQFILG:1433;databaseName=FacturaDB;encrypt=true;trustServerCertificate=true;";
            String usuario = "facturaUser";
            String contrasena = "Factura2025";

            conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("✅ Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos:");
            e.printStackTrace();
        }

        // 2. Descargar archivos del correo
        System.out.println("📥 Conectando a Gmail y descargando archivos adjuntos...");
        LectorCorreo lectorCorreo = new LectorCorreo();
        lectorCorreo.leerYDescargarAdjuntos();

        // 3. Analizar carpeta 'descargas'
        File carpeta = new File("descargas");
        LectorPDF lectorPDF = new LectorPDF();

        if (carpeta.exists() && carpeta.isDirectory()) {
            // 3.1. Leer PDFs
            System.out.println("\n📂 Analizando archivos PDF...");
            File[] archivosPDF = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (archivosPDF != null && archivosPDF.length > 0) {
                for (File archivo : archivosPDF) {
                    System.out.println("\n📄 Leyendo archivo: " + archivo.getName());
                    String texto = lectorPDF.leerTextoDesdePDF(archivo.getPath());

                    if (texto != null && !texto.isBlank()) {
                        System.out.println("📝 Contenido:\n" + texto);

                        String consecutivo = lectorPDF.extraerConsecutivo(texto);
                        if (consecutivo != null) {
                            System.out.println("🔢 Consecutivo detectado: " + consecutivo);
                        } else {
                            System.out.println("⚠️ No se encontró un consecutivo.");
                        }

                        String montoTotal = lectorPDF.extraerMontoTotal(texto);
                        if (montoTotal != null) {
                            System.out.println("💰 Monto total detectado: " + montoTotal);
                        } else {
                            System.out.println("⚠️ No se detectó el monto total.");
                        }

                        if (lectorPDF.contienePalabraClaveFinanciera(texto)) {
                            System.out.println("🔍 Palabra clave financiera detectada (SINPE, transferencia, cuenta, etc).");
                        }
                    } else {
                        System.out.println("⚠️ El archivo PDF está vacío o no se pudo leer.");
                    }
                }
            } else {
                System.out.println("📭 No se encontraron archivos PDF en la carpeta.");
            }

            // 3.2. Leer XMLs
            System.out.println("\n📂 Analizando archivos XML...");
            File[] archivosXML = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

            if (archivosXML != null && archivosXML.length > 0) {
                if (conexion != null) {
                    XMLReader lectorXML = new XMLReader(conexion);
                    for (File archivoXML : archivosXML) {
                        System.out.println("\n📄 Leyendo archivo XML: " + archivoXML.getName());
                        lectorXML.leerXML(archivoXML.getPath());
                    }
                } else {
                    System.out.println("❌ No se pudo procesar XMLs por falta de conexión a la base de datos.");
                }
            } else {
                System.out.println("📭 No se encontraron archivos XML en la carpeta.");
            }

        } else {
            System.out.println("❌ La carpeta 'descargas' no existe o no es un directorio.");
        }

        // 4. Cerrar conexión
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("🔒 Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar la conexión:");
                e.printStackTrace();
            }
        }
    }
}
