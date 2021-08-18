package me.wss.lazyme.bean;

import me.wss.lazyme.annotation.Columns;
import me.wss.lazyme.annotation.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class Table {

    @TableInfo(row = 0, col = 1)
    private String entityName;

    @TableInfo(row = 1, col = 1)
    private String tableName;

    @TableInfo(row = 2, col = 1)
    private String className;

    @Columns
    private List<Column> columns = new ArrayList<>();

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
