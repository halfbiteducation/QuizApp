package com.example.prakharagarwal.quizapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class QuizActivity extends AppCompatActivity {

    List<Quiz> quizList = new ArrayList<>();
    TextView timer;
    int time;
    Timer t;
    TimerTask task;
    TextView questionText;
    int quizPos = 0;
    RadioGroup radioGroup;
    RadioButton option_1;
    RadioButton option_2;
    RadioButton option_3;
    RadioButton option_4;
    String answer=null;
    int score = 0;
    Button nextBtn;
    Button endBtn;
    ProgressBar progressBar;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        timer = findViewById(R.id.timer);
        questionText = findViewById(R.id.ques_text);
        option_1 = findViewById(R.id.option_1);
        option_2 = findViewById(R.id.option_2);
        option_3 = findViewById(R.id.option_3);
        option_4 = findViewById(R.id.option_4);
        radioGroup=findViewById(R.id.options_group);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        container=findViewById(R.id.container);
        container.setVisibility(View.GONE);

        nextBtn=findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.clearCheck();
                t.cancel();
                if(answer!=null && quizList.get(quizPos).answer.equals(answer) )
                    score++;
                quizPos++;
                if(quizPos<quizList.size()){

                    startQuiz();
                }
                else {
                    endTest();
                }
            }
        });
        endBtn=findViewById(R.id.end);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTest();
            }
        });
//        startTimer();


        getQuestions();
    }

    private void startQuiz() {
        questionText.setText(quizList.get(quizPos).question);
        option_1.setText(quizList.get(quizPos).option_1);
        option_2.setText(quizList.get(quizPos).option_2);
        option_3.setText(quizList.get(quizPos).option_3);
        option_4.setText(quizList.get(quizPos).option_4);
        startTimer();
    }

    private void startTimer() {
        time = 20;
        t = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.setText(time + "");
                        if (time > 0) {
                            time--;

                        } else {
                            t.cancel();
                            if(answer!=null && quizList.get(quizPos).answer.equals(answer) )
                                score++;
                            quizPos++;
                            if (quizPos < quizList.size()) {
                                radioGroup.clearCheck();
                                startQuiz();
                            } else {
                                endTest();
                            }
                        }

                    }
                });
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000);

    }

    private void endTest() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("scores");

        Map<String,String> map=new HashMap<>();
        map.put("date","08/07");
        map.put("score",score+"");
        map.put("time","4:00");
        myRef.push().setValue(map);
        Intent intent= new Intent(QuizActivity.this,ScoresActivity.class);
        intent.putExtra("Score",score);
        startActivity(intent);
        finish();
    }

    private void getQuestions() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("quiz");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parseData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void parseData(DataSnapshot dataSnapshot) {
        String question;
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        for (DataSnapshot child : dataSnapshot.getChildren()) {

//            for(DataSnapshot child2 : child.getChildren()){
//                if(child2.getKey().equals("question"))
//                   question = (String) child2.getValue();
//
//            }
            Quiz quiz = child.getValue(Quiz.class);
            quizList.add(quiz);
        }
        startQuiz();

    }

    public void onRadioButtonClicked(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.option_1:
                answer = "option_1";
                break;
            case R.id.option_2:
                answer = "option_2";
                break;
            case R.id.option_3:
                answer = "option_3";
                break;
            case R.id.option_4:
                answer = "option_4";
                break;
        }

    }

    public static class Quiz {
        String question;
        String option_1;
        String option_2;
        String option_3;
        String option_4;
        String answer;

        public Quiz() {
        }

    }
}