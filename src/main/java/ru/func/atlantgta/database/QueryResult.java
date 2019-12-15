package ru.func.atlantgta.database;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryResult implements AutoCloseable {

    public static class SQLSection {
        private final Map<String, Object> valuesMap = new ConcurrentHashMap<>();

        public void cleanup() {
            this.valuesMap.clear();
        }

        @SuppressWarnings("unchecked")
        public <T> T lookupValue(final String key) {
            return (T) this.valuesMap.get(key);
        }

        public void putValue(final String key, final Object val) {
            this.valuesMap.put(key, val);
        }

        public Collection<String> keys() {
            return valuesMap.keySet();
        }
    }

    private final Map<Integer, SQLSection> sectinosMap = new ConcurrentHashMap<>();
    // Cleanup only once.
    private boolean single;

    @SneakyThrows
    public QueryResult(final ResultSet rs) {
        if (rs != null) {
            int point = 0;
            final ResultSetMetaData data = rs.getMetaData();
            while (rs.next()) {
                final SQLSection section = new SQLSection();
                for (int colum = 1; colum <= data.getColumnCount(); colum++) {
                    section.putValue(data.getColumnName(colum), rs.getObject(colum));
                }
                this.sectinosMap.put(point++, section);
            }
            try {
                rs.close();
            } catch (final SQLException ignore) {
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.cleanup();
        super.finalize();
    }

    public Collection<SQLSection> all() {
        return sectinosMap.values();
    }

    public void cleanup() {
        if (!this.single) {
            this.single = true;
            this.sectinosMap.values().forEach(SQLSection::cleanup);
            this.sectinosMap.clear();
        }
    }

    @Override
    public void close() {
        this.cleanup();
    }

    public boolean isEmpty() {
        return this.sectinosMap.isEmpty();
    }

    public SQLSection lookupSection(final int index) {
        return this.sectinosMap.get(index);
    }

}
