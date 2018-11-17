package com.example.imhikarucat.tdl_5;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText mEdtTask, mEdtDuration, mEdtStatus;
    Button mBtnAdd, mBtnList;


    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Record");

        mEdtTask = findViewById(R.id.edtTask);
        mEdtDuration = findViewById(R.id.edtDuration);
        mEdtStatus = findViewById(R.id.edtStatus);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnList = findViewById(R.id.btnList);

        //creating database
        mSQLiteHelper = new SQLiteHelper(this, "RECORDDB.sqlite", null, 1);

        //add record to sqlite
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSQLiteHelper.insertData(
                            mEdtTask.getText().toString().trim(),
                            mEdtDuration.getText().toString().trim(),
                            mEdtStatus.getText().toString().trim()
                    );
                    Toast.makeText(MainActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    //reset views
                    mEdtTask.setText("");
                    mEdtDuration.setText("");
                    mEdtStatus.setText("");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //show record list
        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start recordlist activity
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
                finish();
            }
        });


    }
}
