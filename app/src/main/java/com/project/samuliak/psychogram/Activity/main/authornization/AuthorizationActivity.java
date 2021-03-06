package com.project.samuliak.psychogram.Activity.main.authornization;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.project.samuliak.psychogram.API.ClientAPI;
import com.project.samuliak.psychogram.API.PsychogolistAPI;
import com.project.samuliak.psychogram.Activity.main.MainClientActivity;
import com.project.samuliak.psychogram.Activity.main.MainDoctorActivity;
import com.project.samuliak.psychogram.Model.Client;
import com.project.samuliak.psychogram.Model.Psychologist;
import com.project.samuliak.psychogram.R;
import com.project.samuliak.psychogram.Util.Constants;
import com.project.samuliak.psychogram.Util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity {

    private Psychologist doctor;
    private Client clientBody;
    private TextView loginTv, passwordTv;
    private TextInputLayout loginInputLayout, passwordInputLayout;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        initUI();
    }

    private void initUI() {
        doctor = null;
        clientBody = null;
        mSettings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        loginInputLayout = (TextInputLayout) findViewById(R.id.loginInputLayout);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.passwordInputLayout);
        loginTv = (TextView) findViewById(R.id.loginInput);
        passwordTv = (TextView) findViewById(R.id.passwordInput);
        final Button btnLogIn = (Button) findViewById(R.id.btnLogIn);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Psychologist doctor;
            Client client;
            String type = bundle.getString("TYPE");
            assert type != null;
            if (type.equals("doctor")) {
                doctor = bundle.getParcelable(Psychologist.class.getCanonicalName());
                assert doctor != null;
                loginTv.setText(doctor.getLogin());
                passwordTv.setText(doctor.getPassword());
            }
            else {
                client = bundle.getParcelable(Client.class.getCanonicalName());
                assert client != null;
                loginTv.setText(client.getLogin());
                passwordTv.setText(client.getPassword());
            }

        }else {
            savedUser();
        }

        assert btnLogIn != null;
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authentication();
            }
        });
        authentication();
    }

    private void savedUser() {
        if (mSettings.contains(Constants.APP_PREFERENCES_ID)) {
            loginTv.setText(mSettings.getString(Constants.APP_PREFERENCES_LOGIN, ""));
            passwordTv.setText(mSettings.getString(Constants.APP_PREFERENCES_PASSWORD, ""));
        }
    }

    private void authentication() {
        String login = String.valueOf(loginTv.getText());
        String password = String.valueOf(passwordTv.getText());
        Utils.clearFieldHints(loginInputLayout, passwordInputLayout);
        if(Utils.isOnline(this)){
            connectionToServer(login, password);
        }else {
            Utils.clearFields(loginTv, passwordTv);
            Toast.makeText(this, R.string.without_internet_access, Toast.LENGTH_LONG).show();
        }
    }

    //////// связь с сервером!
    private void connectionToServer(String login, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.connecting_to_server));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        PsychogolistAPI serviceDoctor = Utils.getRetrofit().create(PsychogolistAPI.class);
        ClientAPI serviceClient = Utils.getRetrofit().create(ClientAPI.class);
        final Call<Client> callClient = serviceClient.getClientByLogin(login);
        Call<Psychologist> callDoctor = serviceDoctor.getDoctorByName(login);

        callDoctor.enqueue(new Callback<Psychologist>() {
            @Override
            public void onResponse(Call<Psychologist> call, Response<Psychologist> response) {
                if (response.isSuccessful()) {
                    doctor = response.body();
                    if (doctor.getPassword().equals(password)) {
                        setSharedPreferencesAndStartListActivity();
                    } else {
                        Toast.makeText(getBaseContext(), R.string.password_incorrect, Toast.LENGTH_LONG).show();
                        YoYo.with(Techniques.Shake).duration(700).playOn(passwordTv);
                    }
                } else{
                    callClient.enqueue(new Callback<Client>() {
                        @Override
                        public void onResponse(Call<Client> call, Response<Client> response) {
                            if (response.isSuccessful()){
                                clientBody = response.body();
                                if (clientBody.getPassword().equals(password)){
                                    setSharedPreferencesAndStartListActivity();
                                } else{
                                    Toast.makeText(getBaseContext(), R.string.password_incorrect, Toast.LENGTH_LONG).show();
                                    YoYo.with(Techniques.Shake).duration(700).playOn(passwordTv);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Client> call, Throwable t) {
                            Toast.makeText(getBaseContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                            Utils.clearFields(loginTv, passwordTv);
                        }
                    });

                }
                progressDialog.hide();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Psychologist> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.connecting_error, Toast.LENGTH_LONG).show();
                Log.e("samuliak", "error > " + t.toString());
                progressDialog.hide();
                progressDialog.dismiss();
            }

        });

    }

    // записываем данные доктора в SharedPreferences если стоить разрешение
    private void setSharedPreferencesAndStartListActivity(){
        SharedPreferences.Editor editor = mSettings.edit();

        if (doctor == null){
            editor.putLong(Constants.APP_PREFERENCES_ID, clientBody.getId());
            editor.putString(Constants.APP_PREFERENCES_LOGIN, clientBody.getLogin());
            editor.putString(Constants.APP_PREFERENCES_PASSWORD, clientBody.getPassword());
        }else {
            editor.putLong(Constants.APP_PREFERENCES_ID, doctor.getId());
            editor.putString(Constants.APP_PREFERENCES_LOGIN, doctor.getLogin());
            editor.putString(Constants.APP_PREFERENCES_PASSWORD, doctor.getPassword());
        }
        editor.putBoolean(Constants.IS_AUTO_SIGN, true);
        editor.apply();

        editor.clear();

        Intent i;
        if (doctor == null){
            i = new Intent(getBaseContext(), MainClientActivity.class);
            i.putExtra(Client.class.getCanonicalName(), clientBody);
        } else {
            i = new Intent(getBaseContext(), MainDoctorActivity.class);
            i.putExtra(Psychologist.class.getCanonicalName(), doctor);
        }
        startActivity(i);
    }
}
