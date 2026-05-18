package com.localzero.localzero.service;

import com.localzero.localzero.command.*;
import com.localzero.localzero.model.*;
import com.localzero.localzero.repository.CommentRepository;
import com.localzero.localzero.repository.InitiativeRepository;
import com.localzero.localzero.repository.UpdateRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static io.micrometer.common.util.StringUtils.truncate;

@Service
public class ParticipationService {
    private UserRepository userRepository;
    private InitiativeRepository initiativeRepository;
    private CommentRepository commentRepository;
    private UpdateRepository updateRepository;
    private final NotificationService notificationService;

    public ParticipationService(UserRepository ur, InitiativeRepository ir, CommentRepository cr, UpdateRepository upr, NotificationService notificationService) {
        this.userRepository = ur;
        this.initiativeRepository = ir;
        this.commentRepository = cr;
        this.updateRepository = upr;
        this.notificationService = notificationService;
    }

    public void join(Long userId, int initiativeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Initiative initiative = initiativeRepository.findById(initiativeId).orElseThrow();

        boolean alreadyJoined = initiative.getParticipants()
                .stream()
                .anyMatch(participant -> participant.getId().equals(userId));

        if (alreadyJoined) {return;}

        ActionCommand actionCommand = new JoinInitiative(initiative, user);
        actionCommand.execute();
        initiativeRepository.save(initiative);
    }

    public void unjoin(Long userId, int initiativeId) {
        Initiative initiative = initiativeRepository.findById(initiativeId).orElseThrow();
        initiative.getParticipants().removeIf(participant -> participant.getId().equals(userId));
        initiativeRepository.save(initiative);
    }

    public void postUpdate(Long userId, String updateContent, int initiativeId) {
        User user = userRepository.findById(userId).orElseThrow();
        Initiative initiative = initiativeRepository.findById(initiativeId).orElseThrow();
        Update update = new Update();
        update.setContent(updateContent);
        update.setAuthor(user);
        update.setCreated_at(LocalDateTime.now());
        updateRepository.save(update);
        ActionCommand actionCommand = new PostUpdateCommand(initiative, update);
        actionCommand.execute();
        initiativeRepository.save(initiative);

        String message = user.getName() + " posted an update in " + initiative.getTitle() + ": " + truncate(updateContent, 60);

        getOtherParticipants(initiative, userId).forEach(recipient ->
                notificationService.notifyUser(buildNotification(
                        String.valueOf(recipient.getId()), NotificationType.NEW_UPDATE, message)));
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

        User updateAuthor = update.getAuthor();
        if (updateAuthor != null && !updateAuthor.getId().equals(userId)) {
            String message = user.getName() + " commented on your update: " + truncate(commentContent, 60) + "";
            notificationService.notifyUser(buildNotification(
                    String.valueOf(updateAuthor.getId()), NotificationType.NEW_COMMENT, message));
        }
    }

    public void like(int updateId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Update update = updateRepository.findById(updateId).orElseThrow();
        ActionCommand actionCommand = new LikeUpdateCommand(update, user);
        actionCommand.execute();
        updateRepository.save(update);

        User updateAuthor = update.getAuthor();
        if (updateAuthor != null && !updateAuthor.getId().equals(userId)) {
            String message = user.getName() + " liked your update: " + truncate(update.getContent(), 60);
            notificationService.notifyUser(buildNotification(String.valueOf(
                    updateAuthor.getId()), NotificationType.LIKE, message));
        }
    }

    private List<User> getOtherParticipants(Initiative initiative, Long excludeUserId) {
        return initiative.getParticipants().stream().filter(p -> !p.getId().equals(excludeUserId)).toList();
    }

    private Notification buildNotification(String userId, NotificationType type, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRead(false);
        return notification;
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max) + "…";
    }
}