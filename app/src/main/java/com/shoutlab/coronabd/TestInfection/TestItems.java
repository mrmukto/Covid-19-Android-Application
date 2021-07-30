package com.shoutlab.coronabd.TestInfection;

public class TestItems {
    private String id, question, image;

    public TestItems(String id, String question, String image) {
        this.id = id;
        this.question = question;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
