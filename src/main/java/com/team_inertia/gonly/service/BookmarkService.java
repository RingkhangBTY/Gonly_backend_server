package com.team_inertia.gonly.service;

import com.team_inertia.gonly.dto.GemResponse;
import com.team_inertia.gonly.model.Bookmark;
import com.team_inertia.gonly.model.HiddenGem;
import com.team_inertia.gonly.model.User;
import com.team_inertia.gonly.repo.BookmarkRepository;
import com.team_inertia.gonly.repo.HiddenGemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private HiddenGemRepository gemRepository;

    public boolean toggleBookmark(Long gemId, User user) {
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndGemId(user.getId(), gemId);

        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        } else {
            HiddenGem gem = gemRepository.findById(gemId)
                    .orElseThrow(() -> new RuntimeException("Gem not found with id: " + gemId));
            Bookmark bookmark = new Bookmark(user, gem);
            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    public List<GemResponse> getMyBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId)
                .stream()
                .map(bookmark -> GemResponse.fromEntity(bookmark.getGem()))
                .collect(Collectors.toList());
    }

    public boolean isBookmarked(Long gemId, Long userId) {
        return bookmarkRepository.existsByUserIdAndGemId(userId, gemId);
    }
}