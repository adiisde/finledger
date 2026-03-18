package com.ledger.core.service;

import java.util.UUID;

import com.ledger.core.enums.logs.AuditAction;
import com.ledger.core.enums.logs.EntityType;

public interface AuditService {
    void logAction(AuditAction action, EntityType entityType, UUID entityId, String performedBy, String metadata);
}
