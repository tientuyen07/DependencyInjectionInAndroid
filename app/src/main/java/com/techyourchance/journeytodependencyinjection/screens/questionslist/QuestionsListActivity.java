package com.techyourchance.journeytodependencyinjection.screens.questionslist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import com.techyourchance.journeytodependencyinjection.questions.FetchQuestionsListUseCase;
import com.techyourchance.journeytodependencyinjection.questions.Question;
import com.techyourchance.journeytodependencyinjection.screens.common.dialogs.DialogsManager;
import com.techyourchance.journeytodependencyinjection.screens.common.dialogs.ServerErrorDialogFragment;
import com.techyourchance.journeytodependencyinjection.screens.questiondetails.QuestionDetailsActivity;

import java.util.List;

public class QuestionsListActivity extends AppCompatActivity implements
        QuestionsListViewMvc.Listener, FetchQuestionsListUseCase.Listener {

    private static final int NUM_OF_QUESTIONS_TO_FETCH = 20;
    private final String TAG = getLocalClassName();

    private FetchQuestionsListUseCase mFetchQuestionsListUseCase;

    private QuestionsListViewMvc mViewMvc;

    private DialogsManager mDialogsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewMvc = new QuestionsListViewMvcImpl(LayoutInflater.from(this), null);

        setContentView(mViewMvc.getRootView());

        mFetchQuestionsListUseCase = new FetchQuestionsListUseCase();

        mDialogsManager = new DialogsManager(getSupportFragmentManager());

    }

    @Override
    protected void onStart() {
        super.onStart();

        mViewMvc.registerListener(this);

        mFetchQuestionsListUseCase.registerListener(this);
        Log.d(TAG, "onStart: register QuestionListActivity with Listener");

        mFetchQuestionsListUseCase.fetchLastActiveQuestionsAndNotify(NUM_OF_QUESTIONS_TO_FETCH);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewMvc.unregisterListener(this);
        mFetchQuestionsListUseCase.unregisterListener(this);
    }

    /**
     * FetchQuestionsListUseCase.java
     * -> fetchLastActiveQuestionsAndNotify() -> notifySucceeded()
     * QuestionsListActivity.java
     * -> onFetchOfQuestionsSucceeded()
     **/
    // be called after get QuestionsListActivity from getListeners() method.
    @Override
    public void onFetchOfQuestionsSucceeded(List<Question> questions) {
        mViewMvc.bindQuestions(questions);
    }

    @Override
    public void onFetchOfQuestionsFailed() {
        mDialogsManager.showRetainedDialogWithId(ServerErrorDialogFragment.newInstance(), "");
    }

    @Override
    public void onQuestionClicked(Question question) {
        QuestionDetailsActivity.start(QuestionsListActivity.this, question.getId());
    }
}
