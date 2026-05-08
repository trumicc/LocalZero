package com.localzero.localzero.controller;

import com.localzero.localzero.model.EcoAction;
import com.localzero.localzero.model.User;
import com.localzero.localzero.service.EcoActionService;
import com.localzero.localzero.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sustainability")
public class EcoActionController {

    private final EcoActionService ecoActionService;
    private final UserService userService;

    public EcoActionController(EcoActionService ecoActionService,
                               UserService userService) {

        this.ecoActionService = ecoActionService;
        this.userService = userService;
    }

    @PostMapping("/log")
    public EcoAction logAction(HttpSession session,
                               @RequestParam String actionType,
                               @RequestParam(required = false) String note) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            throw new RuntimeException("Not logged in");
        }

        return ecoActionService.logAction(userId, actionType, note);
    }

    @GetMapping("/my-total")
    public double getMyTotal(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId)
                .orElseThrow();

        return ecoActionService.getTotalCarbonSaved(user);
    }

    @GetMapping("/my-actions")
    public List<EcoAction> getMyActions(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Not logged in");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ecoActionService.getUserActions(user);
    }
    private Long getLoggedInUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}