package com.localzero.localzero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int creator_id;
    private String title;
    private String location;
    private String description;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    @ManyToMany
    private List<User> participants = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Update> updates = new ArrayList<>();
}
