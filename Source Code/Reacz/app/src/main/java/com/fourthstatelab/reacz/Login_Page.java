package com.fourthstatelab.reacz;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

public class Login_Page extends AppCompatActivity {

    EditText username;
    EditText password;
    Button button;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    CallbackManager mcallbackmanager;
    FirebaseUser firebaseUser;
    GoogleApiClient mgoogleapiclient;


    LoginButton loginbutton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthlistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthlistener != null) {
            mAuth.removeAuthStateListener(mAuthlistener);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 007) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //TODO: Faliure code should be put here..............
            }
        } else
            mcallbackmanager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__page);

        shared = getApplicationContext().getSharedPreferences("Reaczsharedpreference", Context.MODE_PRIVATE);
        editor = shared.edit();


        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.button);

        // Facebook Login part

        loginbutton = (LoginButton) findViewById(R.id.login_button);




        loginbutton.setReadPermissions("email", "public_profile");
        mcallbackmanager = CallbackManager.Factory.create();

        //Direct Login if uer already exists(Facebook Login)
        AccessToken accesstoken = AccessToken.getCurrentAccessToken();
        if (accesstoken != null) {
            startActivity(new Intent(Login_Page.this, After_login.class));
            finish();
        }
        //-----------------------------------------------------------------------


        loginbutton.registerCallback(mcallbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("tagger", "facebook:onSuccess:" + loginResult);
                handlefbaccesstokens(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //Facebook Login part end


        mAuth = FirebaseAuth.getInstance();


        mAuthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                } else {
                }

            }
        };

        if (firebaseUser != null)
            Toast.makeText(this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

        //SIGN IN USING USERNAME AND PASSWORD

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninWithEmailAndPassword();

            }
        });


        //SIGN IN USING GOOGLE API's

        SignInButton signInButton=(SignInButton)findViewById(R.id.googlesigninbutton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mgoogleapiclient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    firebaseUser=mAuth.getCurrentUser();
                }
                else
                {
                    //TODO:Login Faliure code has to be added
                }
            }
        });
    }



    public void handlefbaccesstokens(final AccessToken token)
    {
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    firebaseUser=mAuth.getCurrentUser();
                    String savedfbtoken=new Gson().toJson(token);
                    Toast.makeText(Login_Page.this, savedfbtoken+"", Toast.LENGTH_SHORT).show();
                    editor.putString("fbloginsavedtoken",savedfbtoken);
                    editor.apply();
                    startActivity(new Intent(Login_Page.this,After_login.class));
                    finish();


                }
                else
                {
                    Toast.makeText(Login_Page.this, task.getException()+"", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgoogleapiclient);
        startActivityForResult(signInIntent,007);
    }





    public void SigninWithEmailAndPassword()
    {
        mAuth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                try {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login_Page.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Login_Page.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e) {
                    Toast.makeText(Login_Page.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

}
