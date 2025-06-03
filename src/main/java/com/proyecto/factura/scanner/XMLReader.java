package com.proyecto.factura.scanner;

import com.proyecto.factura.db.FacturaDAO;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class XMLReader {
    private final Connection conexion;

    public XMLReader(Connection conexion) {
        this.conexion = conexion;
    }

    public void leerXML(String rutaArchivo) {
        try {
            File archivoXML = new File(rutaArchivo);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivoXML);
            documento.getDocumentElement().normalize();
            String ns = documento.getDocumentElement().getNamespaceURI();

            // Datos generales de la factura
            String consecutivo = getTagNS(documento, ns, "NumeroConsecutivo");
            String fechaStr = getTagNS(documento, ns, "FechaEmision");
            String montoStr = getTagNS(documento, ns, "TotalComprobante");
            Date fecha = null;

            if (fechaStr != null) {
                try {
                    fecha = Date.valueOf(LocalDate.parse(fechaStr.substring(0, 10)));
                } catch (DateTimeParseException e) {
                    System.out.println("⚠️ Fecha inválida, se dejará como NULL.");
                }
            }

            double montoTotal = montoStr != null ? Double.parseDouble(montoStr) : 0.0;

            System.out.println("🔑 Consecutivo: " + consecutivo);
            System.out.println("📅 Fecha: " + fecha);
            System.out.println("💰 Monto total: " + montoTotal);

            FacturaDAO facturaDAO = new FacturaDAO(conexion);
            int idFactura = facturaDAO.insertarFactura(consecutivo, fecha, montoTotal);

            NodeList detalleList = documento.getElementsByTagNameNS(ns, "LineaDetalle");
            for (int i = 0; i < detalleList.getLength(); i++) {
                Node nodo = detalleList.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nodo;
                    int numeroLinea = Integer.parseInt(getTagNS(el, ns, "NumeroLinea"));
                    String codigo = getTagNS(el, ns, "Codigo");
                    double cantidad = Double.parseDouble(getTagNS(el, ns, "Cantidad"));
                    String descripcion = getTagNS(el, ns, "Detalle");
                    double precio = Double.parseDouble(getTagNS(el, ns, "PrecioUnitario"));

                    Element impuesto = (Element) el.getElementsByTagNameNS(ns, "Impuesto").item(0);
                    double tarifa = Double.parseDouble(getTagNS(impuesto, ns, "Tarifa"));
                    double montoIVA = Double.parseDouble(getTagNS(impuesto, ns, "Monto"));

                    facturaDAO.insertarLineaDetalle(idFactura, numeroLinea, codigo, cantidad, descripcion, precio, tarifa, montoIVA);
                }
            }

            System.out.println("✅ Factura y detalles guardados en base de datos.");

        } catch (Exception e) {
            System.err.println("❌ Error leyendo XML: " + rutaArchivo);
            e.printStackTrace();
        }
    }

    private String getTagNS(Document doc, String ns, String tag) {
        NodeList nodos = doc.getElementsByTagNameNS(ns, tag);
        return nodos.getLength() > 0 ? nodos.item(0).getTextContent() : null;
    }

    private String getTagNS(Element el, String ns, String tag) {
        NodeList nodos = el.getElementsByTagNameNS(ns, tag);
        return nodos.getLength() > 0 ? nodos.item(0).getTextContent() : "0.0";
    }
}
