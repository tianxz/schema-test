package org.vinci.schematest.module.lessschema;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Jao on 2017/9/10.
 */
@Mapper
public interface TableGovernDao {
    int createTable(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    long mockQueryTable(@Param("schemaName") String schemaName, @Param("tableName") String tableName);

    @Insert("CREATE SCHEMA `${schemaName}` DEFAULT CHARACTER SET utf8;")
    int createSchema(@Param("schemaName") String schemaName);

    @Insert("DROP DATABASE `${schemaName}`;")
    int deleteSchema(@Param("schemaName") String schemaName);
}
