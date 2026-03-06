package com.team_inertia.gonly.service;

import com.team_inertia.gonly.dto.GemRequest;
import com.team_inertia.gonly.dto.GemResponse;
import com.team_inertia.gonly.enums.Category;
import com.team_inertia.gonly.enums.DifficultyLevel;
import com.team_inertia.gonly.enums.GemStatus;
import com.team_inertia.gonly.enums.LocationSource;
import com.team_inertia.gonly.model.GemImage;
import com.team_inertia.gonly.model.HiddenGem;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.repo.GemImageRepository;
import com.team_inertia.gonly.repo.HiddenGemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GemService {

    @Autowired
    private HiddenGemRepository gemRepository;

    @Autowired
    private GemImageRepository gemImageRepository;

    // ==================== CREATE GEM ====================

    public GemResponse createGem(GemRequest request, User user) {
        HiddenGem gem = new HiddenGem();
        gem.setName(request.getName());
        gem.setDescription(request.getDescription());
        gem.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        gem.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        gem.setLocationSource(LocationSource.valueOf(request.getLocationSource()));
        gem.setCategory(Category.valueOf(request.getCategory()));
        gem.setState(request.getState());
        gem.setNearestTown(request.getNearestTown());
        gem.setAddressAuto(request.getAddressAuto());
        gem.setTravelTips(request.getTravelTips());
        gem.setHowToReach(request.getHowToReach());
        gem.setEntryFee(request.getEntryFee());
        gem.setBestSeasonStart(request.getBestSeasonStart());
        gem.setBestSeasonEnd(request.getBestSeasonEnd());
        gem.setSeasonWarning(request.getSeasonWarning());
        gem.setSafetyNote(request.getSafetyNote());
        gem.setLocalContact(request.getLocalContact());

        if (request.getDifficultyLevel() != null) {
            gem.setDifficultyLevel(DifficultyLevel.valueOf(request.getDifficultyLevel()));
        }
        if (request.getNetworkAvailable() != null) {
            gem.setNetworkAvailable(request.getNetworkAvailable());
        }

        gem.setSubmittedBy(user);
        gem.setStatus(GemStatus.APPROVED);

        HiddenGem savedGem = gemRepository.save(gem);
        return GemResponse.fromEntity(savedGem);
    }

    // ==================== ADD IMAGE ====================

    public void addImageToGem(Long gemId, MultipartFile file, User user) throws IOException {
        HiddenGem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found with id: " + gemId));

        if (!gem.getSubmittedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only add images to your own gems");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("Only JPEG and PNG images are allowed");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("Image size must be less than 2MB");
        }

        GemImage image = new GemImage();
        image.setGem(gem);
        image.setImageData(file.getBytes());
        image.setImageType(contentType);
        image.setFileSizeBytes((int) file.getSize());

        gemImageRepository.save(image);
    }

    // ==================== GET ALL ====================

    public List<GemResponse> getAllApprovedGems() {
        return gemRepository.findByStatus(GemStatus.APPROVED)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    // ==================== GET BY ID ====================

    public GemResponse getGemById(Long id) {
        HiddenGem gem = gemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gem not found with id: " + id));
        gem.setViewCount(gem.getViewCount() + 1);
        gemRepository.save(gem);
        return GemResponse.fromEntity(gem);
    }

    // ==================== BASIC SEARCH (Name only) ====================

    public List<GemResponse> searchByName(String name) {
        return gemRepository.findByStatusAndNameContainingIgnoreCase(GemStatus.APPROVED, name)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    // ==================== ✅ NEW: ADVANCED SEARCH ====================

    /**
     * Smart search that:
     * 1. Cleans and normalizes the query
     * 2. Searches across multiple fields (name, description, state, nearestTown)
     * 3. Returns results sorted by relevance
     */
    public List<GemResponse> advancedSearch(String query) {
        // Clean the query
        String cleanQuery = cleanSearchQuery(query);

        if (cleanQuery.isEmpty()) {
            return getAllApprovedGems();
        }

        // Use the relevance-based search
        return gemRepository.searchWithRelevance(GemStatus.APPROVED, cleanQuery)
                .stream()
                .map(GemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Multi-keyword search: splits query into words and finds gems matching ANY word
     * Example: "clean village meghalaya" finds gems with "clean" OR "village" OR "meghalaya"
     */
    public List<GemResponse> multiKeywordSearch(String query) {
        String cleanQuery = cleanSearchQuery(query);

        if (cleanQuery.isEmpty()) {
            return getAllApprovedGems();
        }

        // Split into keywords
        String[] keywords = cleanQuery.split("\\s+");

        if (keywords.length == 1) {
            // Single word - use advanced search
            return advancedSearch(cleanQuery);
        }

        // Multiple keywords - find gems matching ANY keyword, then rank
        List<HiddenGem> allResults = new ArrayList<>();

        for (String keyword : keywords) {
            if (keyword.length() >= 2) { // Ignore very short words
                List<HiddenGem> results = gemRepository.searchAllFields(GemStatus.APPROVED, keyword);
                for (HiddenGem gem : results) {
                    if (!allResults.contains(gem)) {
                        allResults.add(gem);
                    }
                }
            }
        }

        // Sort by how many keywords match (more matches = higher rank)
        allResults.sort((g1, g2) -> {
            int score1 = countKeywordMatches(g1, keywords);
            int score2 = countKeywordMatches(g2, keywords);
            if (score2 != score1) {
                return score2 - score1; // Higher score first
            }
            // If same score, sort by rating
            return g2.getAvgRating().compareTo(g1.getAvgRating());
        });

        return allResults.stream()
                .map(GemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Fuzzy search suggestions - for "did you mean" feature
     * Returns gems even with slight typos
     */
    public List<GemResponse> fuzzySearch(String query) {
        String cleanQuery = cleanSearchQuery(query);

        if (cleanQuery.isEmpty()) {
            return getAllApprovedGems();
        }

        // Try exact match first
        List<HiddenGem> exactResults = gemRepository.searchAllFields(GemStatus.APPROVED, cleanQuery);

        if (!exactResults.isEmpty()) {
            return exactResults.stream()
                    .map(GemResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        // No exact match - try partial matches
        // Search with first 3 characters (handles typos at the end)
        if (cleanQuery.length() > 3) {
            String partialQuery = cleanQuery.substring(0, 3);
            List<HiddenGem> partialResults = gemRepository.searchAllFields(GemStatus.APPROVED, partialQuery);

            if (!partialResults.isEmpty()) {
                return partialResults.stream()
                        .map(GemResponse::fromEntity)
                        .collect(Collectors.toList());
            }
        }

        // Still no results - return empty list
        return new ArrayList<>();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Clean and normalize search query
     */
    private String cleanSearchQuery(String query) {
        if (query == null) {
            return "";
        }

        return query
                .trim()
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // Remove special characters
                .replaceAll("\\s+", " ");           // Normalize whitespace
    }

    /**
     * Count how many keywords match a gem's fields
     */
    private int countKeywordMatches(HiddenGem gem, String[] keywords) {
        int count = 0;
        String searchText = (
                (gem.getName() != null ? gem.getName() : "") + " " +
                        (gem.getDescription() != null ? gem.getDescription() : "") + " " +
                        (gem.getState() != null ? gem.getState() : "") + " " +
                        (gem.getNearestTown() != null ? gem.getNearestTown() : "")
        ).toLowerCase();

        for (String keyword : keywords) {
            if (searchText.contains(keyword.toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    // ==================== FILTER METHODS ====================

    public List<GemResponse> filterByCategory(String category) {
        Category cat = Category.valueOf(category.toUpperCase());
        return gemRepository.findByStatusAndCategory(GemStatus.APPROVED, cat)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    public List<GemResponse> filterByState(String state) {
        return gemRepository.findByStatusAndStateIgnoreCase(GemStatus.APPROVED, state)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    public List<GemResponse> getNearbyGems(double lat, double lng, double radiusKm) {
        return gemRepository.findNearbyGems(lat, lng, radiusKm)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    public List<GemResponse> getMySubmissions(Long userId) {
        return gemRepository.findBySubmittedById(userId)
                .stream().map(GemResponse::fromEntity).collect(Collectors.toList());
    }

    public void deleteGem(Long gemId, User user) {
        HiddenGem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found with id: " + gemId));
        if (!gem.getSubmittedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own gems");
        }
        gemRepository.delete(gem);
    }

    public GemImage getImageById(Long imageId) {
        return gemImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }
}