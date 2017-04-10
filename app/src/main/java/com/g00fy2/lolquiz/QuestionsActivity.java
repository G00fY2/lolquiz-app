package com.g00fy2.lolquiz;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.sqlite.ChampionsDataSource;
import com.g00fy2.lolquiz.sqlite.QuestionAnswerSet;
import com.g00fy2.lolquiz.util.CircleImgage;
import com.g00fy2.lolquiz.util.ProgressBarAnimation;
import com.squareup.picasso.Picasso;

public class QuestionsActivity extends AppCompatActivity {

    private int quizRound = 1;
    private int rightAnswers;
    private ProgressBar roundProgress;
    private ProgressBar countdownBar;
    private ProgressBarAnimation countdownAnimation;
    private Handler countdownHandler;
    private Runnable countdownRunnable;
    private QuestionAnswerSet qaSet;
    private CardView[] answerCardViews;
    private ImageView[] answerImages;
    private TextView[] answerTexts;
    private int colorRight;
    private int colorWrong;
    private int cardsDefault;
    private boolean answerSet;
    private ChampionsDataSource opendata;
    private AlphaAnimation blinkAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_page);

        // Set Toolbar and Titel
        Toolbar toolbar = (Toolbar) findViewById(R.id.question_toolbar);
        // Set the Toolbar to act as the ActionBar for this activity
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Round 1");
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // Set round ProgressBars
        roundProgress = (ProgressBar) findViewById(R.id.question_progressbar);
        Drawable progressbarRounds = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_progressbar);
        roundProgress.setProgressDrawable(progressbarRounds);
        roundProgress.setProgress(1000);

        // Setting up countdown ProgressBar
        countdownBar = (ProgressBar) findViewById(R.id.question_countdown);
        Drawable progressbarTimer = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_progressbar);
        countdownBar.setProgressDrawable(progressbarTimer);

        colorRight = ContextCompat.getColor(getApplicationContext(), R.color.RightAnswer);
        colorWrong = ContextCompat.getColor(getApplicationContext(), R.color.WrongAnswer);
        cardsDefault = ContextCompat.getColor(getApplicationContext(), R.color.CardDefault);

        // getting the runtime IDs from the views
        getViewIDs();
        // create blinking simple_grow
        setBlinkAnimation();

        // sets up the handler and simple_grow for the countdown
        initCountdown(10000, 500);

        // call new round function to set up question and answers
        setNewRound();
    }

    public void submit_answer(View view) {
        int cardNumber = 0;
        // get the number (0-3) from the card which was clicked
        for (int i = 0; i < answerCardViews.length; i++) {
            if (view.getId() == answerCardViews[i].getId()) {
                cardNumber = i;
                break;
            }
        }

        if (!answerSet) {
            answerSet = true;
            countdownBar.clearAnimation();
            if (qaSet.getQuestionValue() == qaSet.getAnswers()[cardNumber].answerValue) {
                rightAnswers++;
                colorCard(cardNumber, true);
            } else {
                colorCard(cardNumber, false);
                colorRightAnswer();
            }
            // Execute some code after 2 seconds have passed
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setNewRound();
                }
            }, 2000);
        }
    }

    private void setNewRound() {
        if (quizRound <= 10) {
            answerSet = false;
            qaSet = null;

            if (quizRound == 1) {
                // initialize new DataSource class
                opendata = new ChampionsDataSource(getApplicationContext());
            }
            else {
                getSupportActionBar().setTitle("Round " + Integer.toString(quizRound));

                //simple_grow for round progressbar (1-10) multiplied by 1000 for smoother simple_grow
                ProgressBarAnimation anim = new ProgressBarAnimation(roundProgress, (quizRound - 1) * 1000, quizRound * 1000);
                anim.setDuration(500);
                roundProgress.startAnimation(anim);
            }

            // generates new QASet
            opendata.openReadable();
            try {
                qaSet = opendata.getRandomQASet();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            opendata.close();

            if (qaSet != null) {
                // Set question text in toolbar fragment
                String question = qaSet.isMin() ? "min" : "max";
                question = question + " " + qaSet.getCategory();
                TextView questionText = (TextView) findViewById(R.id.question);
                questionText.setText(question);

                for (int i = 0; i < 4; i++) {
                    Picasso.with(this).load((qaSet.getAnswers()[i].imageUrl)).transform(new CircleImgage()).into(answerImages[i]);
                    answerCardViews[i].clearAnimation();
                    answerCardViews[i].setCardBackgroundColor(cardsDefault);
                    answerTexts[i].setText(qaSet.getAnswers()[i].answerText);
                }
                startCountdown();
            }
            quizRound++;
        } else {
            Builder builder = new Builder(this)
                    .setTitle("Game finished!")
                    .setMessage(Integer.toString(rightAnswers) + " from 10 answers were right!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            QuestionsActivity.super.onBackPressed();
                        }
                    });
            AlertDialog dialog = builder.show();
            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
        }
    }

    // Set values and ImageViews of anser cards
    // reset colors
    private void getViewIDs() {
        answerCardViews = new CardView[4];
        answerCardViews[0] = (CardView) findViewById(R.id.card_answer1);
        answerCardViews[1] = (CardView) findViewById(R.id.card_answer2);
        answerCardViews[2] = (CardView) findViewById(R.id.card_answer3);
        answerCardViews[3] = (CardView) findViewById(R.id.card_answer4);

        answerImages = new ImageView[4];
        answerImages[0] = (ImageView) findViewById(R.id.answer1_image);
        answerImages[1] = (ImageView) findViewById(R.id.answer2_image);
        answerImages[2] = (ImageView) findViewById(R.id.answer3_image);
        answerImages[3] = (ImageView) findViewById(R.id.answer4_image);

        answerTexts = new TextView[4];
        answerTexts[0] = (TextView) findViewById(R.id.answer1_text);
        answerTexts[1] = (TextView) findViewById(R.id.answer2_text);
        answerTexts[2] = (TextView) findViewById(R.id.answer3_text);
        answerTexts[3] = (TextView) findViewById(R.id.answer4_text);

    }

    // creates the countdownBar Animation and countdown Handler and Runnable
    // periot in milliseconds, intervall time the Runnable gets called
    private void initCountdown(final int period, final int interval) {
        // creates countdown progressbar simple_grow with duration
        countdownAnimation = new ProgressBarAnimation(countdownBar, 10000, 0);
        countdownAnimation.setDuration(period);

        // initalizie the handler
        countdownHandler = new Handler();

        // creates runnable for the countdown handler
        countdownRunnable = new Runnable() {
            int count;

            @Override
            public void run() {
                // if no answer set
                if (!answerSet) {
                    count += interval;
                    // if duration is reached
                    if (count == period) {
                        count = 0; // reset counter for next run
                        answerSet = true;
                        //TODO: proper countdown end handling
                        colorRightAnswer();
                        Toast.makeText(getApplicationContext(), "Countdown end!", Toast.LENGTH_SHORT).show();
                        // Execute some code after 2 seconds have passed
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setNewRound();
                            }
                        }, 2000);
                    } else {
                        countdownHandler.postDelayed(this, interval);
                    }
                } else {
                    count = 0; // reset counter for next run
                }
            }
        };
    }

    private void startCountdown() {
        if (countdownAnimation != null && countdownRunnable != null) {
            countdownBar.startAnimation(countdownAnimation);
            countdownHandler.removeCallbacks(countdownRunnable);
            countdownHandler.post(countdownRunnable);
        }
    }

    private void colorRightAnswer() {
        double rightValue = qaSet.getQuestionValue();

        for (int i = 0; i < qaSet.getAnswers().length; i++) {
            if (rightValue == qaSet.getAnswers()[i].answerValue) {
                colorCard(i, true, true);
                break;
            }
        }
    }

    private void colorCard(int answer, boolean right) {
        colorCard(answer, right, false);
    }

    private void colorCard(int answer, boolean right, boolean animation) {
        int color = right ? colorRight : colorWrong;

        answerCardViews[answer].setCardBackgroundColor(color);
        if (animation) {
            answerCardViews[answer].startAnimation(blinkAnimation);
        }
    }

    private void setBlinkAnimation() {
        blinkAnimation = new AlphaAnimation(1, 0);
        blinkAnimation.setDuration(750);
        blinkAnimation.setInterpolator(new LinearInterpolator());
        blinkAnimation.setRepeatCount(2);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new Builder(this)
                .setMessage(R.string.EndGameDialog)
                .setPositiveButton(R.string.EndGameDialogButton, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countdownHandler.removeCallbacks(countdownRunnable);
                        QuestionsActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.DialogCancel, null)
                .show();
    }
}
