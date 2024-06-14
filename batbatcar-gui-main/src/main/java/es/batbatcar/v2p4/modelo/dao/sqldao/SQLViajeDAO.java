package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dto.viaje.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Repository
public class SQLViajeDAO implements ViajeDAO {

    private static final String DATABASE_TABLE = "viajes";

    @Autowired
    private MySQLConnection mySQLConnection;

    @Override
    public Set<Viaje> findAll() {
        Set<Viaje> viajes = new HashSet<>();
        String sql = String.format("Select * FROM %s", DATABASE_TABLE);
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Viaje viaje = mapToViaje(resultSet);
                viajes.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(String city) {
        Set<Viaje> viajes = new HashSet<>();
        String sql = String.format("Select * FROM %s WHERE ruta LIKE '%%-%s'", DATABASE_TABLE, city);
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Viaje viaje = mapToViaje(resultSet);
                viajes.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(EstadoViaje estadoViaje) {
        Set<Viaje> viajes = new HashSet<>();
        String sql = String.format("Select * FROM %s WHERE estadoViaje = '%s'", DATABASE_TABLE, estadoViaje);
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Viaje viaje = mapToViaje(resultSet);
                viajes.add(viaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Viaje findById(int codViaje) {
        Viaje viaje = null;
        String sql = String.format("Select * FROM %s WHERE codViaje = %d", DATABASE_TABLE, codViaje);
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                viaje = mapToViaje(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viaje;
    }

    @Override
    public Viaje getById(int codViaje) throws ViajeNotFoundException {
        Viaje viaje = null;
        String sql = String.format("Select * FROM %s WHERE codViaje = %d", DATABASE_TABLE, codViaje);
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                viaje = mapToViaje(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viaje;
    }

    @Override
    public void add(Viaje viaje) {
        int cod = viaje.getCodViaje();
        String propietario = viaje.getPropietario();
        String ruta = viaje.getRuta();
        LocalDateTime fechaSalida = viaje.getFechaSalida();
        long duracion = viaje.getDuracion();
        float precio = viaje.getPrecio();
        int plazas = viaje.getPlazasOfertadas();
        EstadoViaje estado = viaje.getEstado();

        String sql = "INSERT INTO viajes (codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cod);
            ps.setString(2, propietario);
            ps.setString(3, ruta);
            ps.setTimestamp(4, Timestamp.valueOf(fechaSalida));
            ps.setLong(5, duracion);
            ps.setFloat(6, precio);
            ps.setInt(7, plazas);
            ps.setString(8, estado.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void update(Viaje viaje) throws ViajeNotFoundException {
        String sql = "UPDATE viajes SET propietario = ?, ruta = ?, fechaSalida = ?, duracion = ?, precio = ?, plazasOfertadas = ?, estadoViaje = ? WHERE codViaje = ?";

        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, viaje.getPropietario());
            ps.setString(2, viaje.getRuta());
            ps.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
            ps.setLong(4, viaje.getDuracion());
            ps.setFloat(5, viaje.getPrecio());
            ps.setInt(6, viaje.getPlazasOfertadas());
            ps.setString(7, viaje.getEstado().toString());
            ps.setInt(8, viaje.getCodViaje());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ViajeNotFoundException("No se encontró ningún viaje con el código: " + viaje.getCodViaje());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Viaje viaje) throws ViajeNotFoundException {
        String sql = "DELETE FROM viajes WHERE codViaje = ?";

        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, viaje.getCodViaje());

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0) {
                throw new ViajeNotFoundException("No se encontró ningún viaje con el código: " + viaje.getCodViaje());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLastID() {
        int maxCod = 1;
        String sql = "SELECT MAX(codViaje) AS codViaje FROM viajes";
        try (Connection connection = mySQLConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                maxCod = resultSet.getInt("codViaje");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxCod;
    }

    private Viaje mapToViaje(ResultSet resultSet) throws SQLException {
        int cod = resultSet.getInt("codViaje");
        String propietario = resultSet.getString("propietario");
        String ruta = resultSet.getString("ruta");
        LocalDateTime fechaSalida = resultSet.getTimestamp("fechaSalida").toLocalDateTime();
        int duracion = resultSet.getInt("duracion");
        BigDecimal precio1 = resultSet.getBigDecimal("precio");
        float precio = precio1.floatValue();
        int plazasOfertadas = resultSet.getInt("plazasOfertadas");
        String estadoViajeString = resultSet.getString("estadoViaje");
        EstadoViaje estadoViaje = EstadoViaje.parse(estadoViajeString);
        Viaje viaje = new Viaje(cod, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);

        return viaje;
    }

}