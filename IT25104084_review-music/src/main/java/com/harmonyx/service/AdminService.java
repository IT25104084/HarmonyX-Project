package com.harmonyx.service;

import com.harmonyx.exception.ResourceNotFoundException;
import com.harmonyx.model.*;
import com.harmonyx.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ADMINISTRATION MODULE.
 * UC-06 Suspend User Account, artist verification, content review, reports.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ListenerProfileRepository listenerProfileRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SongRepository songRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminService(UserRepository userRepository,
                        ListenerProfileRepository listenerProfileRepository,
                        ArtistProfileRepository artistProfileRepository,
                        SongRepository songRepository,
                        AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.listenerProfileRepository = listenerProfileRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.songRepository = songRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // ------ User management ------

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<ListenerProfile> getAllListeners() { return listenerProfileRepository.findAll(); }
    public List<ArtistProfile>  getAllArtists()   { return artistProfileRepository.findAll(); }

    /** UC-06 Suspend User Account */
    @Transactional
    public void suspendUser(Long userId, User admin, String reason) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        target.setActive(false);
        userRepository.save(target);
        auditLogRepository.save(new AuditLog(admin, "SUSPEND_USER", "User", userId,
                "Reason: " + reason + " | User: " + target.getEmail()));
    }

    @Transactional
    public void reactivateUser(Long userId, User admin) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        target.setActive(true);
        userRepository.save(target);
        auditLogRepository.save(new AuditLog(admin, "REACTIVATE_USER", "User", userId,
                "User: " + target.getEmail()));
    }

    // ------ Artist verification ------

    public List<ArtistProfile> getPendingArtistVerifications() {
        return artistProfileRepository.findByVerificationStatus(VerificationStatus.PENDING);
    }

    @Transactional
    public void verifyArtist(Long artistId, boolean approve, User admin) {
        ArtistProfile artist = artistProfileRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found."));
        VerificationStatus newStatus = approve ? VerificationStatus.VERIFIED : VerificationStatus.REJECTED;
        artist.setVerificationStatus(newStatus);
        if (approve) artist.setVerifiedAt(LocalDateTime.now());
        artistProfileRepository.save(artist);
        auditLogRepository.save(new AuditLog(admin, "ARTIST_VERIFICATION: " + newStatus.name(),
                "ArtistProfile", artistId, "Artist: " + artist.getStageName()));
    }

    // ------ Stats ------

    public long getTotalUserCount()  { return userRepository.count(); }
    public long getTotalSongCount()  { return songRepository.count(); }

    public long getPendingReviewCount() {
        return songRepository.findByUploadStatusOrderByCreatedAtDesc(UploadStatus.PENDING_APPROVAL).size();
    }

    public long getPendingVerificationCount() {
        return artistProfileRepository.findByVerificationStatus(VerificationStatus.PENDING).size();
    }

    public long getTotalStreamCount() {
        return songRepository.findByUploadStatusOrderByCreatedAtDesc(UploadStatus.APPROVED)
                .stream().mapToLong(Song::getStreamCount).sum();
    }

    public Map<String, Long> getSongsByGenre() {
        return songRepository.findByUploadStatusOrderByCreatedAtDesc(UploadStatus.APPROVED)
                .stream()
                .filter(s -> s.getGenre() != null && !s.getGenre().isBlank())
                .collect(Collectors.groupingBy(Song::getGenre, Collectors.counting()));
    }

    public Map<String, Long> getSongsByStatus() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (UploadStatus status : UploadStatus.values()) {
            long count = songRepository.findByUploadStatusOrderByCreatedAtDesc(status).size();
            result.put(status.getDisplayName(), count);
        }
        return result;
    }

    public Map<String, Long> getUsersByRole() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<User> all = userRepository.findAll();
        for (Role role : Role.values()) {
            long count = all.stream().filter(u -> u.getRole() == role).count();
            result.put(role.getDisplayName(), count);
        }
        return result;
    }

    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public List<Song> getTop10SongsByStreams() {
        return songRepository.findTop10ByUploadStatusOrderByStreamCountDesc(UploadStatus.APPROVED);
    }
}
