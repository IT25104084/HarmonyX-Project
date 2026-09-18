package com.harmonyx.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * SongReviewForm — form-backing object for the admin song review page
 * ({@code /admin/songs/{id}/review}).
 *
 * <p>Submitted when an admin makes a moderation decision on a pending song upload.
 * The {@code decision} value must correspond to a valid {@code SongUploadStatus}
 * value that represents a review outcome:
 * <ul>
 *   <li>{@code APPROVED}           – song passes moderation and becomes publicly visible</li>
 *   <li>{@code REJECTED}           – song is permanently refused</li>
 *   <li>{@code CHANGES_REQUIRED}   – artist must revise and resubmit</li>
 *   <li>{@code COPYRIGHT_FLAG}     – song flagged for a copyright concern</li>
 * </ul>
 *
 * <p>Validated by Spring MVC's {@code @Valid}.
 */
public class SongReviewForm {

    // ── Fields ────────────────────────────────────────────────────────────────

    /**
     * The moderation decision string.
     * Expected values: {@code "APPROVED"}, {@code "REJECTED"},
     * {@code "CHANGES_REQUIRED"}, {@code "COPYRIGHT_FLAG"}.
     */
    @NotBlank(message = "A review decision is required")
    private String decision;

    /** Optional note explaining the decision, shown to the artist. */
    private String reviewNote;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** No-argument constructor required by Spring MVC model binding. */
    public SongReviewForm() {
    }

    /**
     * Convenience constructor.
     *
     * @param decision   the review decision string
     * @param reviewNote the reviewer's explanatory note (may be {@code null})
     */
    public SongReviewForm(String decision, String reviewNote) {
        this.decision   = decision;
        this.reviewNote = reviewNote;
    }

    // ── Getters & setters ─────────────────────────────────────────────────────

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
}
