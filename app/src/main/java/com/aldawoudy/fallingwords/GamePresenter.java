package com.aldawoudy.fallingwords;

import com.aldawoudy.fallingwords.utlis.JsonConvertible;
import com.aldawoudy.fallingwords.utlis.RawDataReader;
import com.aldawoudy.fallingwords.utlis.SimpleSubscriber;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Ismail on 3/12/16.
 */
public class GamePresenter extends BasePresenter<FallingWordsView> {

    private static final String TAG = "GamePresenter";

    private static final int LOSING_SCORE = -50;

    private static final int WINING_SCORE = 100;

    private static final int CORRECT_ANSWER_SCORE = 10;

    private static final int INCORRECT_ANSWER_SCORE = -10;

    private static final int NO_ANSWER_SCORE = -5;

    private static final int ANIMATION_DURATION = 5000;

    private static final int TIMER_INTERVAL = 100;

    private static final int TIMER_COUNT = ANIMATION_DURATION / TIMER_INTERVAL;

    private ArrayList<Word> mWords;

    private int mScore;

    private int mWordCount;

    private int mCorrectAnswerCount;

    private int mWrongAnswerCount;

    private int mNoAnswerCount;

    private String mCurrentTranslation;

    private Word mCurrentWord;

    private Subscription mWordTimer;

    private boolean mGameStarted = false;

    public GamePresenter(FallingWordsView view) {
        attachView(view);
        initGame();
        resetGame();
        updateScore();
    }

    private void resetGame() {
        mScore = 0;
        mWordCount = 0;
        mCorrectAnswerCount = 0;
        mWrongAnswerCount = 0;
        mNoAnswerCount = 0;
    }

    public void initGame() {
        loadWords(new LoadWordsListener() {
            @Override
            public void onWordsLoaded(ArrayList<Word> words) {
                mWords = words;
            }

            @Override
            public void onWordsLoadingFailed(Throwable e) {

            }
        });
    }

    public void loadWords(final LoadWordsListener listener) {
        RawDataReader rawDataReader = new RawDataReader(getView().getContext());
        rawDataReader.readString(R.raw.words)
        .subscribe(new SimpleSubscriber<String>() {
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                listener.onWordsLoadingFailed(e);
            }

            @Override
            public void onNext(String s) {
                parseWords(s, listener);
            }
        });
    }

    private void parseWords(final String json, final LoadWordsListener listener) {
        Observable.defer(new Func0<Observable<ArrayList<Word>>>() {
            @Override
            public Observable<ArrayList<Word>> call() {
                return Observable.just(parseWordsBlocking(json));
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SimpleSubscriber<ArrayList<Word>>() {
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                listener.onWordsLoadingFailed(e);
            }

            @Override
            public void onNext(ArrayList<Word> words) {
                listener.onWordsLoaded(words);
            }
        });
    }

    private ArrayList<Word> parseWordsBlocking(String json) {
        ArrayList<Word> words = JsonConvertible.fromJsonString(json.getBytes(), new
                TypeReference<ArrayList<Word>>() {});
        return words != null ? words : new ArrayList<Word>();
    }

    public void onCorrectClick() {
        boolean isCorrect = mCurrentWord.getSpanish().equals(mCurrentTranslation);
        updateAnswerCount(isCorrect);
        onAnswer(getScore(isCorrect));
    }

    public void onWrongClick() {
        boolean isCorrect = !mCurrentWord.getSpanish().equals(mCurrentTranslation);
        updateAnswerCount(isCorrect);
        onAnswer(getScore(isCorrect));
    }

    private void updateAnswerCount(boolean isCorrect) {
        if (isCorrect) {
            mCorrectAnswerCount++;
        } else {
            mWrongAnswerCount++;
        }
    }

    private int getScore(boolean isCorrect) {
        return isCorrect ? CORRECT_ANSWER_SCORE : INCORRECT_ANSWER_SCORE;
    }

    private void onAnswer(int score) {
        mWordTimer.unsubscribe();
        mScore += score;
        if (!gameEnded()) {
            updateScore();
            displayNextWord(emitWord());
        } else {
            displayResult();
        }
    }

    private void displayResult() {
        if (mScore <= LOSING_SCORE) {
            getView().showGameOver(mWordCount, mCorrectAnswerCount, mWrongAnswerCount, mNoAnswerCount);
        } else {
            getView().showGameWon(mWordCount, mCorrectAnswerCount, mWrongAnswerCount, mNoAnswerCount);
        }
    }

    private void updateScore() {
        getView().updateScore(String.valueOf(mScore));
    }

    private void onNoAnswer() {
        mNoAnswerCount++;
        onAnswer(NO_ANSWER_SCORE);
    }

    private boolean gameEnded() {
        return mWordCount == mWords.size() || mScore <= LOSING_SCORE || mScore >= WINING_SCORE;
    }


    public void onPauseClick() {
        if (mWordTimer != null) {
            mWordTimer.unsubscribe();
        }
        getView().pauseGame(mGameStarted);
    }

    public void onStartClick() {
        if (mWords == null) {
            getView().showLoading();
            getView().hideStart();
            loadWords(new LoadWordsListener() {
                @Override
                public void onWordsLoaded(ArrayList<Word> words) {
                    mWords = words;
                    startGame();
                }

                @Override
                public void onWordsLoadingFailed(Throwable e) {

                }
            });
        } else {
            startGame();
        }
    }

    private void startGame() {
        mGameStarted = true;
        if (getView() != null) {
            getView().hideLoading();
            getView().resetScore();
            displayNextWord(emitWord());
            getView().startGame();
        }
    }

    private void displayNextWord(Word word) {
        mWordCount++;
        mCurrentWord = word;
        mCurrentTranslation = getRandomTranslation(word);
        getView().setWord(word.getEnglish());
        getView().setTranslation(mCurrentTranslation, ANIMATION_DURATION);

        mWordTimer = Observable.interval(TIMER_INTERVAL, TimeUnit.MILLISECONDS)
                .take(TIMER_COUNT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        onNoAnswer();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        float countDown = 5f -  ((float) aLong.intValue())/10;
                        boolean alert = countDown <= 2;
                        getView().updateTimer(String.format(Locale.getDefault(), "%.2f",
                                countDown), alert);
                    }
                });
    }

    private String getRandomTranslation(Word current) {
        Random r = new Random();
        return r.nextBoolean() ? current.getSpanish() :
                mWords.get(r.nextInt(mWords.size())).getSpanish();
    }

    private Word emitWord() {
        Random r = new Random();
        return mWords.get(r.nextInt(mWords.size()));
    }

    public void onRestartClick() {
        resetGame();
        updateScore();
        startGame();
    }


    public interface LoadWordsListener {

        void onWordsLoaded(ArrayList<Word> words);

        void onWordsLoadingFailed(Throwable e);
    }
}
