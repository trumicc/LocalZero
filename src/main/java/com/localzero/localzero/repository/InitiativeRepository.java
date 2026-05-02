package com.localzero.localzero.repository;

import com.localzero.localzero.model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InitiativeRepository extends JpaRepository<Initiative, Integer> {
}
