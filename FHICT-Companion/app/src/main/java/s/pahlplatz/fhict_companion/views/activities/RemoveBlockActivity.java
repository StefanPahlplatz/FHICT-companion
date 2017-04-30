package s.pahlplatz.fhict_companion.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import s.pahlplatz.fhict_companion.R;
import s.pahlplatz.fhict_companion.adapters.BlockAdapter;
import s.pahlplatz.fhict_companion.models.CustomBlock;
import s.pahlplatz.fhict_companion.utils.LocalPersistence;

public class RemoveBlockActivity extends AppCompatActivity {

    private BlockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_block);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayList<CustomBlock> blocks = (ArrayList<CustomBlock>) LocalPersistence.readObjectFromFile(
                getBaseContext(), LocalPersistence.BLOCKS
        );
        
        if (blocks == null) {
            Toast.makeText(this, "No extra blocks found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        adapter = new BlockAdapter(getBaseContext(), blocks);

        ListView listView = (ListView) findViewById(R.id.remove_block_listview);
        listView.setAdapter(adapter);

        Button btnRemove = (Button) findViewById(R.id.remove_block_delete);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> itemsToDelete = adapter.getSelected();

                if (itemsToDelete.size() == 0) {
                    Toast.makeText(getBaseContext(), "Select 1 or more products first", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = itemsToDelete.size() - 1; i >= 0; i--) {
                        int index = itemsToDelete.get(i);

                        // Remove from local lists
                        blocks.remove(index);
                    }

                    LocalPersistence.writeObjectToFile(getBaseContext(), blocks, LocalPersistence.BLOCKS);

                    // Assign new adapter
                    adapter = new BlockAdapter(getBaseContext(), blocks);

                    Toast.makeText(getBaseContext()
                            , (itemsToDelete.size() == 1 ? "Item" : "Items") + " removed"
                            , Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }
}
