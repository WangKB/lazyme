package me.wss.lazyme.controller;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import me.wss.lazyme.bean.Table;
import me.wss.lazyme.listener.SimpleRowListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/process")
public class ProcessController {

    @Resource
    private SimpleRowListener simpleRowListener;

    @PostMapping
    public String process(@RequestParam("template") MultipartFile template, String jsonModel) throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.getVersion());
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.getVersion()));

        String content = new String(template.getBytes());
        Template t = new Template("template", new StringReader(content), cfg);

        Map<String, Object> model =
                new ObjectMapper().readValue(jsonModel, HashMap.class);

        Writer out = new StringWriter();
        t.process(model, out);

        String transformedTemplate = out.toString();
        return transformedTemplate;
    }

    @PostMapping("parseTableExcel")
    public String parseTableExcel(@RequestParam("excel") MultipartFile excel) throws IOException {

        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(excel.getInputStream(), simpleRowListener).headRowNumber(0);
        ExcelReader excelReader = excelReaderBuilder.build();
        List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
        for (ReadSheet sheet : sheets) {
            excelReader.read(sheet);
        }
        return "success";
    }

    public static void main(String[] args) {

        File file = new File("F://projects/utils/lazyme/data/测试数据.xlsx");
        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(file, new SimpleRowListener()).headRowNumber(0);
        ExcelReader excelReader = excelReaderBuilder.build();
        List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
        for (ReadSheet sheet : sheets) {
            excelReader.read(sheet);
        }
    }
}
