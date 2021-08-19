package me.wss.lazyme.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import me.wss.lazyme.listener.TableParserListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/process")
public class ProcessController {

    @Resource
    private TableParserListener tableParserListener;

    @PostMapping
    public String process(@RequestParam("template") MultipartFile template, String jsonModel) throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.getVersion());
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.getVersion()));

        String content = new String(template.getBytes());
        Template t = new Template("template", new StringReader(content), cfg);

        Writer out = new StringWriter();
        t.process(new ObjectMapper().readValue(jsonModel, HashMap.class), out);

        return out.toString();
    }

    @PostMapping("parseTableExcel")
    public String parseTableExcel(@RequestParam("excel") MultipartFile excel) throws IOException {

        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(excel.getInputStream(), tableParserListener).headRowNumber(0);
        ExcelReader excelReader = excelReaderBuilder.build();
        List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
        for (ReadSheet sheet : sheets) {
            excelReader.read(sheet);
        }
        return "success";
    }
}
