package s.pahlplatz.fhict_companion.views.activities;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.models.Block;
import s.pahlplatz.fhict_companion.models.CustomBlock;
import s.pahlplatz.fhict_companion.utils.DayHelper;
import s.pahlplatz.fhict_companion.utils.LocalPersistence;
import s.pahlplatz.fhict_companion.utils.SetTime;

public class AddBlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Spinner days = (Spinner) findViewById(R.id.add_block_day);
        ArrayAdapter<String> adp1 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, DayHelper.DAYS);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days.setAdapter(adp1);

        final EditText etSubject = (EditText) findViewById(R.id.add_block_subject);
        final EditText etTeacher = (EditText) findViewById(R.id.add_block_teacher);
        final EditText etRoom = (EditText) findViewById(R.id.add_block_room);
        final EditText etStart = (EditText) findViewById(R.id.add_block_start);
        SetTime startTime = new SetTime(etStart, this);
        final EditText etEnd = (EditText) findViewById(R.id.add_block_end);
        SetTime endTime = new SetTime(etEnd, this);

        Button save = (Button) findViewById(R.id.add_block_save);
        final EditText[] fields = new EditText[]{etTeacher, etRoom, etStart, etEnd};
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check compontents for empty strings.
                for (EditText e : fields) {
                    if (empty(e.getText().toString())) {
                        e.setError("Can't be empty.");
                        e.requestFocus();
                        return;
                    }
                }

                CustomBlock block = new CustomBlock(
                        etRoom.getText().toString(),
                        etSubject.getText().toString(),
                        etTeacher.getText().toString(),
                        etStart.getText().toString(),
                        etEnd.getText().toString(),
                        days.getSelectedItem().toString()
                );
                store(block);
                finish();
            }
        });
    }

    /**
     * Stores the block to LocalPersistence.
     * @param block to store.
     * @return whether the action succeeded.
     */
    private boolean store(CustomBlock block) {
        try {
            ArrayList<CustomBlock> blocks = (ArrayList<CustomBlock>)LocalPersistence.readObjectFromFile(getBaseContext(), LocalPersistence.BLOCKS);
            if (blocks == null) {
                blocks = new ArrayList<>();
            }
            blocks.add(block);
            LocalPersistence.writeObjectToFile(getBaseContext(), blocks, LocalPersistence.BLOCKS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Null-safe, short-circuit evaluation.
     *
     * @param s string to check.
     * @return Whether the string is empty or not.
     */
    public static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }
}
