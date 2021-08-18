package me.wss.lazyme.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.wss.lazyme.annotation.TableInfo;
import me.wss.lazyme.bean.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SimpleRowListener extends AnalysisEventListener<Map> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${table.excel.columnsRow}")
    private Integer columnsRow;

    private Map<Integer, Map<Integer, Field>> tableInfos = new HashMap<>();

    private Table table;

    public Map<Integer, Map<Integer, Field>> getTableInfos() {
        return tableInfos;
    }

    public void setTableInfos(Map<Integer, Map<Integer, Field>> tableInfos) {
        this.tableInfos = tableInfos;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public SimpleRowListener() {

        setTable(new Table());

        Field[] fields = Table.class.getDeclaredFields();
        Map<Integer, Map<Integer, Field>> tableInfos = getTableInfos();
        for (Field field : fields) {
            TableInfo tableInfo = field.getAnnotation(TableInfo.class);
            if (tableInfo == null) {
                continue;
            }
            Map<Integer, Field> row = tableInfos.get(tableInfo.row());
            if (row ==null) {
                row = new HashMap<>();
                tableInfos.put(tableInfo.row(), row);
            }
            row.put(tableInfo.col(), field);
        }
        setTableInfos(tableInfos);
        System.out.println(JSON.toJSONString(getTableInfos()));
    }

    @Override
    public void invoke(Map data, AnalysisContext context) {
        try {
            setTableInfoPerRow(data, context);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        LOGGER.info(columnsRow.toString());
    }

    private void setTableInfoPerRow(Map data, AnalysisContext context) throws IllegalAccessException {

        Table table = getTable();
        Map<Integer, Map<Integer, Field>> tableInfos = getTableInfos();
        Map<Integer, Field> rowTableInfo = tableInfos.get(context.readRowHolder().getRowIndex());
        if (rowTableInfo ==null) {
            return;
        }
        for(Map.Entry<Integer, Field> item: rowTableInfo.entrySet()){
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
    }


}
