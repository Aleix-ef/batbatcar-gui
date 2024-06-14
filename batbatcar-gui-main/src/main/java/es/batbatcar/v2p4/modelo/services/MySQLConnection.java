package es.batbatcar.v2p4.modelo.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

@Service
public class MySQLConnection {

    private static Connection connection;
    private String ip;
    private String database;
    private String userName;
    private String password;

    public MySQLConnection() {
        this.ip = "127.0.0.1";
        this.database = "batbatcar";
        this.userName = "root";
        this.password = "1234";
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String dbURL = "jdbc:mysql://" + ip + "/" + database;
                this.connection = DriverManager.getConnection(dbURL, userName, password);
                System.out.println("Conexion valida: " + connection.isValid(20));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return this.connection;
    }
}
