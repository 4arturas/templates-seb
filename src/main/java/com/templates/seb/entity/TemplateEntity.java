package com.templates.seb.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import lombok.Data;


@Entity
@Table(name = "templates")
@Audited
@AuditTable(value = "templates_audit")
@Data
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer tribeId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
}