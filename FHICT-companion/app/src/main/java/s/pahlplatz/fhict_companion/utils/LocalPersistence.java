package s.pahlplatz.fhict_companion.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class to read and write objects to local storage.
 */

public class LocalPersistence {
    private static final String TAG = LocalPersistence.class.getSimpleName();

    public static void writeObjectToFile(Context ctx, Object object, String filename) {
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = ctx.openFileOutput(filename, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readObjectFromFile(Context ctx, String filename) {
        ObjectInputStream objectIn = null;
        Object object = null;
        try {
            FileInputStream fileIn = ctx.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "readObjectFromFile: No saved schedule found.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
        return object;
    }
}
