package com.ledger.core.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ledger.core.enums.logs.AuditAction;
import com.ledger.core.enums.logs.EntityType;
import com.ledger.core.model.AuditLog;
import com.ledger.core.repository.AuditLogRepository;
import com.ledger.core.service.AuditService;

@Service
public class AuditServiceImpl implements AuditService {
    private final AuditLogRepository auditLogRepo;

    public AuditServiceImpl(AuditLogRepository auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    /* create log */

    @Override
    public void logAction(AuditAction action, EntityType entityType, UUID entityId, String performedBy, String metadata) {
        AuditLog log = new AuditLog(action, entityType, entityId, performedBy, metadata);
        auditLogRepo.save(log);
    }
}
