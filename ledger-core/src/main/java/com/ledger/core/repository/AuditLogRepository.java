package com.ledger.core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledger.core.enums.logs.EntityType;
import com.ledger.core.model.AuditLog;

/* provides methods for audit logs in db */

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}
