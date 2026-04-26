package com.localzero.localzero.service;

import com.localzero.localzero.repository.CommentRepository;
import com.localzero.localzero.repository.InitiativeRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {
    private UserRepository userRepository;
    private InitiativeRepository initiativeRepository;
    private CommentRepository commentRepository;
}
