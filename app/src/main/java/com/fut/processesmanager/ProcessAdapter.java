package com.fut.processesmanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Statm;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by User on 09.05.2017.
 */

public class ProcessAdapter extends ArrayAdapter<AndroidAppProcess> {

    private Context context;
    private PackageManager pm;

    public ProcessAdapter(Context context, ArrayList<AndroidAppProcess> processes) {
        super(context, 0, processes);

        this.context = context;
        this.pm = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AndroidAppProcess process = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_process, parent, false);
        }

        // Lookup view for data population
        TextView nameView = (TextView) convertView.findViewById(R.id.nameView);
        TextView memoryView = (TextView) convertView.findViewById(R.id.memoryView);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        // Populate the data into the template view using the data object

        try {
            PackageInfo packageInfo = process.getPackageInfo(context, 0);
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);

            nameView.setText(appName);
            imageView.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException nnfe) {
            nnfe.printStackTrace();
        }

        try {
            Statm statm = process.statm();
            long totalSizeOfProcess = statm.getResidentSetSize();
            totalSizeOfProcess = totalSizeOfProcess/1048576;
            //long residentSetSize = statm.getResidentSetSize();

            memoryView.setText(String.valueOf(totalSizeOfProcess) + "Мб.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
