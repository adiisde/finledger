package com.ledger.core.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledger.core.enums.logs.AuditAction;
import com.ledger.core.enums.logs.EntityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
/* system entire activity */
public class AuditLog {
    @Id
    @GeneratedValue
    private UUID id;

    /* entity type (with enums) means which part of the system touched or affected */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    /* entity id of the affected record */
    @Column(nullable = false)
    private UUID entityId;

    /* action means what happend at that moment */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    /* for who perform this action */
    @Column(nullable = false)
    private String performedBy;

    /* timestamps */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* extra metadata */
    @Column(columnDefinition = "jsonb")
    private String metadata;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
