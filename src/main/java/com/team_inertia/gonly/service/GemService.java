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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
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

//        if (!gem.getSubmittedBy().getId().equals(user.getId())) {  // letting every signed-up user permission to upload image.
//            throw new RuntimeException("You can only add images to your own gems");
//        }

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
        image.setUploadedBy(user);

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


    // ==================== GET ALL IMAGES WITH BYTES (Base64) ====================
    public List<Map<String, Object>> getAllImageBytesForGem(Long gemId) {
        gemRepository.findById(gemId)
                .orElseThrow(() -> {
                    log.warn("Gem not found with id: {}", gemId);
                    return new RuntimeException("Gem not found with id: " + gemId);
                });

        List<GemImage> images = gemImageRepository.findByGemId(gemId);

        log.debug("Found {} images for gem id: {}", images.size(), gemId);

        return images.stream().map(image -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", image.getId());
            data.put("imageType", image.getImageType());
            data.put("fileSizeBytes", image.getFileSizeBytes());
            data.put("createdAt", image.getCreatedAt());

            // Convert bytes to Base64 string (Android can decode this directly)
            String base64Image = java.util.Base64.getEncoder().encodeToString(image.getImageData());
            data.put("imageBase64", base64Image);

            if (image.getUploadedBy() != null) {
                data.put("uploadedByName", image.getUploadedBy().getFullName());
                data.put("uploadedById", image.getUploadedBy().getId());
            }

            return data;
        }).collect(Collectors.toList());
    }


    // =============== smart search for place/hidden gem ==========

    /**
     * Smart search that automatically tries different strategies:
     *
     * Strategy 1: Search by Name (exact match on name field)
     *    ↓ if no results
     * Strategy 2: Advanced Search (search across all fields with relevance)
     *    ↓ if no results
     * Strategy 3: Multi-Keyword Search (split query, match ANY keyword)
     *    ↓ if no results
     * Strategy 4: Fuzzy Search (handle typos with partial matching)
     *    ↓ if no results
     * Return empty list
     *
     * @param query The search query from user
     * @return List of matching gems (uses first strategy that returns results)
     */
    public List<GemResponse> smartSearch(String query) {
        // Clean the query first
        String cleanQuery = cleanSearchQuery(query);

        // If empty query, return all approved gems
        if (cleanQuery.isEmpty()) {
            return getAllApprovedGems();
        }

        List<GemResponse> results;

        // STRATEGY 1: Search by Name (fastest, most precise)
        results = searchByNameInternal(cleanQuery);
        if (!results.isEmpty()) {
            return results;
        }

        // STRATEGY 2: Advanced Search (search all fields with relevance)
        results = advancedSearchInternal(cleanQuery);
        if (!results.isEmpty()) {
            return results;
        }

        // STRATEGY 3: Multi-Keyword Search (for multi-word queries)
        results = multiKeywordSearchInternal(cleanQuery);
        if (!results.isEmpty()) {
            return results;
        }

        // STRATEGY 4: Fuzzy Search (handle typos)
        results = fuzzySearchInternal(cleanQuery);
        if (!results.isEmpty()) {
            return results;
        }

        // No results found with any strategy

        return new ArrayList<>();
    }

    // INTERNAL SEARCH METHODS (Private — used by smartSearch)
    /**
     * Strategy 1: Search by name only (case-insensitive LIKE)
     */
    private List<GemResponse> searchByNameInternal(String query) {
        List<HiddenGem> results = gemRepository
                .findByStatusAndNameContainingIgnoreCase(GemStatus.APPROVED, query);

        return results.stream()
                .map(GemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Strategy 2: Advanced search across multiple fields with relevance ranking
     */
    private List<GemResponse> advancedSearchInternal(String query) {
        List<HiddenGem> results = gemRepository
                .searchWithRelevance(GemStatus.APPROVED, query);

        return results.stream()
                .map(GemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Strategy 3: Multi-keyword search (split query, match ANY keyword)
     */
    private List<GemResponse> multiKeywordSearchInternal(String query) {
        // Split into keywords
        String[] keywords = query.split("\\s+");

        // If single word, return empty (already tried in strategies 1 & 2)
        if (keywords.length <= 1) {
            return new ArrayList<>();
        }

        // Multiple keywords - find gems matching ANY keyword
        List<HiddenGem> allResults = new ArrayList<>();

        for (String keyword : keywords) {
            if (keyword.length() >= 2) { // Ignore very short words
                List<HiddenGem> keywordResults = gemRepository
                        .searchAllFields(GemStatus.APPROVED, keyword);

                for (HiddenGem gem : keywordResults) {
                    if (!containsGem(allResults, gem)) {
                        allResults.add(gem);
                    }
                }
            }
        }

        // Sort by number of keyword matches (more matches = higher rank)
        final String[] finalKeywords = keywords;
        allResults.sort((g1, g2) -> {
            int score1 = countKeywordMatches(g1, finalKeywords);
            int score2 = countKeywordMatches(g2, finalKeywords);
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
     * Strategy 4: Fuzzy search (handle typos with partial matching)
     */
    private List<GemResponse> fuzzySearchInternal(String query) {
        // Try partial match with first few characters
        // This handles typos at the end of words

        if (query.length() <= 3) {
            // Query too short for fuzzy matching
            return new ArrayList<>();
        }

        // Try with first 3 characters
        String partialQuery3 = query.substring(0, 3);
        List<HiddenGem> results = gemRepository
                .searchAllFields(GemStatus.APPROVED, partialQuery3);

        if (!results.isEmpty()) {
            return results.stream()
                    .map(GemResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        // Try with first 4 characters if query is long enough
        if (query.length() >= 5) {
            String partialQuery4 = query.substring(0, 4);
            results = gemRepository.searchAllFields(GemStatus.APPROVED, partialQuery4);

            return results.stream()
                    .map(GemResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    // HELPER METHODS
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
     * Check if a gem already exists in the list (by ID)
     */
    private boolean containsGem(List<HiddenGem> list, HiddenGem gem) {
        for (HiddenGem g : list) {
            if (g.getId().equals(gem.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count how many keywords match a gem's fields
     */
    private int countKeywordMatches(HiddenGem gem, String[] keywords) {
        int count = 0;
        String searchText = buildSearchText(gem);

        for (String keyword : keywords) {
            if (searchText.contains(keyword.toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Build searchable text from gem fields
     */
    private String buildSearchText(HiddenGem gem) {
        StringBuilder sb = new StringBuilder();

        if (gem.getName() != null) sb.append(gem.getName()).append(" ");
        if (gem.getDescription() != null) sb.append(gem.getDescription()).append(" ");
        if (gem.getState() != null) sb.append(gem.getState()).append(" ");
        if (gem.getNearestTown() != null) sb.append(gem.getNearestTown()).append(" ");
        if (gem.getAddressAuto() != null) sb.append(gem.getAddressAuto()).append(" ");
        if (gem.getTravelTips() != null) sb.append(gem.getTravelTips()).append(" ");

        return sb.toString().toLowerCase();
    }

    // ══════════════════════════════════════════════════════════════════
    // FILTER METHODS
    // ══════════════════════════════════════════════════════════════════

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