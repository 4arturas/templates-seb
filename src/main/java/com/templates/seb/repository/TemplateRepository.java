package com.templates.seb.repository;

import com.templates.seb.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Integer> {
    TemplateEntity findByName(String name);
}