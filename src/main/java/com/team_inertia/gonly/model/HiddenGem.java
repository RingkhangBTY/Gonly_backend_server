package com.team_inertia.gonly.model;

import com.team_inertia.gonly.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hidden_gems")
public class HiddenGem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // ==================== LOCATION ====================

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_source", nullable = false, length = 20)
    private LocationSource locationSource;

    // ==================== CLASSIFICATION ====================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(name = "nearest_town", length = 200)
    private String nearestTown;

    @Column(name = "address_auto", length = 500)
    private String addressAuto;

    // ==================== TRAVEL INFO ====================

    @Column(name = "travel_tips", columnDefinition = "TEXT")
    private String travelTips;

    @Column(name = "how_to_reach", columnDefinition = "TEXT")
    private String howToReach;

    @Column(name = "entry_fee", length = 100)
    private String entryFee;

    // ==================== SEASONAL INFO ====================

    @Column(name = "best_season_start")
    private Integer bestSeasonStart;

    @Column(name = "best_season_end")
    private Integer bestSeasonEnd;

    @Column(name = "season_warning", length = 500)
    private String seasonWarning;

    // ==================== SAFETY & ACCESS ====================

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", length = 20)
    private DifficultyLevel difficultyLevel = DifficultyLevel.EASY;

    @Column(name = "safety_note", columnDefinition = "TEXT")
    private String safetyNote;

    @Column(name = "local_contact", length = 200)
    private String localContact;

    @Column(name = "network_available")
    private Boolean networkAvailable = true;

    // ==================== STATS ====================

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    // ==================== MODERATION ====================

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GemStatus status = GemStatus.APPROVED;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // ==================== RELATIONSHIPS ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;

    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "gem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // ==================== TIMESTAMPS ====================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== CONSTRUCTORS ====================

    public HiddenGem() {}

    // ==================== GETTERS & SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocationSource getLocationSource() {
        return locationSource;
    }

    public void setLocationSource(LocationSource locationSource) {
        this.locationSource = locationSource;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNearestTown() {
        return nearestTown;
    }

    public void setNearestTown(String nearestTown) {
        this.nearestTown = nearestTown;
    }

    public String getAddressAuto() {
        return addressAuto;
    }

    public void setAddressAuto(String addressAuto) {
        this.addressAuto = addressAuto;
    }

    public String getTravelTips() {
        return travelTips;
    }

    public void setTravelTips(String travelTips) {
        this.travelTips = travelTips;
    }

    public String getHowToReach() {
        return howToReach;
    }

    public void setHowToReach(String howToReach) {
        this.howToReach = howToReach;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public Integer getBestSeasonStart() {
        return bestSeasonStart;
    }

    public void setBestSeasonStart(Integer bestSeasonStart) {
        this.bestSeasonStart = bestSeasonStart;
    }

    public Integer getBestSeasonEnd() {
        return bestSeasonEnd;
    }

    public void setBestSeasonEnd(Integer bestSeasonEnd) {
        this.bestSeasonEnd = bestSeasonEnd;
    }

    public String getSeasonWarning() {
        return seasonWarning;
    }

    public void setSeasonWarning(String seasonWarning) {
        this.seasonWarning = seasonWarning;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getSafetyNote() {
        return safetyNote;
    }

    public void setSafetyNote(String safetyNote) {
        this.safetyNote = safetyNote;
    }

    public String getLocalContact() {
        return localContact;
    }

    public void setLocalContact(String localContact) {
        this.localContact = localContact;
    }

    public Boolean getNetworkAvailable() {
        return networkAvailable;
    }

    public void setNetworkAvailable(Boolean networkAvailable) {
        this.networkAvailable = networkAvailable;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(BigDecimal avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public GemStatus getStatus() {
        return status;
    }

    public void setStatus(GemStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    public List<GemImage> getImages() {
        return images;
    }

    public void setImages(List<GemImage> images) {
        this.images = images;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}