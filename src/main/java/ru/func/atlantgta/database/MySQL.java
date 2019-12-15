package ru.func.atlantgta.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import ru.func.atlantgta.AtlantGTA;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MySQL {

    //Класс хранящий инструменты для работы с MySql
    private final String user;
    private final String password;
    private final String host;
    private final String database;
    private final int port;

    private ThreadPoolExecutor pool;
    private HikariDataSource dataSource;

    public MySQL(String user, String password, String host, String database, int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.database = database;
        this.port = port;

        this.pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                10L, TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
        pool.allowCoreThreadTimeOut(true);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        HikariConfig config = new HikariConfig();
        config.setConnectionTimeout(5000);
        config.setMaximumPoolSize(3);
        config.setAutoCommit(true);
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s" +
                "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", host, port, database));
        config.setUsername(user);
        config.setPassword(password);
        this.dataSource = new HikariDataSource(config);
    }

    public void executeUpdate(String query) {
        pool.submit(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     Statement statement = connection.createStatement()) {
                    statement.executeUpdate(query);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void executeQuery(String query, Consumer<QueryResult> callback) {
        pool.submit(() -> {
            try {
                try (Connection connection = dataSource.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet set = statement.executeQuery(query)) {
                    QueryResult result = new QueryResult(set);
                    Bukkit.getScheduler().runTask(AtlantGTA.getInstance(), () -> callback.accept(result));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
