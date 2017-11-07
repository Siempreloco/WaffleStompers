package com.sourcey.WaffleStompers.presentation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.sourcey.WaffleStompers.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.sourcey.WaffleStompers.objects.AWSClientManager;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_username) EditText _userNameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    private ProgressDialog waitDialog;
    private AlertDialog userDialog;
    private NewPasswordContinuation newPasswordContinuation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AWSClientManager.init(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        showWaitDialog("Authenticating...");

        String username = _userNameText.getText().toString();
        String password = _passwordText.getText().toString();

        AWSClientManager.getUserPool().getUser(username).getSessionInBackground(authenticationHandler);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _userNameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _userNameText.setError("enter a username");
            valid = false;
        } else {
            _userNameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
            _passwordText.setError("between 6 and 12 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
    private void launchUser() {
        Intent menuActivity = new Intent(this, MenuActivity.class);
        menuActivity.putExtra("name", _userNameText.toString());
        startActivityForResult(menuActivity, 4);
    }


    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e(TAG, "Auth Success");
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation continuation, String userID) {
            closeWaitDialog();
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userID, _passwordText.getText().toString(), null);
            continuation.setAuthenticationDetails(authenticationDetails);
            continuation.continueTask();
        }
        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
            multiFactorAuthenticationContinuation.setMfaCode(null);
            multiFactorAuthenticationContinuation.continueTask();
        }
        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Sign-in failed", AWSClientManager.formatException(e));
            _loginButton.setEnabled(true);
        }
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
           //
        }
    };

}
