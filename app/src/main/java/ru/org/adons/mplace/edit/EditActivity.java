package ru.org.adons.mplace.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import ru.org.adons.mplace.MConstants;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.db.DBUpdateService;

public class EditActivity extends EditActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ACTION EDIT - set initial value
        place = (Place) getIntent().getSerializableExtra(MConstants.EXTRA_PLACE);
        imagePath = place.getImagePath();
        isImage = true;
        if (!TextUtils.isEmpty(imagePath)) {
            setImage();
        }
        name.setText(place.getName());
        description.setText(place.getDescription());
    }

    /**
     * Handle click Action Bar Button 'Done'
     */
    @Override
    public void done(MenuItem item) {
        if (!TextUtils.isEmpty(imagePath) && !imagePath.equals(place.getImagePath())
                || !name.getText().toString().equals(place.getName())
                || !description.getText().toString().equals(place.getDescription())) {

            final Place newPlace = getNewPlace();
            newPlace.setID(place.getID());

            final Intent serviceIntent = new Intent(this, DBUpdateService.class)
                    .setAction(MConstants.ACTION_EDIT_PLACE)
                    .putExtra(MConstants.EXTRA_PLACE, newPlace);
            startService(serviceIntent);

            Intent result = new Intent();
            result.putExtra(MConstants.EXTRA_PLACE, newPlace);
            setResult(RESULT_OK, result);
        }
        finish();
    }

}
