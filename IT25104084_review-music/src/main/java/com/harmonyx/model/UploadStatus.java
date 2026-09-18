package com.harmonyx.model;

/**
 * Lifecycle status for a {@link Song} submission.
 *
 * <pre>
 *   DRAFT            — saved by artist but not yet submitted for moderation.
 *   PENDING_APPROVAL — submitted; awaiting admin review.
 *   APPROVED         — passed moderation; visible to all listeners.
 *   REJECTED         — failed moderation; artist can revise and resubmit.
 *   CHANGES_REQUIRED — admin requested revisions.
 * </pre>
 */
public enum UploadStatus {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CHANGES_REQUIRED("Changes Required");

    private final String displayName;

    UploadStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
