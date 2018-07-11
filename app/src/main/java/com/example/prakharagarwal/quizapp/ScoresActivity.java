package com.example.prakharagarwal.quizapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    TextView score;
    List<Score> scoreList=new ArrayList<>();
    LinearLayout scoresContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        score=findViewById(R.id.score);
        scoresContainer=findViewById(R.id.scores_container);
//        score.setText("Your Score is : "+getIntent().getIntExtra("Score",0)+"");
        getScores();
    }

    private void getScores() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("scores");
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
        for (DataSnapshot child : dataSnapshot.getChildren()) {


            Score score = child.getValue(Score.class);
            scoreList.add(score);
        }
        updateScores();
    }

    private void updateScores() {
        for (int i=0;i<scoreList.size();i++){
            View view= LayoutInflater.from(this).inflate(R.layout.score_item,null);
            TextView score=view.findViewById(R.id.score_text);
            score.setText(scoreList.get(i).score);
            TextView date=view.findViewById(R.id.score_date);
            date.setText(scoreList.get(i).date + " "+ scoreList.get(i).time);

            scoresContainer.addView(view);
        }
    }

    public static class Score{
        String date;
        String score;
        String time;
    }
}
