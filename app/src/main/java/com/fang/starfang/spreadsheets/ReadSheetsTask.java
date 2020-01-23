package com.fang.starfang.spreadsheets;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReadSheetsTask extends AsyncTask<String, String, String> {
    private static final String TAG = "FANG_SHT_READ";
    private WeakReference<Context> contextWeakReference;
    private WeakReference<AppCompatSpinner> spinnerWeakReference;
    private WeakReference<AppCompatEditText> editTextWeakReference;
    private WeakReference<AppCompatImageButton> infoButtonWeakReference;
    private ArrayList<String> sheetTitles;


    ReadSheetsTask(Context context, AppCompatEditText spreadsheetID, AppCompatSpinner spinner
            , AppCompatImageButton button_read_sheet_info
    ) {
        this.contextWeakReference = new WeakReference<>(context);
        this.editTextWeakReference = new WeakReference<>(spreadsheetID);
        this.spinnerWeakReference = new WeakReference<>(spinner);
        this.infoButtonWeakReference = new WeakReference<>(button_read_sheet_info);

        this.sheetTitles = new ArrayList<>();


    }

    @Override
    protected String doInBackground(String... tasks) {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory factory = JacksonFactory.getDefaultInstance();
        final Sheets sheetsService = new Sheets.Builder(transport,
                factory, null)
                .setApplicationName("fangcatKey")
                .build();
        final Editable spreadsheetIdEditable = editTextWeakReference.get().getText();

        if (spreadsheetIdEditable != null) {
            try {
                Spreadsheet spreadsheet = sheetsService.spreadsheets()
                        .get(spreadsheetIdEditable.toString())
                        .setKey(SpreadsheetsConfig.google_api_key)
                        .setIncludeGridData(false).execute();
                List<Sheet> sheets = spreadsheet.getSheets();
                if (sheets != null) {
                    if (sheets.size() > 0) {
                        for (Sheet sheet : sheets) {
                            String title = sheet.getProperties().getTitle();
                            Log.d(TAG, title);
                            sheetTitles.add(sheet.getProperties().getTitle());
                        }
                        return "succ";
                    }
                }

            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }


        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        if( result != null ) {
            ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(
                    contextWeakReference.get(),
                    android.R.layout.simple_spinner_item, sheetTitles);

            titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWeakReference.get().setAdapter(titleAdapter);
            Log.d(TAG, "success reading sheets");
        }
        infoButtonWeakReference.get().setEnabled(true);
    }
}
