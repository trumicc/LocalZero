package com.localzero.localzero.command;

import com.localzero.localzero.model.Comment;
import com.localzero.localzero.model.Update;

public class CommentUpdateCommand implements ActionCommand {
    private Update update;
    private Comment comment;
    public CommentUpdateCommand(Update update, Comment comment) {
        this.update = update;
        this.comment = comment;
    }
    @Override
    public void execute() {
        update.getComments().add(comment);
    }
}
