package com.localzero.localzero.command;

import com.localzero.localzero.model.Update;
import com.localzero.localzero.model.User;

public class LikeUpdateCommand implements ActionCommand {
    private Update update;
    private User user;

    @Override
    public void execute() {
        update.getLikes().add(user);
    }
}
