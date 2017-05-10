package com.fut.processesmanager;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ProcessAdapter pa;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();
        ListView procList = (ListView) findViewById(R.id.processesList);
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

        ArrayList<AndroidAppProcess> processesList = new ArrayList<AndroidAppProcess>(processes.size());
        processesList.addAll(processes);

        pa = new ProcessAdapter(this,processesList);
        procList.setAdapter(pa);

        procList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AndroidAppProcess process = (AndroidAppProcess) parent.getAdapter().getItem(position);

                Intent myIntent = new Intent(context, ProcessInfoActivity.class);
                myIntent.putExtra("ActiveProcess", process); //Optional parameters
                startActivity(myIntent);
            }
        });

        final FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);
                updateList();
                btn.setEnabled(true);
            }
        });
    }

    private void updateList() {

        pa.clear();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

        ArrayList<AndroidAppProcess> processesList = new ArrayList<AndroidAppProcess>(processes.size());
        processesList.addAll(processes);

        pa.addAll(processesList);
    }
}
