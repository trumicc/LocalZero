package com.localzero.localzero.strategy;

import org.springframework.stereotype.Component;

@Component
public class CarbonStrategyFactory {

    public CarbonStrategy getStrategy(String actionType) {

        return switch (actionType.toUpperCase()) {

            case "BIKED_TO_WORK" -> new BikeStrategy();

            case "COMPOSTED" -> new CompostStrategy();

            case "SKIPPED_MEAT" -> new SkipMeatStrategy();

            default -> throw new IllegalArgumentException("Unknown action type");
        };
    }
}