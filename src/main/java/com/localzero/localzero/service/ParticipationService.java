package com.localzero.localzero.service;

import com.localzero.localzero.command.*;
import com.localzero.localzero.model.Comment;
import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.model.Update;
import com.localzero.localzero.model.User;
import com.localzero.localzero.repository.CommentRepository;
import com.localzero.localzero.repository.InitiativeRepository;
import com.localzero.localzero.repository.UpdateRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {
    private UserRepository userRepository;
    private InitiativeRepository initiativeRepository;
    private CommentRepository commentRepository;
    private UpdateRepository updateRepository;

    public ParticipationService(UserRepository ur, InitiativeRepository ir, CommentRepository cr, UpdateRepository upr) {
        this.userRepository = ur;
        this.initiativeRepository = ir;
        this.commentRepository = cr;
        this.updateRepository = upr;
    }

    public void join(Long userId, int initiativeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Initiative initiative = initiativeRepository.findById(initiativeId).orElseThrow();
        ActionCommand actionCommand = new JoinInitiative(initiative, user);
        actionCommand.execute();
    }

    public void postUpdate(Long userId, String updateContent, int initiativeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Initiative initiative = initiativeRepository.findById(initiativeId).orElseThrow();
        Update update = new Update();
        update.setContent(updateContent);
        ActionCommand actionCommand = new PostUpdateCommand(initiative, update);
        actionCommand.execute();
    }

    public void comment(String commentContent, int updateId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Update update = updateRepository.findById(updateId).orElseThrow();
        Comment comment = new Comment();
        comment.setContent(commentContent);
        comment.setAuthor(user);
        commentRepository.save(comment);
        ActionCommand actionCommand = new CommentUpdateCommand(update, comment);
        actionCommand.execute();
        updateRepository.save(update);
    }

    public void like(int updateId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Update update = updateRepository.findById(updateId).orElseThrow();
        ActionCommand actionCommand = new LikeUpdateCommand(update, user);
        actionCommand.execute();
        updateRepository.save(update);
    }
}