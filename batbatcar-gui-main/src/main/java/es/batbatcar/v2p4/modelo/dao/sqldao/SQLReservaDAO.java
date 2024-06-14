package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.exceptions.DatabaseConnectionException;
import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SQLReservaDAO implements ReservaDAO {

	private static final String DATABASE_TABLE = "reservas";

	@Autowired
	private MySQLConnection mySQLConnection;

	@Override
	public Set<Reserva> findAll() {
		String sql = String.format("SELECT * FROM %s", DATABASE_TABLE);
		Set<Reserva> reservas = new HashSet<>();

		Connection connection = mySQLConnection.getConnection();

		try (PreparedStatement ps = connection.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

			}
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}

		return reservas;
	}

	@Override
	public Reserva findById(String id) {
		String sql = String.format("SELECT * FROM %s WHERE codigoReserva = ? ", DATABASE_TABLE);

		Connection connection = mySQLConnection.getConnection();

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {

			}

		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}
		return null;
	}

	@Override
	public ArrayList<Reserva> findAllByUser(String user) {
		String sql = String.format("SELECT * FROM %s WHERE usuario = ? ", DATABASE_TABLE);

		Connection connection = mySQLConnection.getConnection();

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, user);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {

			}

		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}
		return null;
	}

	@Override
	public ArrayList<Reserva> findAllByTravel(Viaje viaje) {
		String sql = String.format("SELECT * FROM %s WHERE viaje = ?", DATABASE_TABLE);
		ArrayList<Reserva> reservas = new ArrayList<>();

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Reserva reserva = mapResultSetToReserva(rs);
				reservas.add(reserva);
			}
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}

		return reservas;
	}

	@Override
	public Reserva getById(String id) throws ReservaNotFoundException {
		Reserva reserva = findById(id);
		if (reserva == null) {
			throw new ReservaNotFoundException(id);
		}
		return reserva;
	}

	@Override
	public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
		String sql = String.format("SELECT * FROM %s WHERE viaje = ? AND (usuario LIKE ? OR codigoReserva LIKE ?)",
				DATABASE_TABLE);
		List<Reserva> reservas = new ArrayList<>();

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ps.setString(2, "%" + searchParams + "%");
			ps.setString(3, "%" + searchParams + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Reserva reserva = mapResultSetToReserva(rs);
				reservas.add(reserva);
			}
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}

		return reservas;
	}

	@Override
	public void add(Reserva reserva) throws ReservaAlreadyExistsException {
		if (findById(reserva.getCodigoReserva()) != null) {
			throw new ReservaAlreadyExistsException(reserva);
		}

		String sql = String.format(
				"INSERT INTO %s (codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje) VALUES (?, ?, ?, ?, ?)",
				DATABASE_TABLE);

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getCodigoReserva());
			ps.setString(2, reserva.getUsuario());
			ps.setInt(3, reserva.getPlazasSolicitadas());
			ps.setTimestamp(4, Timestamp.valueOf(reserva.getFechaRealizacion()));
			ps.setInt(5, reserva.getViaje().getCodViaje());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}
	}

	@Override
	public void update(Reserva reserva) throws ReservaNotFoundException {
		if (findById(reserva.getCodigoReserva()) == null) {
			throw new ReservaNotFoundException(reserva.getCodigoReserva());
		}

		String sql = String.format(
				"UPDATE %s SET usuario = ?, plazasSolicitadas = ?, fechaRealizacion = ?, viaje = ? WHERE codigoReserva = ?",
				DATABASE_TABLE);

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getUsuario());
			ps.setInt(2, reserva.getPlazasSolicitadas());
			ps.setTimestamp(3, Timestamp.valueOf(reserva.getFechaRealizacion()));
			ps.setInt(4, reserva.getViaje().getCodViaje());
			ps.setString(5, reserva.getCodigoReserva());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}
	}

	@Override
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		if (findById(reserva.getCodigoReserva()) == null) {
			throw new ReservaNotFoundException(reserva.getCodigoReserva());
		}

		String sql = String.format("DELETE FROM %s WHERE codigoReserva = ?", DATABASE_TABLE);

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, reserva.getCodigoReserva());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}
	}

	@Override
	public int getNumPlazasReservadasEnViaje(Viaje viaje) {
		String sql = String.format("SELECT SUM(plazasSolicitadas) AS numPlazas FROM %s WHERE viaje = ?",
				DATABASE_TABLE);
		int numPlazas = 0;

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numPlazas = rs.getInt("numPlazas");
			}
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}

		return numPlazas;
	}

	@Override
	public Reserva findByUserInTravel(String usuario, Viaje viaje) {
		String sql = String.format("SELECT * FROM %s WHERE viaje = ? AND usuario = ?", DATABASE_TABLE);

		try (Connection connection = mySQLConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, viaje.getCodViaje());
			ps.setString(2, usuario);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSetToReserva(rs);
			}
		} catch (SQLException e) {
			throw new DatabaseConnectionException(sql + e.getMessage());
		}

		return null;
	}

    private Reserva mapResultSetToReserva(ResultSet rs) throws SQLException {
        String codigoReserva = rs.getString("codigoReserva");
        String usuario = rs.getString("usuario");
        int plazasSolicitadas = rs.getInt("plazasSolicitadas");
        LocalDateTime fechaRealizacion = rs.getTimestamp("fechaRealizacion").toLocalDateTime();
		int viajeId = rs.getInt("viaje");
		Viaje viaje = new Viaje(viajeId);
        return new Reserva(codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje);
    }


}