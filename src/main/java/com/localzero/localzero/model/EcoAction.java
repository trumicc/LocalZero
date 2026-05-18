package com.localzero.localzero.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EcoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EcoActionType actionType;

    private String note;

    private double carbonSaved;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;
    public EcoAction() {
    }

    public EcoAction(EcoActionType actionType,
                     String note,
                     double carbonSaved,
                     User user) {

        this.actionType = actionType;
        this.note = note;
        this.carbonSaved = carbonSaved;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public EcoActionType getActionType() {
        return actionType;
    }

    public void setActionType(EcoActionType actionType) {
        this.actionType = actionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getCarbonSaved() {
        return carbonSaved;
    }

    public void setCarbonSaved(double carbonSaved) {
        this.carbonSaved = carbonSaved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}