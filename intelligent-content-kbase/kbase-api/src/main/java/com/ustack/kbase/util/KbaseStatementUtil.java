package com.ustack.kbase.util;

import com.kbase.jdbc.ConnectionImpl;
import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;
import kbase.struct.TPI_RETURN_RESULT;
import lombok.extern.slf4j.Slf4j;
import net.cnki.kbase.jdbc.KbaseDataSource;
import net.cnki.kbase.jdbc.template.AbstractKbaseTemplate;
import net.cnki.kbase.jdbc.template.KbaseTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname KbaseUtil
 * @Description TODO
 */
@Slf4j
public class KbaseStatementUtil {

    Connection jdbcConnection = null;
    ConnectionImpl kbaseConnection = null;
    Statement statement = null;
    StatementImpl stmt = null;
    ResultSetImpl rs = null;

    public void closeConnection() {
//        logger.info("Test Case: closeConnection()");
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        if (kbaseConnection != null) {
            try {
                kbaseConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            kbaseConnection = null;
        }

        if (jdbcConnection != null) {
            try {
                jdbcConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            jdbcConnection = null;
        }
    }

    public StatementImpl createStatement(KbaseDataSource kbaseDataSource) {
        try {
            jdbcConnection = DriverManager.getConnection(kbaseDataSource.getJdbcUrl(), kbaseDataSource.getJdbcUsername(),
                    kbaseDataSource.getJdbcPassword());
            kbaseConnection = (ConnectionImpl) jdbcConnection;
            statement = kbaseConnection.createStatement();
            stmt = (StatementImpl) statement;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error("创建kbase连接失败，失败原因{}", throwables);
        }
        return stmt;
    }

    /**
     * 执行SQL
     *
     * @param sql
     * @param kbaseDataSource
     * @return
     */
    public boolean execute(String sql, KbaseDataSource kbaseDataSource) {
        boolean flag = false;
        try {
            createStatement(kbaseDataSource);
            flag = stmt.execute(sql, true);
            if (flag) {
                closeConnection();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            closeConnection();
            log.error("执行sql失败，失败原因{}", throwables);
            return false;
        }
        return flag;
    }

    /**
     * 新增
     *
     * @param sql
     * @param kbaseDataSource
     * @return
     */
    public boolean insertExecute(String sql, KbaseDataSource kbaseDataSource) {
        boolean flag = false;
        try {
            createStatement(kbaseDataSource);
            flag = stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            closeConnection();
        }
        return flag;
    }

    /**
     * 获取 数据源
     *
     * @return
     */
//    public static AbstractKbaseTemplate getKbaseTemplate(String ip,String username,String password){
//        KbaseDataSource dataSource = KbaseDataSource.create(ip,username,password);
//        AbstractKbaseTemplate cjTemplate = new KbaseTemplate(dataSource);
//        cjTemplate.setMaxCountPerFetch(5000); // 5000条数据
//        cjTemplate.setRestrictFetchEnabled(false);
//        return cjTemplate;
//    }
    public static AbstractKbaseTemplate getKbaseTemplate(KbaseDataSource kbaseDataSource) {
        AbstractKbaseTemplate cjTemplate = new KbaseTemplate(kbaseDataSource);
        cjTemplate.setMaxCountPerFetch(5000); // 5000条数据
        cjTemplate.setRestrictFetchEnabled(false);
        return cjTemplate;
    }


    public Map executeQuery(String sql, KbaseDataSource kbaseDataSource) {
        List<Map<String, String>> list = null;
        Map resultMap = new HashMap(2);
        try {
            createStatement(kbaseDataSource);
            stmt = (StatementImpl) statement;
            rs = (ResultSetImpl) stmt.executeQuery(sql, false);
            int retCount = rs.KBase_GetRecordSetCount();
            int fieldCount = rs.KBaseGetFieldCount();
            list = new ArrayList<>(retCount);
            Map resutlMap = null;
            while (rs.next()) {
                resutlMap = new HashMap<>(fieldCount);
                for (int i = 1; i <= fieldCount; i++) {
                    String fieldValue = rs.getString(i);
                    TPI_RETURN_RESULT fileObject = rs.KBaseGetFieldName(i - 1);
                    String fileName = fileObject.rtnBuf;
                    resutlMap.put(fileName, fieldValue.trim());
                }
                list.add(resutlMap);
            }
            resultMap.put("data", list);
            resultMap.put("count", retCount);
            return resultMap;
        } catch (Exception throwables) {
            throwables.printStackTrace();
            closeConnection();
            log.error("执行查询sql失败，失败原因{}", throwables);
        }
        return null;
    }

}

























