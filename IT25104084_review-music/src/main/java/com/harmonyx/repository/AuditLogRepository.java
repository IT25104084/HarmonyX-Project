package com.harmonyx.repository;

import com.harmonyx.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the admin audit log.
 * AuditLog entity uses: entityType, entityId, createdAt.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** 50 most recent audit entries (admin audit log page). */
    List<AuditLog> findTop50ByOrderByCreatedAtDesc();

    /** History for a specific entity (e.g., all actions on Song #42). */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
