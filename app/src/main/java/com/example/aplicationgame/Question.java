package com.example.aplicationgame;

import java.util.List;

public class Question {
    private String questionText;
    private String correctAnswer;
    private String[] options;
    private String explanation;
    private String detail; // Field baru untuk detail soal
    private List<String> hints;
    private int level;

    public Question(String questionText, String correctAnswer, String[] options,
                    String explanation, String detail, List<String> hints, int level) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.explanation = explanation;
        this.detail = detail;
        this.hints = hints;
        this.level = level;
    }

    public String getQuestionText() { return questionText; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String[] getOptions() { return options; }
    public String getExplanation() { return explanation; }
    public String getDetail() { return detail; }
    public List<String> getHints() { return hints; }

    public int getLevel() {
        return level;
    }

}