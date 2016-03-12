package com.aldawoudy.fallingwords;

import android.content.Context;

/**
 * Created by Ismail on 3/12/16.
 */
public interface FallingWordsView {

    void startGame();

    void pauseGame(boolean showRestart);

    void stopGame();

    Context getContext();

    void showLoading();

    void hideLoading();

    void hideStart();

    void resetScore();

    void setWord(String word);

    void updateTimer(String countDown, boolean alert);

    void setTranslation(String translation, long duration);

    void updateScore(String score);

    void showGameWon(int wordCount, int correctAnswerCount, int wrongAnswerCount, int noAnswerCount);

    void showGameOver(int wordCount, int correctAnswerCount, int wrongAnswerCount, int noAnswerCount);
}
