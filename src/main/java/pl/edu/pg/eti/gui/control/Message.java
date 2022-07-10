package pl.edu.pg.eti.gui.control;

import io.vavr.control.Either;

import java.io.File;

public class Message {

    final String author;
    final Either<String, File> content;

    public Message(String author, String content) {
        this.author = author;
        this.content = Either.left(content);
    }

    public Message(String author, File file) {
        this.author = author;
        this.content = Either.right(file);
    }

    public Either<String, File> getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }
}
