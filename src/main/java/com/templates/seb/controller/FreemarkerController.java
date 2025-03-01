package com.templates.seb.controller;

import com.templates.seb.entity.TemplateEntity;
import com.templates.seb.service.TemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

@AllArgsConstructor
@RestController
@RequestMapping("/api/freemarker")
public class FreemarkerController {

    private TemplateService templateService;

    private Configuration freemarkerConfig;

    @PostMapping("/render/json")
    public ResponseEntity<String> renderTemplateWithJson(
            @RequestParam("templateName") String templateName,
            @RequestBody Map<String, Object> jsonData) {

        try {
            TemplateEntity template = templateService.getTemplateByName(templateName);
            if (template == null) {
                return ResponseEntity.badRequest().body("Template not found: " + templateName);
            }

            Template freemarkerTemplate = new Template(templateName, template.getContent(), freemarkerConfig);

            StringWriter stringWriter = new StringWriter();
            freemarkerTemplate.process(jsonData, stringWriter);

            return ResponseEntity.ok(stringWriter.toString());

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error loading template: " + e.getMessage());
        } catch (TemplateException e) {
            return ResponseEntity.status(500).body("Error processing template: " + e.getMessage());
        }
    }

    @PostMapping("/render/xml")
    public ResponseEntity<String> renderTemplateWithXml(
            @RequestParam("templateName") String templateName,
            @RequestBody String xmlData) {

        try {
            TemplateEntity template = templateService.getTemplateByName(templateName);
            if (template == null) {
                return ResponseEntity.badRequest().body("Template not found: " + templateName);
            }

            Map<String, Object> dataModel = parseXmlToMap(xmlData);
            if (dataModel == null || dataModel.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid or empty XML data.");
            }

            Template freemarkerTemplate = new Template(templateName, template.getContent(), freemarkerConfig);

            StringWriter stringWriter = new StringWriter();
            freemarkerTemplate.process(dataModel, stringWriter);

            return ResponseEntity.ok(stringWriter.toString());

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error loading template: " + e.getMessage());
        } catch (TemplateException e) {
            return ResponseEntity.status(500).body("Error processing template: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing XML data: " + e.getMessage());
        }
    }

    private Map<String, Object> parseXmlToMap(String xmlData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            Object result = parseNode(document.getDocumentElement());
            if (result instanceof Map) {
                return (Map<String, Object>) result;
            } else {
                Map<String, Object> rootMap = new HashMap<>();
                rootMap.put(document.getDocumentElement().getNodeName(), result);
                return rootMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object parseNode(org.w3c.dom.Node node) {
        if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            NodeList children = node.getChildNodes();
            Map<String, Object> map = new HashMap<>();

            for (int i = 0; i < children.getLength(); i++) {
                org.w3c.dom.Node child = children.item(i);

                if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    String key = child.getNodeName();
                    Object value = parseNode(child);

                    if (map.containsKey(key)) {
                        Object existingValue = map.get(key);
                        if (!(existingValue instanceof List)) {
                            List<Object> list = new ArrayList<>();
                            list.add(existingValue);
                            map.put(key, list);
                        }
                        ((List<Object>) map.get(key)).add(value);
                    } else {
                        map.put(key, value);
                    }
                } else if (child.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                    return child.getTextContent().trim();
                }
            }

            if (map.size() == 1 && map.containsKey("#text")) {
                return map.get("#text");
            }

            return map;
        } else if (node.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
            return node.getTextContent().trim();
        } else {
            return null;
        }
    }
}