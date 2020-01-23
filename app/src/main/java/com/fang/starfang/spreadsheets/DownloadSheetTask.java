package com.fang.starfang.spreadsheets;

import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class DownloadSheetTask extends AsyncTask<String, String, String> {
    private static final String TAG = "FANG_SHT_DOWN";
    private WeakReference<AppCompatSpinner> spinnerWeakReference;
    private WeakReference<AppCompatImageButton> downButtonWeakReference;
    private WeakReference<AppCompatEditText> editTextWeakReference;
    private WeakReference<AppCompatEditText> rangeTextWeakReference1;
    private WeakReference<AppCompatEditText> rangeTextWeakReference2;
    private WeakReference<AppCompatEditText> resultTextWeakReference;

    DownloadSheetTask(
            AppCompatSpinner spinner
            , AppCompatImageButton button_read_sheet_down
            , AppCompatEditText spreadsheetID
            , AppCompatEditText text_sheet_range1
            , AppCompatEditText text_sheet_range2
            , AppCompatEditText text_sheet_result) {
        this.spinnerWeakReference = new WeakReference<>(spinner);
        this.downButtonWeakReference = new WeakReference<>(button_read_sheet_down);
        this.editTextWeakReference = new WeakReference<>(spreadsheetID);
        this.rangeTextWeakReference1 = new WeakReference<>(text_sheet_range1);
        this.rangeTextWeakReference2 = new WeakReference<>(text_sheet_range2);
        this.resultTextWeakReference = new WeakReference<>(text_sheet_result);
    }


    @Override
    protected String doInBackground(String... strings) {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory factory = JacksonFactory.getDefaultInstance();
        final Sheets sheetsService = new Sheets.Builder(transport,
                factory, null)
                .setApplicationName("fangcatKey")
                .build();

        Editable spreadsheetIdEditable = editTextWeakReference.get().getText();

        if( spreadsheetIdEditable != null ) {

            try {
                String spreadSheetRange = (String) spinnerWeakReference.get().getSelectedItem();


                if (spreadSheetRange != null) {

                    Editable rangeEditable1 = rangeTextWeakReference1.get().getText();
                    Editable rangeEditable2 = rangeTextWeakReference2.get().getText();


                    if( rangeEditable1 != null && rangeEditable2 != null) {
                        String rangeSuffix = "!" + rangeEditable1.toString() + ":" + rangeEditable2.toString();
                        spreadSheetRange += rangeSuffix;
                    }

                    ValueRange result = sheetsService.spreadsheets().values()
                            .get(spreadsheetIdEditable.toString(), spreadSheetRange)
                            .setKey(SpreadsheetsConfig.google_api_key)
                            .execute();

                   // int numRows = result.getValues() != null ?
                     //       result.getValues().size() : 0;

                    //Log.d(TAG, "rows retrieved " + numRows + result);

                    return result.toString();
                }


            } catch (IOException e) {
                return e.toString();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        resultTextWeakReference.get().setText(result);
        downButtonWeakReference.get().setEnabled(true);
    }
}
