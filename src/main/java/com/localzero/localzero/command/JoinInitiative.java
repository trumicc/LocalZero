package com.localzero.localzero.command;

import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.model.User;

public class JoinInitiative implements ActionCommand {

    private Initiative initiative;
    private User user;

    public JoinInitiative(Initiative initiative, User user) {
        this.initiative = initiative;
        this.user = user;
    }
    @Override
    public void execute() {

    }
}
