package com.project.samuliak.psychogram.Activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.project.samuliak.psychogram.Activity.main.menu.common_items.DialogActivity;
import com.project.samuliak.psychogram.Activity.main.menu.common_items.ProfileDoctorActivity;
import com.project.samuliak.psychogram.Activity.main.menu.doctor_menu_items.ExClientsActivity;
import com.project.samuliak.psychogram.Activity.main.menu.doctor_menu_items.FriendActivity;
import com.project.samuliak.psychogram.Activity.main.menu.doctor_menu_items.MyClientsActivity;
import com.project.samuliak.psychogram.Adapter.MenuAdapter;
import com.project.samuliak.psychogram.Listener.RecyclerClickListener;
import com.project.samuliak.psychogram.Model.Psychologist;
import com.project.samuliak.psychogram.R;

public class MainDoctorActivity extends AppCompatActivity {

    private Psychologist doctor;
    private RecyclerView menuRV;
    private String[] items = new String[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);
        initActivity();
    }

    private void initActivity() {
        doctor = getIntent().getExtras().getParcelable(Psychologist.class.getCanonicalName());
        initUI();
        MenuAdapter adapter = new MenuAdapter(items);
        menuRV.setAdapter(adapter);
        menuRV.setLayoutManager(new LinearLayoutManager(this));
        menuRV.addOnItemTouchListener(new RecyclerClickListener(this,
                new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i;
                        switch (position){
                            case 0:
                                i = new Intent(view.getContext(), MyClientsActivity.class);
                                i.putExtra(Psychologist.class.getCanonicalName(), doctor);
                                startActivity(i);
                                break;
                            case 1:
                                break;
                            case 2:
                                i = new Intent(view.getContext(), ExClientsActivity.class);
                                i.putExtra(Psychologist.class.getCanonicalName(), doctor);
                                startActivity(i);
                                break;
                            case 3:
                                break;
                            case 4:
                                i = new Intent(view.getContext(), DialogActivity.class);
                                i.putExtra(Psychologist.class.getCanonicalName(), doctor);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(view.getContext(), ProfileDoctorActivity.class);
                                i.putExtra("IS_CLIENT_LOOK", false);
                                i.putExtra("IS_OWN_ACCOUNT", true);
                                i.putExtra(Psychologist.class.getCanonicalName(), doctor);
                                startActivity(i);
                                break;
                            case 6:
                                break;
                            case 7:
                                i = new Intent(view.getContext(), FriendActivity.class);
                                i.putExtra(Psychologist.class.getCanonicalName(), doctor);
                                startActivity(i);
                                break;
                            case 8:
                                break;
                        }
                    }
                }));
    }

    private void initUI() {
        items[0] = getResources().getString(R.string.my_clients);
        items[1] = getResources().getString(R.string.cabinet);
        items[2] = getResources().getString(R.string.clients_history);
        items[3] = getResources().getString(R.string.psychodiagnostika);
        items[4] = getResources().getString(R.string.online_help);
        items[5] = getResources().getString(R.string.information_about_me);
        items[6] = getResources().getString(R.string.first_help);
        items[7] = getResources().getString(R.string.friends_from_work);
        items[8] = getResources().getString(R.string.settings);
        menuRV = (RecyclerView) findViewById(R.id.menuRV);
    }
}
