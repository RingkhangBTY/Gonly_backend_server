package com.team_inertia.gonly.dto;

import com.team_inertia.gonly.model.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String state;
    private String eventType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String submittedByName;
    private Long submittedById;
    private LocalDateTime createdAt;
    private List<Long> imageIds;

    public EventResponse() {}

    public static EventResponse fromEntity(LocalEvent event) {
        EventResponse r = new EventResponse();
        r.setId(event.getId());
        r.setTitle(event.getTitle());
        r.setDescription(event.getDescription());
        r.setLatitude(event.getLatitude().doubleValue());
        r.setLongitude(event.getLongitude().doubleValue());
        r.setLocationSource(event.getLocationSource().name());
        r.setState(event.getState());
        r.setEventType(event.getEventType().name());
        r.setStartDate(event.getStartDate());
        r.setEndDate(event.getEndDate());
        r.setStatus(event.getStatus().name());

        if (event.getSubmittedBy() != null) {
            r.setSubmittedByName(event.getSubmittedBy().getFullName());
            r.setSubmittedById(event.getSubmittedBy().getId());
        }

        r.setCreatedAt(event.getCreatedAt());

        if (event.getImages() != null) {
            r.setImageIds(
                    event.getImages().stream()
                            .map(img -> img.getId())
                            .collect(Collectors.toList())
            );
        }
        return r;
    }

}