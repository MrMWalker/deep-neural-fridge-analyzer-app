package ch.fridge.activities.settings;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import ch.fridge.R;
import ch.fridge.detection.DetectorFactory;
import ch.fridge.domain.Label;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_headers);

        final MultiSelectListPreference shoppingListPreferences =
                (MultiSelectListPreference) findPreference("pref_shoppinglist");
        List<Label> labels = DetectorFactory.getLabels(getAssets()).stream()
                .sorted(Comparator.comparing(Label::getDisplayText)).collect(Collectors.toList());
        List<String> labelValues = labels.stream().map(x -> "" + x.getId()).collect(Collectors.toList());
        List<String> labelsText = labels.stream().map(Label::getDisplayText).collect(Collectors.toList());
        shoppingListPreferences.setEntryValues(labelValues.toArray(new CharSequence[labelValues.size()]));
        shoppingListPreferences.setEntries(labelsText.toArray(new CharSequence[labelsText.size()]));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}