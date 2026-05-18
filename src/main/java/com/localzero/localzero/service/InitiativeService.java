package com.localzero.localzero.service;

import com.localzero.localzero.model.*;
import com.localzero.localzero.repository.InitiativeRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InitiativeService {

    private final InitiativeRepository initiativeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InitiativeService(
            InitiativeRepository initiativeRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.initiativeRepository = initiativeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<Initiative> getVisibleInitiativesForUser(Long userId) {
        User user = getUserById(userId);

        return initiativeRepository.findAll()
                .stream()
                .filter(initiative -> isVisibleToUser(initiative, user))
                .toList();
    }

    public Initiative getInitiativeById(int initiativeId, Long userId) {
        Initiative initiative = initiativeRepository.findById(initiativeId)
                .orElseThrow(() -> new RuntimeException("Initiative not found"));

        User user = getUserById(userId);

        if (!isVisibleToUser(initiative, user)) {
            throw new RuntimeException("You are not allowed to view this initiative");
        }

        return initiative;
    }

    public Initiative createInitiative(Initiative initiative, Long creatorId) {
        User creator = getUserById(creatorId);

        if (initiative.getTitle() == null || initiative.getTitle().isBlank()) {
            throw new RuntimeException("Title is required");
        }

        if (initiative.getDescription() == null) {
            initiative.setDescription("");
        }

        if (initiative.getCategory() == null || initiative.getCategory().isBlank()) {
            initiative.setCategory("other");
        }

        if (initiative.getVisibility() == null) {
            initiative.setVisibility(Visibility.PUBLIC);
        }

        if (initiative.getStartDate() == null) {
            initiative.setStartDate(LocalDate.now());
        }

        if (initiative.getLocation() == null || initiative.getLocation().isBlank()) {
            initiative.setLocation(creator.getLocation());
        }

        initiative.setCreator_id(creator.getId().intValue());
        Initiative newInitiative = initiativeRepository.save(initiative);

        notifyRelevantUsers(newInitiative, creator);

        return newInitiative;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isVisibleToUser(Initiative initiative, User user) {
        if (initiative.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        if (initiative.getVisibility() == Visibility.SPECIFIC) {
            return initiative.getLocation() != null
                    && user.getLocation() != null
                    && initiative.getLocation().equalsIgnoreCase(user.getLocation());
        }

        return false;
    }

    private void notifyRelevantUsers(Initiative initiative, User creator) {
        String message = creator.getName() + " started a new initiative: " + initiative.getTitle() + "";

        userRepository.findAll().stream().filter(user -> !user.getId().equals(creator.getId()))
            .filter(user -> isVisibleToUser(initiative, user)).forEach(u -> {
                Notification n = new Notification();
                n.setUserId(String.valueOf(u.getId()));
                n.setType(NotificationType.JOIN_INITIATIVE);
                n.setContent(message);
                n.setRead(false);
                notificationService.notifyUser(n);
            });
    }

}