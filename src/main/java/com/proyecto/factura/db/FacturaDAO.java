package com.proyecto.factura.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class FacturaDAO {
    private final Connection conexion;

    public FacturaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public int insertarFactura(String consecutivo, Date fecha, double total) throws SQLException {
        String sql = "INSERT INTO Factura (consecutivo, fecha, total) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, consecutivo);
            stmt.setDate(2, fecha);
            stmt.setDouble(3, total);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el ID de la factura insertada.");
    }

    public void insertarLineaDetalle(int facturaId, int numeroLinea, String codigo, double cantidad,
                                     String descripcion, double precioUnitario, double tarifaIVA, double montoIVA) throws SQLException {
        String sql = "INSERT INTO LineaDetalle (factura_id, numeroLinea, codigo, cantidad, descripcion, precioUnitario, tarifaIVA, montoIVA) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, facturaId);
            stmt.setInt(2, numeroLinea);
            stmt.setString(3, codigo);
            stmt.setDouble(4, cantidad);
            stmt.setString(5, descripcion);
            stmt.setDouble(6, precioUnitario);
            stmt.setDouble(7, tarifaIVA);
            stmt.setDouble(8, montoIVA);
            stmt.executeUpdate();
        }
    }
}
