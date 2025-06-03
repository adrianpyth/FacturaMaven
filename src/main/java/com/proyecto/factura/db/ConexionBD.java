package com.proyecto.factura.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:sqlserver://DESKTOP-ESQFILG:1433;databaseName=FacturaDB";
    private static final String USUARIO = "facturaUser";
    private static final String CONTRASENA = "Factura2025";

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos.");
            e.printStackTrace();
            return null;
        }
    }
}
