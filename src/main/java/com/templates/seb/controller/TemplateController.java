package com.templates.seb.controller;

import com.templates.seb.entity.TemplateEntity;
import com.templates.seb.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<TemplateEntity> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateEntity> getTemplateById(@PathVariable Integer id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TemplateEntity createTemplate(@RequestBody TemplateEntity templateEntity) {
        return templateService.createTemplate(templateEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateEntity> updateTemplate(@PathVariable Integer id, @RequestBody TemplateEntity updatedTemplateEntity) {
        return templateService.updateTemplate(id, updatedTemplateEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        if (templateService.deleteTemplate(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}