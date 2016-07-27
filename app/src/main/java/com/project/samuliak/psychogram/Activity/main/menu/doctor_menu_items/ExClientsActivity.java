package com.project.samuliak.psychogram.Activity.main.menu.doctor_menu_items;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.project.samuliak.psychogram.API.PsychogolistAPI;
import com.project.samuliak.psychogram.Adapter.MyClientsAdapter;
import com.project.samuliak.psychogram.Model.Client;
import com.project.samuliak.psychogram.Model.Psychogolist;
import com.project.samuliak.psychogram.R;
import com.project.samuliak.psychogram.Util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExClientsActivity extends AppCompatActivity {

    private Psychogolist doctor;
    private RecyclerView rv_ex;
    private PsychogolistAPI service;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_clients);
        initUI();
    }

    private void initUI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(PsychogolistAPI.class);
        progressDialog = new ProgressDialog(this);
        doctor = getIntent().getExtras().getParcelable(Psychogolist.class.getCanonicalName());
        rv_ex = (RecyclerView) findViewById(R.id.rv_ex_clients);
        getExClients();
    }

    private void getExClients() {
        Call<List<Client>> listCurrentCall = service.getExClients(doctor.getLogin());
        initProgressDialog();
        listCurrentCall.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful()){
                    MyClientsAdapter adapter = new MyClientsAdapter(getBaseContext(), response.body(),
                            false, true);
                    rv_ex.setAdapter(adapter);
                    rv_ex.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.connecting_error, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private void initProgressDialog() {
        progressDialog.setMessage(getResources().getString(R.string.connecting_to_server));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
}