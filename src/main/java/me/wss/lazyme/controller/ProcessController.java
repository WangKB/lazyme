package me.wss.lazyme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/process")
public class ProcessController {

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


}
