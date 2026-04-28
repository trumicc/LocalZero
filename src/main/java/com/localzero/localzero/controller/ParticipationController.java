package com.localzero.localzero.controller;

import com.localzero.localzero.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participation")
public class ParticipationController {
    @Autowired
    private ParticipationService participationService;
    @PostMapping("/join")
    public void join(@RequestParam int initiativeId, @RequestParam int userId) {
        participationService.join(userId, initiativeId);
    }
    @PostMapping("/postUpdate")
    public void update(@RequestParam int initiativeId,@RequestParam String content, @RequestParam int userId) {
        participationService.postUpdate(userId,content,initiativeId );
    }
    @PostMapping("/comment")
    public void comment(@RequestParam int updateId, String content, @RequestParam int userId) {
        participationService.comment(content,updateId,userId);
    }
    @PostMapping("/like")
    public void like(@RequestParam int updateId, @RequestParam int userId) {
        participationService.like(updateId,userId);
    }
}
