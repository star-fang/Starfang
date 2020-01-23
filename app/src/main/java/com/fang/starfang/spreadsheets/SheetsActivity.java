package com.fang.starfang.spreadsheets;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.fang.starfang.R;

public class SheetsActivity extends AppCompatActivity {

    private static final String TAG = "FANG_ACT_SHT";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "_ON CREATE");

        setContentView(R.layout.activity_sheets);

        final AppCompatEditText text_sheet_id = findViewById(R.id.text_sheet_id);
        final AppCompatImageButton button_read_sheet_info = findViewById(R.id.button_read_sheet_info);
        final AppCompatSpinner spinner_sheet = findViewById(R.id.spinner_sheet);
        final AppCompatEditText text_sheet_range1 = findViewById(R.id.text_sheet_range1);
        final AppCompatEditText text_sheet_range2 = findViewById(R.id.text_sheet_range2);
        final AppCompatImageButton button_upload_sheet = findViewById(R.id.button_upload_sheet);
        final AppCompatImageButton button_download_sheet = findViewById(R.id.button_download_sheet);
        final AppCompatEditText text_sheet_result = findViewById(R.id.text_sheet_result);

        button_read_sheet_info.setOnClickListener(v -> {
            button_read_sheet_info.setEnabled(false);
            ReadSheetsTask readSheetsTask = new ReadSheetsTask(this, text_sheet_id
            , spinner_sheet, button_read_sheet_info);
            readSheetsTask.execute("");
        });

        button_download_sheet.setOnClickListener(v -> {
            button_download_sheet.setEnabled(false);
            DownloadSheetTask downloadSheetTask = new DownloadSheetTask( spinner_sheet, button_download_sheet,
                    text_sheet_id, text_sheet_range1, text_sheet_range2, text_sheet_result);
            downloadSheetTask.execute("");
        });


    }


}
