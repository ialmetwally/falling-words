package com.aldawoudy.fallingwords;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FallingWordsActivity extends AppCompatActivity implements FallingWordsView {

    private static final String TAG = "FallingWordsActivity";

    @Bind(R.id.word)
    TextView mWordView;

    @Bind(R.id.translation)
    TextView mTranslationView;

    @Bind(R.id.score)
    TextView mScoreView;

    @Bind(R.id.container)
    View mContainerView;

    @Bind(R.id.loading)
    View mLoadingView;

    @Bind(R.id.start)
    View mStartView;

    @Bind(R.id.restart)
    View mRestartView;

    @Bind(R.id.top_controls)
    View mTopControls;

    @Bind(R.id.bottom_controls)
    View mBottomControls;

    @Bind(R.id.countdown)
    TextView mCountDownView;

    @Bind(R.id.game_ended)
    View mGameEndedView;

    @Bind(R.id.words_count)
    TextView mWordCountView;

    @Bind(R.id.correct_answers_count)
    TextView mCorrectCountView;

    @Bind(R.id.incorrect_answers_count)
    TextView mWrongCountView;

    @Bind(R.id.no_answers_count)
    TextView mNoAnswerCountView;

    @Bind(R.id.game_end_message)
    TextView mGameEndedMessage;

    @OnClick(R.id.pause)
    void onPauseClick() {
        mGamePresenter.onPauseClick();
    }

    @OnClick(R.id.button_correct)
    void onCorrectClick() {
        mGamePresenter.onCorrectClick();
        Log.d(TAG, "onCorrectClick");
    }

    @OnClick(R.id.button_wrong)
    void onWrongClick() {
        mGamePresenter.onWrongClick();
        Log.d(TAG, "onWrongClick");
    }

    @OnClick(R.id.start)
    void onStartClick() {
        mGamePresenter.onStartClick();
    }

    @OnClick(R.id.restart)
    void onRestartClick() {
        mGamePresenter.onRestartClick();
    }


    @OnClick(R.id.start_over)
    void onStartOVerClick() {
        mGamePresenter.onRestartClick();
    }

    private GamePresenter mGamePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_panel);
        ButterKnife.bind(this);
        mGamePresenter = new GamePresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mGamePresenter.detachView();
        mGamePresenter = null;
    }

    @Override
    public void startGame() {
        mGameEndedView.setVisibility(View.GONE);
        mStartView.setVisibility(View.GONE);
        mRestartView.setVisibility(View.GONE);
        mWordView.setVisibility(View.VISIBLE);
        mTopControls.setVisibility(View.VISIBLE);
        mBottomControls.setVisibility(View.VISIBLE);
    }

    @Override
    public void pauseGame(boolean showRestart) {
        mWordView.setVisibility(View.GONE);
        mTranslationView.clearAnimation();
        mTranslationView.setVisibility(View.GONE);
        mTopControls.setVisibility(View.GONE);
        mBottomControls.setVisibility(View.GONE);
        mStartView.setVisibility(View.VISIBLE);
        if (showRestart) {
            mRestartView.setVisibility(View.VISIBLE);
        } else {
            mRestartView.setVisibility(View.GONE);
        }
    }

    @Override
    public void stopGame() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void hideStart() {
        mStartView.setVisibility(View.GONE);
    }

    @Override
    public void resetScore() {

    }

    @Override
    public void setWord(String word) {
        mWordView.setText(word);
    }

    @Override
    public void updateTimer(String countDown, boolean alert) {
        mCountDownView.setText(countDown);
        if (alert) {
            mCountDownView.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else {
            mCountDownView.setTextColor(ContextCompat.getColor(this, R.color.light_blue));
        }
    }

    @Override
    public void setTranslation(String translation, long duration) {
        mTranslationView.setText(translation);
        TranslateAnimation animate = new TranslateAnimation(0,0,0,mContainerView.getHeight());
        animate.setDuration(duration);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTranslationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTranslationView.setVisibility(View.GONE);
                mTranslationView.setY(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mTranslationView.startAnimation(animate);
    }

    @Override
    public void updateScore(String score) {
        mScoreView.setText(score);
    }

    @Override
    public void showGameWon(int wordCount, int correctAnswerCount, int wrongAnswerCount,
                            int noAnswerCount) {
        mGameEndedMessage.setText(R.string.GAME_WON_MSG);
        mGameEndedMessage.setTextColor(ContextCompat.getColor(this, R.color.green));
        updateGameResults(wordCount, correctAnswerCount, wrongAnswerCount, noAnswerCount);
        mGameEndedView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGameOver(int wordCount, int correctAnswerCount, int wrongAnswerCount,
                             int noAnswerCount) {
        mGameEndedMessage.setText(R.string.GAME_OVER_MSG);
        mGameEndedMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
        updateGameResults(wordCount, correctAnswerCount, wrongAnswerCount, noAnswerCount);
        mGameEndedView.setVisibility(View.VISIBLE);
    }

    private void updateGameResults(int wordCount, int correctAnswerCount, int wrongAnswerCount,
                                   int noAnswerCount) {
        mWordCountView.setText(String.valueOf(wordCount));
        mCorrectCountView.setText(String.valueOf(correctAnswerCount));
        mWrongCountView.setText(String.valueOf(wrongAnswerCount));
        mNoAnswerCountView.setText(String.valueOf(noAnswerCount));
    }
}
