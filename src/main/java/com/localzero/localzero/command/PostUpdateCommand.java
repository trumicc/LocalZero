package com.localzero.localzero.command;

import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.model.Update;
import com.localzero.localzero.model.User;

public class PostUpdateCommand implements ActionCommand {
    private Initiative initiative;
    private Update update;

    public PostUpdateCommand(Initiative initiative, Update update) {
        this.initiative = initiative;
        this.update = update;
    }
    @Override
    public void execute() {
        initiative.getUpdates().add(update);
    }
}
