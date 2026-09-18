package com.harmonyx.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A single audio track on the HarmonyX platform.
 * Belongs to one ArtistProfile and optionally to an Album.
 * The uploadStatus drives the admin moderation workflow.
 */
@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(length = 100)
    private String genre;

    @Column(length = 100)
    private String mood;

    @Column(length = 100)
    private String language;

    @Column(length = 1000)
    private String description;

    @Lob
    private String lyrics;

    private int durationSeconds;

    @Column(length = 512)
    private String audioFileUrl;

    @Column(length = 512)
    private String artworkUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private UploadStatus uploadStatus = UploadStatus.DRAFT;

    @Column(length = 1000)
    private String reviewNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(nullable = false)
    private long streamCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    private boolean skipped = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Song() {}

    // Getters / Setters

    public Long getId()                              { return id; }
    public Long getSongId()                          { return id; }

    public String getTitle()                         { return title; }
    public void   setTitle(String title)             { this.title = title; }

    public ArtistProfile getArtist()                         { return artist; }
    public void          setArtist(ArtistProfile artist)     { this.artist = artist; }

    public String        getArtistStageName()                { return artist != null ? artist.getStageName() : ""; }

    public Album   getAlbum()                        { return album; }
    public void    setAlbum(Album album)             { this.album = album; }

    public String  getGenre()                        { return genre; }
    public void    setGenre(String genre)            { this.genre = genre; }

    public String  getMood()                         { return mood; }
    public void    setMood(String mood)              { this.mood = mood; }

    public String  getLanguage()                     { return language; }
    public void    setLanguage(String language)      { this.language = language; }

    public String  getDescription()                  { return description; }
    public void    setDescription(String desc)       { this.description = desc; }

    public String  getLyrics()                       { return lyrics; }
    public void    setLyrics(String lyrics)          { this.lyrics = lyrics; }

    public int     getDurationSeconds()              { return durationSeconds; }
    public void    setDurationSeconds(int d)         { this.durationSeconds = d; }

    public String  getAudioFileUrl()                 { return audioFileUrl; }
    public void    setAudioFileUrl(String url)       { this.audioFileUrl = url; }

    /** Alias for audioFileUrl — used in templates. */
    public String  getAudioUrl()                     { return audioFileUrl; }
    public void    setAudioUrl(String url)           { this.audioFileUrl = url; }

    public String  getArtworkUrl()                   { return artworkUrl; }
    public void    setArtworkUrl(String url)         { this.artworkUrl = url; }

    public UploadStatus getUploadStatus()                    { return uploadStatus; }
    public void         setUploadStatus(UploadStatus s)      { this.uploadStatus = s; }

    public String  getReviewNote()                   { return reviewNote; }
    public void    setReviewNote(String note)        { this.reviewNote = note; }

    public User    getReviewedBy()                   { return reviewedBy; }
    public void    setReviewedBy(User user)          { this.reviewedBy = user; }

    public LocalDateTime getReviewedAt()             { return reviewedAt; }
    public void          setReviewedAt(LocalDateTime t) { this.reviewedAt = t; }

    public long    getStreamCount()                  { return streamCount; }
    public void    setStreamCount(long count)        { this.streamCount = count; }

    public int     getLikeCount()                    { return likeCount; }
    public void    setLikeCount(int count)           { this.likeCount = count; }

    public boolean isSkipped()                       { return skipped; }
    public void    setSkipped(boolean skipped)       { this.skipped = skipped; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void          setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }

    public Long getArtistProfileId() {
        return artist != null ? artist.getId() : null;
    }

    public Long getArtistId() {
        return artist != null ? artist.getId() : null;
    }



    public String getDurationFormatted() {
        if (durationSeconds <= 0) {
            return "0:00";
        }
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Transient
    private Double averageRating = 0.0;

    @Transient
    private int ratingCount = 0;

    @Transient
    private boolean favouritedByCurrentUser = false;

    public Double getAverageRating() {
        return averageRating != null ? averageRating : 0.0;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public boolean isFavouritedByCurrentUser() {
        return favouritedByCurrentUser;
    }

    public void setFavouritedByCurrentUser(boolean favouritedByCurrentUser) {
        this.favouritedByCurrentUser = favouritedByCurrentUser;
    }
}
