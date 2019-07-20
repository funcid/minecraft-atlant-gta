package ru.func.atlantgta.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    protected Connection connection;

    protected Database() {
        this.connection = null;
    }

    public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }
}