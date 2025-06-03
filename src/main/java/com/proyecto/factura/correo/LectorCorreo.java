package com.proyecto.factura.correo;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;

import java.io.File;
import java.util.Date;
import java.util.Properties;

public class LectorCorreo {

    public void leerYDescargarAdjuntos() {
        try {
            // Cargar el archivo de configuración desde resources
            Properties config = new Properties();
            config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));

            String usuario = config.getProperty("gmail.user");
            String clave = config.getProperty("gmail.password");
            String carpetaSalida = config.getProperty("output.folder");

            // Crear carpeta de salida si no existe
            File carpeta = new File(carpetaSalida);
            if (!carpeta.exists()) carpeta.mkdirs();

            // Configurar conexión IMAP a Gmail
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");

            Session sesion = Session.getInstance(props);
            Store store = sesion.getStore("imaps");
            store.connect("imap.gmail.com", usuario, clave);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] mensajes = inbox.getMessages();
            System.out.println("📬 Correos encontrados: " + mensajes.length);

            for (Message mensaje : mensajes) {
                Date fechaCorreo = mensaje.getReceivedDate();
                long diferenciaMs = new Date().getTime() - fechaCorreo.getTime();
                long diferenciaDias = diferenciaMs / (1000 * 60 * 60 * 24);

                // Filtro por fecha (últimos 30 días)
                if (diferenciaDias <= 100) {
                    String asunto = mensaje.getSubject();
                    if (asunto != null) {
                        asunto = asunto.toLowerCase();

                        // Filtro por palabras clave en el asunto
                        if (asunto.contains("notificación") || asunto.contains("notificacion")
                                || asunto.contains("transferencia") || asunto.contains("comprobante")
                                || asunto.contains("transacción") || asunto.contains("transaccion")) {

                            if (mensaje.getContent() instanceof Multipart) {
                                Multipart multipart = (Multipart) mensaje.getContent();

                                for (int i = 0; i < multipart.getCount(); i++) {
                                    BodyPart parte = multipart.getBodyPart(i);

                                    if (Part.ATTACHMENT.equalsIgnoreCase(parte.getDisposition())) {
                                        MimeBodyPart adjunto = (MimeBodyPart) parte;
                                        String nombreArchivo = adjunto.getFileName();
                                        File archivoDestino = new File(carpetaSalida + "/" + nombreArchivo);
                                        adjunto.saveFile(archivoDestino);
                                        System.out.println("✅ Descargado: " + nombreArchivo);
                                    }
                                }
                            }

                        } else {
                            System.out.println("📤 Ignorado por asunto: " + asunto);
                        }
                    }

                } else {
                    System.out.println("📭 Ignorado por antigüedad: " + mensaje.getSubject());
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error leyendo correos.");
        }
    }
}
