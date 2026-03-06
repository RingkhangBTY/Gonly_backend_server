package com.team_inertia.gonly.service;

import com.team_inertia.gonly.dto.EventRequest;
import com.team_inertia.gonly.dto.EventResponse;
import com.team_inertia.gonly.enums.EventType;
import com.team_inertia.gonly.enums.GemStatus;
import com.team_inertia.gonly.enums.LocationSource;
import com.team_inertia.gonly.model.EventImage;
import com.team_inertia.gonly.model.LocalEvent;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.repo.EventImageRepository;
import com.team_inertia.gonly.repo.LocalEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private LocalEventRepository eventRepository;

    @Autowired
    private EventImageRepository eventImageRepository;

    public EventResponse createEvent(EventRequest request, User user) {
        LocalEvent event = new LocalEvent();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        event.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        event.setLocationSource(LocationSource.valueOf(request.getLocationSource()));
        event.setState(request.getState());
        event.setEventType(EventType.valueOf(request.getEventType()));
        event.setStartDate(LocalDate.parse(request.getStartDate()));
        event.setEndDate(LocalDate.parse(request.getEndDate()));
        event.setSubmittedBy(user);
        event.setStatus(GemStatus.APPROVED);

        LocalEvent savedEvent = eventRepository.save(event);
        return EventResponse.fromEntity(savedEvent);
    }

    public void addImageToEvent(Long eventId, MultipartFile file, User user) throws IOException {
        LocalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (event.getSubmittedBy() != null && !event.getSubmittedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only add images to your own events");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("Only JPEG and PNG images are allowed");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("Image size must be less than 2MB");
        }

        EventImage image = new EventImage();
        image.setEvent(event);
        image.setImageData(file.getBytes());
        image.setImageType(contentType);
        image.setFileSizeBytes((int) file.getSize());

        eventImageRepository.save(image);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findByStatus(GemStatus.APPROVED)
                .stream().map(EventResponse::fromEntity).collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByStatusAndEndDateGreaterThanEqual(GemStatus.APPROVED, LocalDate.now())
                .stream().map(EventResponse::fromEntity).collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByState(String state) {
        return eventRepository.findByStatusAndStateIgnoreCase(GemStatus.APPROVED, state)
                .stream().map(EventResponse::fromEntity).collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        LocalEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return EventResponse.fromEntity(event);
    }

    public EventImage getImageById(Long imageId) {
        return eventImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
    }
}