package me.wss.lazyme.bean;

import me.wss.lazyme.annotation.ColumnInfo;

public class Column {

    @ColumnInfo(col = 0)
    private String fieldName;

    @ColumnInfo(col = 1, nullable = false)
    private String fieldName4Class;

    @ColumnInfo(col = 2)
    private String fieldName4Table;

    @ColumnInfo(col = 3, nullable = false)
    private String fieldType4Table;

    @ColumnInfo(col = 4)
    private String fieldLength4Table;

    @ColumnInfo(col = 5)
    private String remark;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName4Class() {
        return fieldName4Class;
    }

    public void setFieldName4Class(String fieldName4Class) {
        this.fieldName4Class = fieldName4Class;
    }

    public String getFieldName4Table() {
        return fieldName4Table;
    }

    public void setFieldName4Table(String fieldName4Table) {
        this.fieldName4Table = fieldName4Table;
    }

    public String getFieldType4Table() {
        return fieldType4Table;
    }

    public void setFieldType4Table(String fieldType4Table) {
        this.fieldType4Table = fieldType4Table;
    }

    public String getFieldLength4Table() {
        return fieldLength4Table;
    }

    public void setFieldLength4Table(String fieldLength4Table) {
        this.fieldLength4Table = fieldLength4Table;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
