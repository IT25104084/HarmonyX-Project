package com.harmonyx.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Records an admin action on a user or piece of content.
 *
 * <p>Every significant admin operation (verification, suspension, music review)
 * writes a row here so that there is a full, immutable audit trail.
 * The {@code entityType} / {@code entityId} pair identifies what was acted on.</p>
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The admin who performed the action. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    /**
     * Short action code, e.g. {@code "VERIFY_ARTIST"}, {@code "SUSPEND_USER"},
     * {@code "APPROVE_SONG"}, {@code "REJECT_SONG"}.
     */
    @Column(nullable = false, length = 60)
    private String action;

    /**
     * The type of entity this log entry relates to, e.g. {@code "User"},
     * {@code "ArtistProfile"}, {@code "Song"}.
     */
    @Column(length = 50)
    private String entityType;

    /** The primary-key value of the acted-on entity. */
    private Long entityId;

    /** Human-readable explanation or additional context. */
    @Column(length = 1000)
    private String details;

    /** When this log entry was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    protected AuditLog() {}

    /**
     * Convenience builder-style constructor.
     *
     * @param admin      the acting admin
     * @param action     short action code
     * @param entityType type of target entity
     * @param entityId   PK of target entity
     * @param details    free-text detail
     */
    public AuditLog(User admin, String action, String entityType,
                    Long entityId, String details) {
        this.admin      = admin;
        this.action     = action;
        this.entityType = entityType;
        this.entityId   = entityId;
        this.details    = details;
    }

    // -----------------------------------------------------------------------
    // Getters / Setters
    // -----------------------------------------------------------------------

    public Long          getId()         { return id; }

    public User          getAdmin()      { return admin; }
    public void          setAdmin(User a){ this.admin = a; }

    public String        getAction()     { return action; }
    public void          setAction(String a) { this.action = a; }

    public String        getEntityType() { return entityType; }
    public void          setEntityType(String t) { this.entityType = t; }

    public Long          getEntityId()   { return entityId; }
    public void          setEntityId(Long i) { this.entityId = i; }

    public String        getDetails()    { return details; }
    public void          setDetails(String d) { this.details = d; }

    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getPerformedAt() { return createdAt; }
    public String        getTargetType()  { return entityType; }
    public Long          getTargetId()    { return entityId; }
}
