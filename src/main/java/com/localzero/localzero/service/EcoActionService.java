package com.localzero.localzero.service;

import com.localzero.localzero.model.EcoAction;
import com.localzero.localzero.model.EcoActionType;
import com.localzero.localzero.model.User;
import com.localzero.localzero.repository.EcoActionRepository;
import com.localzero.localzero.repository.UserRepository;
import com.localzero.localzero.strategy.CarbonStrategy;
import com.localzero.localzero.strategy.CarbonStrategyFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EcoActionService {

    private final EcoActionRepository ecoActionRepository;
    private final CarbonStrategyFactory strategyFactory;
    private final UserRepository userRepository;


    public EcoActionService(EcoActionRepository ecoActionRepository,
                            CarbonStrategyFactory strategyFactory, UserRepository userRepository) {

        this.ecoActionRepository = ecoActionRepository;
        this.strategyFactory = strategyFactory;
        this.userRepository = userRepository;
    }

    public EcoAction logAction(Long userId, String actionType, String note) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarbonStrategy strategy = strategyFactory.getStrategy(actionType);
        double carbonSaved = strategy.calculateCarbonSaved();

        EcoAction action = new EcoAction();
        action.setUser(user);
        action.setActionType(EcoActionType.valueOf(actionType));
        action.setNote(note);
        action.setCarbonSaved(carbonSaved);
        action.setCreatedAt(LocalDateTime.now());

        return ecoActionRepository.save(action);
    }

    public double getTotalCarbonSaved(User user){

        List<EcoAction> actions =
                ecoActionRepository.findByUser(user);

        return actions.stream()
                .mapToDouble(EcoAction::getCarbonSaved)
                .sum();
    }

    public List<EcoAction> getUserActions(User user) {
        return ecoActionRepository.findByUser(user);
    }
}