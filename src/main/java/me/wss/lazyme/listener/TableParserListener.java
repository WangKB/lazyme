package me.wss.lazyme.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import me.wss.lazyme.annotation.ColumnInfo;
import me.wss.lazyme.annotation.Columns;
import me.wss.lazyme.annotation.TableInfo;
import me.wss.lazyme.bean.Column;
import me.wss.lazyme.bean.Table;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableParserListener extends AnalysisEventListener<Map> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${table.excel.columnsRow}")
    private Integer columnsRow;

    private static Map<Integer, Map<Integer, Field>> tableInfos = new HashMap<>();

    private static Map<Integer, Field> columnInfos = new HashMap<>();

    private static Field columnsField;

    private Table table = new Table();

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @PostConstruct
    public void init() {
        initTableInfo();
        initColumnInfo();
    }

    private void initColumnInfo() {
        Field[] fields = Column.class.getDeclaredFields();
        for (Field field : fields) {
            Columns columns = field.getAnnotation(Columns.class);
            if (columns != null) {
                columnsField = field;
            }

            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
            if (columnInfo == null) {
                continue;
            }
            columnInfos.put(columnInfo.col(), field);
        }
    }

    private void initTableInfo() {

        Field[] fields = Table.class.getDeclaredFields();
        for (Field field : fields) {
            TableInfo tableInfo = field.getAnnotation(TableInfo.class);
            if (tableInfo == null) {
                continue;
            }
            Map<Integer, Field> row = tableInfos.get(tableInfo.row());
            if (row == null) {
                row = new HashMap<>();
                tableInfos.put(tableInfo.row(), row);
            }
            row.put(tableInfo.col(), field);
        }
    }

    @Override
    public void invoke(Map data, AnalysisContext context) {

        try {
            if (context.readRowHolder().getRowIndex() < columnsRow) {
                setTableInfoPerRow(data, context);
            } else {
                parseColumn(data, context);
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("解析table错误", e);
        }
    }

    private void parseColumn(Map data, AnalysisContext context) throws IllegalAccessException {
        Table table = getTable();
        Column column = new Column();
        for (Map.Entry<Integer, Field> item : columnInfos.entrySet()) {
            Object value = data.get(item.getKey());
            Field field = item.getValue();
            field.setAccessible(true);
            field.set(column, value);
        }
        List<Column> columns = table.getColumns();
        columns.add(column);
        table.setColumns(columns);

    }

    private void setTableInfoPerRow(Map data, AnalysisContext context) throws IllegalAccessException {

        Table table = getTable();
        Map<Integer, Field> rowTableInfo = tableInfos.get(context.readRowHolder().getRowIndex());
        if (rowTableInfo == null) {
            return;
        }
        for (Map.Entry<Integer, Field> item : rowTableInfo.entrySet()) {
            Object value = data.get(item.getKey());
            Field field = item.getValue();

            field.setAccessible(true);
            field.set(table, value);
        }
        setTable(table);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LOGGER.info(JSON.toJSONString(getTable()));
        setTable(new Table());
    }


}
