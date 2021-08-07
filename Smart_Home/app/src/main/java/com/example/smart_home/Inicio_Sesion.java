package com.example.smart_home;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Inicio_Sesion extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    /*ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new
                    ActivityResultCallback<Instrumentation.ActivityResult>() {
                @Override
                public void onActivityResult(Instrumentation.ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.w("TAG", "Fallo el inicio de sesión con google.", e);
                        }
                    }
                }
            });
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }

    }

    public void iniciarSesion(View view) {
        //resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        System.out.println("error");
                        updateUI(null);
                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());
        } else {
            System.out.println("sin registrarse");
        }
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> updateUI(null));
    }
}