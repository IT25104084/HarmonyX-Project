package com.harmonyx.repository;

import com.harmonyx.model.ArtistProfile;
import com.harmonyx.model.Song;
import com.harmonyx.model.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SongRepository — Spring Data JPA repository for {@link Song} entities.
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByUploadStatusOrderByCreatedAtDesc(UploadStatus status);

    List<Song> findByArtistOrderByCreatedAtDesc(ArtistProfile artist);

    List<Song> findByArtistAndUploadStatusOrderByCreatedAtDesc(ArtistProfile artist, UploadStatus status);

    List<Song> findTop10ByUploadStatusOrderByStreamCountDesc(UploadStatus status);

    List<Song> findTop10ByUploadStatusOrderByCreatedAtDesc(UploadStatus status);

    @Query("SELECT s FROM Song s WHERE s.uploadStatus = 'APPROVED' " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(s.genre) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Song> searchApproved(@Param("q") String query);

    List<Song> findByAlbum(com.harmonyx.model.Album album);
}
