package s.pahlplatz.fhict_companion.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * TimePicker dialog.
 */

public class SetTime implements TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTime(EditText editText, Context ctx) {
        this.editText = editText;
        this.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker();
            }
        });
        this.myCalendar = Calendar.getInstance();
        this.ctx = ctx;
    }

    /**
     * Shows the picker.
     */
    private void showPicker() {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        new TimePickerDialog(ctx, this, hour, minute, true).show();
    }

    /**
     * Sets the text in the EditText after the time picker is closed.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.editText.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":"
                + (minute < 10 ? "0" + minute : minute));
    }
}