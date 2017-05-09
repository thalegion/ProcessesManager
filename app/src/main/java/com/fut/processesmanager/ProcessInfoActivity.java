package com.fut.processesmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

public class ProcessInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_info);
       // getActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView iconView = (ImageView) findViewById(R.id.iconView);
        TextView nameView = (TextView) findViewById(R.id.nameView);
        TextView packageNameView = (TextView) findViewById(R.id.packetNameVIew);
        TextView sizeView = (TextView) findViewById(R.id.sizeView);
        TextView rssView = (TextView) findViewById(R.id.rssView);
        final TextView statusView = (TextView) findViewById(R.id.statusView);
        final Button stopBtn = (Button) findViewById(R.id.stopBtn);

        Intent intent = getIntent();
        final AndroidAppProcess process = (AndroidAppProcess) intent.getExtras().get("ActiveProcess");

        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();

        try {

            Stat stat = process.stat();
            Statm statm = process.statm();

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean killed = false;
                    try {
                        int pid = process.stat().getPid();
                        android.os.Process.killProcess(pid);
                        android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
                        ActivityManager amg = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        amg.killBackgroundProcesses(process.name);
                        killed = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (killed) {
                            statusView.setText("Остановлен");
                            stopBtn.setEnabled(false);
                            stopBtn.setText("Процесс уже остановлен");
                        } else {
                            stopBtn.setEnabled(false);
                            stopBtn.setText("Процесс невозможно остановить");
                        }
                    }
                }
            });

            packageNameView.setText(process.name);
            sizeView.setText(String.valueOf(statm.getSize()) + " байт");
            rssView.setText(String.valueOf(statm.getResidentSetSize()) + " байт");

            String status = "";
            switch (stat.state()) {
                case 'R':
                    status = "Запущен";
                    break;
                case 'S':
                    status = "Спит в прерываемом ожидании";
                    break;
                case 'D':
                    status = "Ожидает в непрерываемом сне";
                    break;
                case 'Z':
                    status = "Зомби";
                    break;
                case 'T':
                case 't':
                    status = "Остановлен";
                    break;
                case 'W':
                    status = "Просыпается";
                    break;
                case 'X':
                case 'x':
                    status = "Мёртв";
                    break;
                case 'K':
                    status = "Просымёртв";
                    break;
                case 'P':
                    status = "Припаркован";
                    break;
                default:
                    status = "Неизвестно";
                    break;
            }
            statusView.setText(status);

            try {
                PackageInfo packageInfo = process.getPackageInfo(context, 0);
                nameView.setText(packageInfo.applicationInfo.loadLabel(pm).toString());
                iconView.setImageDrawable(packageInfo.applicationInfo.loadIcon(pm));
            } catch (PackageManager.NameNotFoundException nnfe) {
                nnfe.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
