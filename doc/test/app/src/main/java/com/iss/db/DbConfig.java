package com.iss.db;

import com.iss.bean.BaseBean;

import java.util.ArrayList;

/**
 * 数据库 DbConfig
 *
 * @author zhenglk
 */
public class DbConfig {
    final ArrayList<Class<? extends BaseBean<?>>> tableList;

    final String dbName;

    final int dbVersion;

    final String authority;

    final ArrayList<String> tableNameList;

    private DbConfig(final Builder builder) {
        tableList = builder.tableList;
        dbName = builder.dbName;
        dbVersion = builder.dbVersion;
        authority = builder.authority;

        tableNameList = new ArrayList<String>();
        for (Class<? extends BaseBean<?>> c : tableList) {
            String name = TableUtil.getTableName(c);
            tableNameList.add(name);
        }
    }

    @Override
    public String toString() {
        return "DB Builder name=" + dbName + ",v=" + dbVersion + ",authority=" + authority;
    }

    /**
     * builder
     */
    public static class Builder {
        private ArrayList<Class<? extends BaseBean<?>>> tableList;

        private String dbName;

        private int dbVersion;

        private String authority = "com.iss.mobile";

        /**
         * 构造
         */
        public Builder() {
            tableList = new ArrayList<Class<? extends BaseBean<?>>>();
        }

        /**
         * set name
         *
         * @param name name
         * @return builder
         */
        public Builder setName(String name) {
            dbName = name;
            return this;
        }

        /**
         * set version
         *
         * @param version version
         * @return builder
         */
        public Builder setVersion(int version) {
            dbVersion = version;
            return this;
        }

        /**
         * add table
         *
         * @param table table
         * @return builder
         */
        public Builder addTatble(Class<? extends BaseBean<?>> table) {
            tableList.add(table);
            return this;
        }

        /**
         * set authority
         *
         * @param auth auth
         * @return builder
         */
        public Builder setAuthority(String auth) {
            this.authority = auth;
            return this;
        }

        /**
         * 构建
         *
         * @return bean
         */
        public DbConfig build() {
            return new DbConfig(this);
        }
    }

}
