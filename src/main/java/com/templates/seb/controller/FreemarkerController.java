package com.templates.seb.controller;

import com.templates.seb.entity.TemplateEntity;
import com.templates.seb.service.TemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/freemarker")
public class FreemarkerController {

    private static final Logger log = LoggerFactory.getLogger(FreemarkerController.class);

    private final TemplateService templateService;
    private final Configuration freemarkerConfig;

    @PostMapping("/render/json")
    public ResponseEntity<String> renderTemplateWithJson(
            @RequestParam("templateName") String templateName,
            @RequestBody Map<String, Object> jsonData) {
        log.info("Rendering template '{}' with JSON data", templateName);
        try {
            TemplateEntity template = getTemplate(templateName);
            String result = processTemplate(template, jsonData);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error rendering template with JSON", e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/render/xml")
    public ResponseEntity<String> renderTemplateWithXml(
            @RequestParam("templateName") String templateName,
            @RequestBody String xmlData) {
        log.info("Rendering template '{}' with XML data", templateName);
        try {
            TemplateEntity template = getTemplate(templateName);
            Map<String, Object> dataModel = parseXmlToMap(xmlData);
            if (dataModel == null || dataModel.isEmpty()) {
                throw new IllegalArgumentException("Invalid or empty XML data provided.");
            }
            String result = processTemplate(template, dataModel);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error rendering template with XML", e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    private TemplateEntity getTemplate(String templateName) {
        TemplateEntity template = templateService.getTemplateByName(templateName);
        if (template == null || template.getContent() == null || template.getContent().isEmpty()) {
            throw new IllegalArgumentException("Template not found or is empty: " + templateName);
        }
        log.debug("Template '{}' found with content: {}", templateName, template.getContent());
        return template;
    }

    private String processTemplate(TemplateEntity templateEntity, Map<String, Object> dataModel) throws IOException, TemplateException {
        Template freemarkerTemplate = new Template(templateEntity.getName(), templateEntity.getContent(), freemarkerConfig);
        StringWriter stringWriter = new StringWriter();
        freemarkerTemplate.process(dataModel, stringWriter);
        String result = stringWriter.toString();
        log.debug("Template processed successfully with result: {}", result);
        return result;
    }

    private Map<String, Object> parseXmlToMap(String xmlData) {
        try {
            log.debug("Parsing XML data: {}", xmlData);
            JSONObject jsonObj = XML.toJSONObject(xmlData, true);
            Map<String, Object> dataModel = jsonObj.toMap();
            if (dataModel.containsKey("root")) {
                dataModel = (Map<String, Object>) dataModel.get("root");
            }
            log.debug("Flattened data model: {}", dataModel);
            return dataModel;
        } catch (Exception e) {
            log.error("Error converting XML to Map", e);
            throw new RuntimeException("Error converting XML to Map: " + e.getMessage(), e);
        }
    }
}