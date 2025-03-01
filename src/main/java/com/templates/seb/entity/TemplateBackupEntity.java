package com.templates.seb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "templates_backup")
@Data
public class TemplateBackupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer tribeId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 10)
    private String actionType; // 'UPDATE' or 'DELETE'

    @Column(nullable = false)
    private LocalDateTime backedUpAt;
}