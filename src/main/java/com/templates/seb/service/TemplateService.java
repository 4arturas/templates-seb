package com.templates.seb.service;

import com.templates.seb.entity.TemplateEntity;
import com.templates.seb.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<TemplateEntity> getAllTemplates() {
        return templateRepository.findAll();
    }

    public Optional<TemplateEntity> getTemplateById(Integer id) {
        return templateRepository.findById(id);
    }

    public TemplateEntity createTemplate(TemplateEntity templateEntity) {
        templateEntity.setTribeId(1);
        templateEntity.setUserId(1);
        templateEntity.setDateCreated(LocalDateTime.now());
        templateEntity.setDateUpdated(LocalDateTime.now());
        return templateRepository.save(templateEntity);
    }

    public Optional<TemplateEntity> updateTemplate(Integer id, TemplateEntity updatedTemplateEntity) {
        return templateRepository.findById(id).map(templateEntity -> {
            templateEntity.setTribeId(1);
            templateEntity.setUserId(1);
            templateEntity.setName(updatedTemplateEntity.getName());
            templateEntity.setContent(updatedTemplateEntity.getContent());
            templateEntity.setDateUpdated(LocalDateTime.now());
            return templateRepository.save(templateEntity);
        });
    }

    public boolean deleteTemplate(Integer id) {
        if (templateRepository.existsById(id)) {
            templateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public TemplateEntity getTemplateByName(String name) {
        return templateRepository.findByName(name);
    }
}