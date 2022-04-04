package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.*;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import com.example.demo.domain.User;

@Repository
public class UserRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    private String tableName = "users";

    private byte[] CF_INFO = Bytes.toBytes("info");
    private byte[] qUser = Bytes.toBytes("user");
    private byte[] qEmail = Bytes.toBytes("email");
    private byte[] qPassword = Bytes.toBytes("password");

    public List<User> findAll() {
        return hbaseTemplate.find(tableName, "info", new RowMapper<User>() {
            @Override
            public User mapRow(Result result, int rowNum) throws Exception {
                return new User(
                        Bytes.toString(result.getValue(CF_INFO, qUser)),
                        Bytes.toString(result.getValue(CF_INFO, qEmail)),
                        Bytes.toString(result.getValue(CF_INFO, qPassword))
                );
            }
        });
    }

    public User find(String rowKey) {
        return hbaseTemplate.execute(tableName, new TableCallback<User>() {
            @Override
            public User doInTable(HTableInterface table) throws Throwable {
                Get g = new Get(Bytes.toBytes(rowKey));
                g.addFamily(CF_INFO);
                Result result = table.get(g);
                // Versioned cell:
                // result.getColumn(CF_INFO, qUser).get(0).getTimestamp();
                // result.getColumn(CF_INFO, qUser).get(0).getValue();
                return new User(
                        Bytes.toString(result.getValue(CF_INFO, qUser)),
                        Bytes.toString(result.getValue(CF_INFO, qEmail)),
                        Bytes.toString(result.getValue(CF_INFO, qPassword))
                );
            }
        });
    }

    public List<User> scan(String start, String end) {
        return hbaseTemplate.execute(tableName, new TableCallback<List<User>>() {
            @Override
            public List<User> doInTable(HTableInterface table) throws Throwable {
                Scan s = new Scan(Bytes.toBytes(start), Bytes.toBytes(end));
                ResultScanner resultScanner = table.getScanner(s);

                List<User> users = new ArrayList<>();
                for (Result result : resultScanner) {
                    User user = new User(
                            Bytes.toString(result.getValue(CF_INFO, qUser)),
                            Bytes.toString(result.getValue(CF_INFO, qEmail)),
                            Bytes.toString(result.getValue(CF_INFO, qPassword))
                    );
                    users.add(user);
                }
                return users;
            }
        });
    }

    public User save(User user) {
        return hbaseTemplate.execute(tableName, new TableCallback<User>() {
            public User doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(Bytes.toBytes(user.getName()));
                p.addColumn(CF_INFO, qUser, Bytes.toBytes(user.getName()));
                p.addColumn(CF_INFO, qEmail, Bytes.toBytes(user.getEmail()));
                p.addColumn(CF_INFO, qPassword, Bytes.toBytes(user.getPassword()));
                table.put(p);
                return user;
            }
        });
    }

    public User delete(String rowKey) {
        return hbaseTemplate.execute(tableName, new TableCallback<User>() {
            @Override
            public User doInTable(HTableInterface table) throws Throwable {
                Delete d = new Delete(Bytes.toBytes(rowKey));
                table.delete(d);
                return null;
            }
        });
    }

    public User deleteColumn(String rowKey, String col) {
        return hbaseTemplate.execute(tableName, new TableCallback<User>() {
            @Override
            public User doInTable(HTableInterface table) throws Throwable {
                Delete d = new Delete(Bytes.toBytes(rowKey));
                d.addColumn(CF_INFO, Bytes.toBytes(col));
                table.delete(d);
                return null;
            }
        });
    }
}
