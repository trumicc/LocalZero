package com.localzero.localzero.repository;

import com.localzero.localzero.model.EcoAction;
import com.localzero.localzero.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EcoActionRepository extends JpaRepository<EcoAction, Long> {

    List<EcoAction> findByUser(User user);
}