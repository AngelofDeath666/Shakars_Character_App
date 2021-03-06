package com.example.shakars_character_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Function;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewCharacter_activity extends AppCompatActivity implements View.OnClickListener {
    Button save;
    ImageButton settings;
    TextView title;
    EditText[][] editText;
    String documentID;
    LottieAnimationView loading;

    private static final String TAG = "NewCharacterActivity";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_character);

        title = findViewById(R.id.newCharacterTitle);

        save = findViewById(R.id.newCharacterSaveButton);
        save.setOnClickListener(this);

        settings = findViewById(R.id.newCharacterOverlayButton);
        //        settings.setOnClickListener(this);
        settings.setVisibility(View.INVISIBLE);

        loading = findViewById(R.id.newCharacterLoadAnimation);
        loading.setVisibility(View.INVISIBLE);



        ViewGroup content = findViewById(R.id.newCharacterLayout);
        content.removeAllViews();


        editText = new EditText[CategoriesAndProperties.dataPC.categoriesPC.length][];

        for (int catIndex = 0; catIndex < CategoriesAndProperties.dataPC.categoriesPC.length; catIndex++) {
            View catRoot = getLayoutInflater().inflate(R.layout.category_title, content, false);
            LinearLayout cat_title_ll = catRoot.findViewById(R.id.cat_title_ll);
            cat_title_ll.removeAllViews();
            TextView catTitleTv = catRoot.findViewById(R.id.categoriesTV);
            catTitleTv.setText(CategoriesAndProperties.dataPC.categoriesPC[catIndex]);
            catTitleTv.setOnClickListener(this);
            catTitleTv.setTag(catIndex);
            content.addView(catRoot);
            categoriesLL.add(cat_title_ll);
            cat_title_ll.setVisibility(CategoriesAndProperties.foldedPC[catIndex] ? View.GONE : View.VISIBLE);

            int[] props = CategoriesAndProperties.dataPC.propertiesPC[catIndex];
            editText[catIndex] = new EditText[props.length];
            int[] hints = CategoriesAndProperties.dataPC.hintsPC[catIndex];
            for (int propIndex = 0; propIndex < props.length; propIndex++) {

                View root = getLayoutInflater().inflate(R.layout.category_property, content, false);
                TextView titleTV = root.findViewById(R.id.propertiesTV);
                titleTV.setText(props[propIndex]);



                int hintIndex = propIndex;
                if (hintIndex >= hints.length) {
                    System.err.println("Missing hint for "+catIndex+", "+propIndex);
                    hintIndex = 0;
                }
                EditText hintTV = root.findViewById(R.id.hints);
                editText[catIndex][propIndex] = hintTV;
                hintTV.setHint(hints[hintIndex]);
                hintTV.setTag(hintIndex);

                cat_title_ll.addView(root);
            }

        }

    }


    @Override
    public void onClick (final View v){
        if (save.getId() == v.getId()) {
            v.startAnimation(buttonClick);
            sendCharacter();
            if (TextUtils.equals(documentID,"")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("End character creation.");
                builder.setMessage("You are about to end character creation.\nThe character cannot be saved without a name.\nYou will be sent back to the front page.\nAre you sure you want to finish?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(NewCharacter_activity.this, AllCharacters_activity.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            } else {
                loading.playAnimation();
                loading.setVisibility(View.VISIBLE);
                ViewGroup content = findViewById(R.id.newCharacterLayout);
                content.setVisibility(View.INVISIBLE);

                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(v.getContext(), ViewCharacter_activity.class);
                        intent.putExtra("DocumentID", documentID);
                        v.getContext().startActivity(intent);

                    }
                }.start();




            }

        }



        Object tag = v.getTag();
        if (tag != null) {
            int catIndex = (int) tag;
            CategoriesAndProperties.foldedPC[catIndex] = !CategoriesAndProperties.foldedPC[catIndex];

            View cat = categoriesLL.get(catIndex);
            if (CategoriesAndProperties.foldedPC[catIndex]) {
                cat.animate().scaleY(0);

            } else {
                cat.setVisibility(View.VISIBLE);
                cat.animate().scaleY(1);

            }

        }



    }

    ArrayList<View> categoriesLL = new ArrayList<>();

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End character creation.");
        builder.setMessage("You are about to end character creation.\nYou will be sent back to the front page.\nAre you sure you want to finish?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(NewCharacter_activity.this, AllCharacters_activity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void sendCharacter() {
        //Basic
        String name = editText[0][0].getText().toString();
        String title = editText[0][1].getText().toString();
        String race = editText[0][2].getText().toString();
        String gender = editText[0][3].getText().toString();
        String age = editText[0][4].getText().toString();
        String nationality = editText[0][5].getText().toString();
        String hometown = editText[0][6].getText().toString();
        String continent = editText[0][7].getText().toString();
        String rpClass = editText[0][8].getText().toString();
        String sexuality = editText[0][9].getText().toString();
        String attracted = editText[0][10].getText().toString();
        String occupation = editText[0][11].getText().toString();
        String religion = editText[0][12].getText().toString();
        //Looks
        String eyes = editText[1][0].getText().toString();
        String skin = editText[1][1].getText().toString();
        String hair = editText[1][2].getText().toString();
        String scent = editText[1][3].getText().toString();
        String voice = editText[1][4].getText().toString();
        String features = editText[1][5].getText().toString();
        //Personality
        String personality = editText[2][0].getText().toString();
        String behaviour = editText[2][1].getText().toString();
        String traits = editText[2][2].getText().toString();
        String flaws = editText[2][3].getText().toString();
        String quirks = editText[2][4].getText().toString();
        String hobbies = editText[2][5].getText().toString();
        String strengths = editText[2][6].getText().toString();
        String wants = editText[2][7].getText().toString();
        String fears = editText[2][8].getText().toString();
        String secrets = editText[2][9].getText().toString();
        //Relations
        String family = editText[3][0].getText().toString();
        String allies = editText[3][1].getText().toString();
        String enemies = editText[3][2].getText().toString();
        String groups = editText[3][3].getText().toString();
        //Goals and BG
        String goals = editText[4][0].getText().toString();
        String backGround = editText[4][1].getText().toString();

        documentID = name;

        Map<String, Object> character = new HashMap<>();
        character.put("Name", name);
        character.put("TitleKenningMoniker", title);
        character.put("Race", race);
        character.put("Gender", gender);
        character.put("Age", age);
        character.put("Nationality", nationality);
        character.put("Hometown", hometown);
        character.put("Continent", continent);
        character.put("Class", rpClass);
        character.put("Sexuality", sexuality);
        character.put("TraitsAttractedTo", attracted);
        character.put("Occupation", occupation);
        character.put("Religion", religion);
        character.put("Eyes", eyes);
        character.put("Skin", skin);
        character.put("Hair", hair);
        character.put("Scent", scent);
        character.put("Voice", voice);
        character.put("CharacteristicFeatures", features);
        character.put("Personality", personality);
        character.put("BehaviourAndManner", behaviour);
        character.put("CharacterTraits", traits);
        character.put("CharacterFlaws",flaws);
        character.put("Quirks", quirks);
        character.put("Hobbies", hobbies);
        character.put("StrengthsAndWeaknesses", strengths);
        character.put("WantsAndDesires", wants);
        character.put("FearsAndInsecurities", fears);
        character.put("Secrets", secrets);
        character.put("Family", family);
        character.put("AlliesAndContacts", allies);
        character.put("Enemies", enemies);
        character.put("AffiliatedGroups", groups);
        character.put("CurrentGoals", goals);
        character.put("BackgroundStory", backGround);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (TextUtils.isEmpty(name)) {
            editText[0][0].setError("This cannot be empty");

        }

         else {

            DocumentReference newCharacterRef = db.collection("users").document("test")
                    .collection("characters").document(name);


            newCharacterRef.set(character).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }





    }




}
