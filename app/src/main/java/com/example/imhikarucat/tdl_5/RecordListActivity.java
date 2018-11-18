package com.example.imhikarucat.tdl_5;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {


    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;
//    static boolean firstTime = true;
    SharedPreferences firstTime;

    public SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Record List");

        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        //creating database
        mSQLiteHelper = new SQLiteHelper(this, "RECORDDB.sqlite", null, 1);

        //creating table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, task VARCHAR, duration VARCHAR, status VARCHAR)");

        //Insert 2 sample tasks, put into pref so it can only added once
        firstTime = getSharedPreferences("FirstTime", MODE_PRIVATE);
        if (!firstTime.getBoolean("isFirstTime", false)) {
            //your code goes here
            try {
                mSQLiteHelper.insertData("Mindful breathing", "15", "New");
            }
            catch (Exception e){
                e.printStackTrace();
            }

            try {
                mSQLiteHelper.insertData("Bedtime breathing", "15", "New");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            final SharedPreferences pref = getSharedPreferences("FirstTime", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstTime", true);
            editor.commit();
        }

        //get all data from sqlite
        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String task = cursor.getString(1);
            String duration = cursor.getString(2);
            String status = cursor.getString(3);
            //add to list
            mList.add(new Model(id, task, duration, status));
        }
        mAdapter.notifyDataSetChanged();
        if (mList.size()==0){
            //if there is no record in table of database which means listview is empty
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this, arrID.get(position));
                        }
                        if (i==1){
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordListActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warning");
        dialogDelete.setMessage("Are you sure?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        final EditText edtTask = dialog.findViewById(R.id.edtTask);
        final EditText edtDuration = dialog.findViewById(R.id.edtDuration);
        final EditText edtStatus = dialog.findViewById(R.id.edtStatus);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

//        Toast.makeText(activity, ""+position, Toast.LENGTH_SHORT).show();

        int iddd = position;
        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+iddd);
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String task = cursor.getString(1);
            edtTask.setText(task);
            String duration = cursor.getString(2);
            edtDuration.setText(duration);
            String status = cursor.getString(3);
            edtStatus.setText(status);
            //add to list
            mList.add(new Model(id, task, duration, status));
        }

        //set width of dialog
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set hieght of dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSQLiteHelper.updateData(
                            edtTask.getText().toString().trim(),
                            edtDuration.getText().toString().trim(),
                            edtStatus.getText().toString().trim(),
                            position);
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update success", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Update error", error.getMessage());
                }
                updateRecordList();
            }
        });

    }

    private void updateRecordList() {
        //get all data from sqlite
        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String task = cursor.getString(1);
            String duration = cursor.getString(2);
            String status = cursor.getString(3);

            mList.add(new Model(id, task, duration, status));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void onTimerClicked(View view) {
        startActivity(new Intent(RecordListActivity.this, Timer.class));
    }
}
