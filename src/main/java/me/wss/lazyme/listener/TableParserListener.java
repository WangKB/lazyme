package me.wss.lazyme.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import me.wss.lazyme.annotation.ColumnInfo;
import me.wss.lazyme.annotation.Columns;
import me.wss.lazyme.annotation.TableInfo;
import me.wss.lazyme.bean.Column;
import me.wss.lazyme.bean.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableParserListener extends AnalysisEventListener<Map<Integer, Object>> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${table.excel.columnsRow}")
    private Integer columnsRow;

    private static final Map<Integer, Map<Integer, Field>> tableInfos = new HashMap<>();

    private static final Map<Integer, Field> columnInfos = new HashMap<>();

    private static Field columnsField;

    private Table currentSheetTable = new Table();

    public Table getCurrentSheetTable() {
        return currentSheetTable;
    }

    public void setCurrentSheetTable(Table currentSheetTable) {
        this.currentSheetTable = currentSheetTable;
    }

    @PostConstruct
    public void init() {
        initTableInfo();
        initColumnInfo();
    }

    private void initColumnInfo() {
        Field[] fields = Column.class.getDeclaredFields();
        for (Field field : fields) {

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
            Columns columns = field.getAnnotation(Columns.class);
            if (columns != null) {
                columnsField = field;
            }
            TableInfo tableInfo = field.getAnnotation(TableInfo.class);
            if (tableInfo == null) {
                continue;
            }
            Map<Integer, Field> row = tableInfos.computeIfAbsent(tableInfo.row(), k -> new HashMap<>());
            row.put(tableInfo.col(), field);
        }
    }

    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {

        try {
            if (context.readRowHolder().getRowIndex() < columnsRow) {
                setTableInfoPerRow(data, context);
            } else {
                parseColumn(data);
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("解析table错误", e);
        }
    }

    private void parseColumn(Map<Integer, Object> data) throws IllegalAccessException {
        Table table = getCurrentSheetTable();
        Column column = new Column();
        for (Map.Entry<Integer, Field> item : columnInfos.entrySet()) {
            Object value = data.get(item.getKey());
            Field field = item.getValue();
            field.setAccessible(true);
            field.set(column, value);
        }
        columnsField.setAccessible(true);
        List<Column> columns = (List<Column>) columnsField.get(table);
        columns.add(column);
        columnsField.set(table, columns);

    }

    private void setTableInfoPerRow(Map<Integer, Object> data, AnalysisContext context) throws IllegalAccessException {

        Table table = getCurrentSheetTable();
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
        setCurrentSheetTable(table);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        String fileName = "F://projects/utils/lazyme/data/tables/%s.json";
        fileName = String.format(fileName, getCurrentSheetTable().getClassName());
        try {
            JSON.writeJSONString(new FileWriter(fileName),getCurrentSheetTable());
        } catch (IOException e) {
            LOGGER.error("存储table错误", e);
        }
        setCurrentSheetTable(new Table());
    }


}
