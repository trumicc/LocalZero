package com.localzero.localzero.repository;

import com.localzero.localzero.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
