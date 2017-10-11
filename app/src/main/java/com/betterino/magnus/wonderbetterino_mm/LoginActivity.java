package com.betterino.magnus.wonderbetterino_mm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonLogin;
    private Button registerButton;
    private EditText emailText;
    private EditText passwordText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AlertDialog popup;
    final Context context = this;
//    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


//        this.user = SingletonApplications.user;
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                SingletonApplications.user = firebaseAuth.getCurrentUser();
                if (SingletonApplications.user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + SingletonApplications.user.getUid());

                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Layout
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener((View.OnClickListener) this);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener((View.OnClickListener) this);


        emailText = (EditText) findViewById(R.id.emailLogin);
        passwordText = (EditText) findViewById(R.id.passwordLogin);

        emailText.setText("test@mail.dk");
        passwordText.setText("123456");





    }

    public void onClick(View v) {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if(v == buttonLogin){
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            if (!email.contains("@")) {
                Snackbar.make(v, "Use a valid email address", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else if (password.length() < 6) {
                Snackbar.make(v, "Wrong password", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else if (email.matches("")) {
                Snackbar.make(v, "Use a valid email address", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
            }
            else if (password.matches("")) {
                Snackbar.make(v, "Use a valid password. Passwords must be at least 6 charracters long", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Toast.makeText(this, "You did not enter a password", Toast.LENGTH_SHORT).show();
            }
            else {
                login(email, password);
            }
        }

        else if (v == registerButton) {
            registerPopup("");
        }
    }

    public void login(String email, final String password) {
        final String email2 = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            setUser(email2, password);
                            openMainMenu();
                        }
                        if (!task.isSuccessful()) {
                            Log.w("", "signInWithEmail", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setUser(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
//                SingletonApplications.user = authResult.getUser();

                //Smider alle users ind i et array i singleton
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference();
//
//                myRef.child("users").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                        for (DataSnapshot child : children) {
//                            UserDTO value = child.getValue(UserDTO.class);
//                            SingletonApplications.userArray.add(value);
//                        }
//
//
//                        for (UserDTO users : SingletonApplications.userArray) {
//                            //Den data der passer med userid
//                            makeToast(users.getName());
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });


            }
        });
    }

    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration failed, please try again", Toast.LENGTH_SHORT).show();

                        }
                        else if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registration complete, you can now sign in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openMainMenu() {
        Intent i = new Intent(this, MainMenu.class);
        startActivity(i);
    }




    public void registerPopup(String email) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Register user");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText emailInput = new EditText(context);
        emailInput.setHint("Insert email");
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(emailInput);

        final EditText passwordInput = new EditText(context);
        passwordInput.setHint("Insert password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);

        final EditText passwordInput2 = new EditText(context);
        passwordInput2.setHint("Insert password again");
        passwordInput2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput2);

        if (!email.equals(""))
            emailInput.setText(email);

        alertDialogBuilder.setView(layout);


        alertDialogBuilder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!emailInput.getText().toString().contains("@")) {
                    makeToast("emailNotValid");
                    registerPopup(emailInput.getText().toString());
                }
                else if (emailInput.getText().toString().matches("")) {
                    makeToast("email");
                    registerPopup(emailInput.getText().toString());
                }
                else if (passwordInput.getText().toString().matches("") || passwordInput2.getText().toString().matches("")) {
                    makeToast("pass");
                    registerPopup(emailInput.getText().toString());
                }
                else if (passwordInput.getText().toString().length() < 6 || passwordInput2.getText().toString().length() < 6) {
                    makeToast("passTooShort");
                    registerPopup(emailInput.getText().toString());
                }
                else if (!passwordInput.getText().toString().equals(passwordInput2.getText().toString())){
                    makeToast("passNoMatch");
                    registerPopup(emailInput.getText().toString());
                }
                else {


                    signUp(emailInput.getText().toString(), passwordInput.getText().toString());

                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Umiddelbart lukkes popup bare når der trykkes her, så jeg efterlader det as is
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void makeToast(String option) {
        if (option.equals("email"))
            Toast.makeText(this, "You did not enter a username", Toast.LENGTH_SHORT).show();
        else if (option.equals("pass"))
            Toast.makeText(this, "You did not enter a password", Toast.LENGTH_SHORT).show();
        else if (option.equals("passNoMatch"))
            Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT).show();
        else if (option.equals("emailNotValid"))
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
        else if (option.equals("passTooShort"))
            Toast.makeText(this, "Password is too short, it should be at least 6 characters long", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, option, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}