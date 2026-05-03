package com.localzero.localzero.service;

import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.model.User;
import com.localzero.localzero.model.Visibility;
import com.localzero.localzero.repository.InitiativeRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InitiativeService {

    private final InitiativeRepository initiativeRepository;
    private final UserRepository userRepository;

    public InitiativeService(
            InitiativeRepository initiativeRepository,
            UserRepository userRepository
    ) {
        this.initiativeRepository = initiativeRepository;
        this.userRepository = userRepository;
    }

    public List<Initiative> getVisibleInitiativesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return initiativeRepository.findAll()
                .stream()
                .filter(initiative ->
                        initiative.getVisibility() == Visibility.PUBLIC ||
                                initiative.getLocation().equalsIgnoreCase(user.getLocation())
                )
                .toList();
    }

    public Initiative getInitiativeById(int initiativeId, Long userId) {
        Initiative initiative = initiativeRepository.findById(initiativeId)
                .orElseThrow(() -> new RuntimeException("Initiative not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isVisible =
                initiative.getVisibility() == Visibility.PUBLIC ||
                        initiative.getLocation().equalsIgnoreCase(user.getLocation());

        if (!isVisible) {
            throw new RuntimeException("You are not allowed to view this initiative");
        }

        return initiative;
    }

    public Initiative createInitiative(Initiative initiative, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        initiative.setCreator_id(creator.getId().intValue());
        initiative.setStartDate(LocalDateTime.now());

        if (initiative.getVisibility() == null) {
            initiative.setVisibility(Visibility.PUBLIC);
        }

        return initiativeRepository.save(initiative);
    }
}