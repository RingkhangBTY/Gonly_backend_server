package com.team_inertia.gonly.repo;

import com.team_inertia.gonly.enums.Category;
import com.team_inertia.gonly.enums.GemStatus;
import com.team_inertia.gonly.model.HiddenGem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HiddenGemRepository extends JpaRepository<HiddenGem, Long> {

    List<HiddenGem> findByStatus(GemStatus status);

    // ✅ ORIGINAL: Basic search by name (case-insensitive)
    List<HiddenGem> findByStatusAndNameContainingIgnoreCase(GemStatus status, String name);

    // ✅ NEW: Search across MULTIPLE fields (name, description, state, nearestTown)
    @Query("SELECT g FROM HiddenGem g WHERE g.status = :status AND (" +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.state) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.nearestTown) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.addressAuto) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ")")
    List<HiddenGem> searchAllFields(@Param("status") GemStatus status, @Param("query") String query);

    // ✅ NEW: Search with relevance/priority (name matches first, then description)
    @Query("SELECT g FROM HiddenGem g WHERE g.status = :status AND (" +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.state) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.nearestTown) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ") ORDER BY " +
            "CASE WHEN LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) THEN 1 " +
            "WHEN LOWER(g.state) LIKE LOWER(CONCAT('%', :query, '%')) THEN 2 " +
            "WHEN LOWER(g.nearestTown) LIKE LOWER(CONCAT('%', :query, '%')) THEN 3 " +
            "ELSE 4 END, " +
            "g.avgRating DESC")
    List<HiddenGem> searchWithRelevance(@Param("status") GemStatus status, @Param("query") String query);

    List<HiddenGem> findByStatusAndCategory(GemStatus status, Category category);

    List<HiddenGem> findByStatusAndStateIgnoreCase(GemStatus status, String state);

    List<HiddenGem> findBySubmittedById(Long userId);

    @Query(value = "SELECT * FROM hidden_gems WHERE status = 'APPROVED' AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(latitude)))) < :radiusKm " +
            "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(latitude))))",
            nativeQuery = true)
    List<HiddenGem> findNearbyGems(@Param("lat") double lat,
                                   @Param("lng") double lng,
                                   @Param("radiusKm") double radiusKm);
}