package com.g00fy2.lolquiz.sqlite;

import java.util.Random;

/**
 * Created by thoma on 13.11.2016.
 */

public class QuestionAnswerSet {
    private Answer[] answers = new Answer[4];
    private int counter;
    private boolean min;
    private double questionValue;
    private String category = "";

    public int getCounter() {
        return counter;
    }

    public double getQuestionValue() {
        return questionValue;
    }

    public boolean isMin() {
        return min;
    }

    public void setMin(boolean min) {
        this.min = min;
    }

    public void setQuestionValue(Double questionValue) {
        this.questionValue = questionValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Answer[] getAnswers() {
        return answers;
    }

    public void addAnswer(String answerText, double answerValue, String imageUrl) {
        // check if answer and imageUrl arent empty (value can be 0)
        if (answerText != null && !answerText.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
            Answer answerObj = new Answer();
            answerObj.answerText = answerText;
            answerObj.answerValue = answerValue;
            answerObj.imageUrl = imageUrl;

            if (counter < answers.length) {
                answers[counter] = answerObj;
                counter++;

                // if last answer was added randomly sort answers array
                if (counter == 4) {
                    int n = answers.length;
                    Random random = new Random();
                    // loop through array and randomly swap the entry
                    for (int i = 0; i < n; i++) {
                        int r = random.nextInt(n);
                        Answer tmp = answers[i];
                        answers[i] = answers[r];
                        answers[r] = tmp;
                    }
                }
            }
        }
    }

    public class Answer {
        public String answerText = "";
        public double answerValue;
        public String imageUrl = "";
    }

}
