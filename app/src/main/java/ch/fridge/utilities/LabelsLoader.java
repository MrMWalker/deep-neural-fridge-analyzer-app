package ch.fridge.utilities;

import android.content.res.AssetManager;

import ch.fridge.domain.Label;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class LabelsLoader {

    public static List<Label> loadLabels(AssetManager assetManager, String labelFilename) {
        try {
            InputStream labelsInput = assetManager.open(labelFilename);
            BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            String labelsString = sb.toString();
            List<Label> labels = convertJsonToLabels(labelsString).stream()
                    .sorted(Comparator.comparingInt(Label::getId))
                    .collect(Collectors.toList());
            return labels;
        }
        catch(JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Label> convertJsonToLabels(String labelsString) throws JSONException {
        JSONObject json = new JSONObject(labelsString);
        List<Label> labels = new ArrayList<>();
        JSONArray labelsJson = json.getJSONArray("labels");
        for (int i = 0; i < labelsJson.length(); i++) {
            JSONObject jsonObject = labelsJson.getJSONObject(i);
            Label label = convertJsonObjectToLabel(jsonObject);
            labels.add(label);
        }

        return labels;
    }

    private static Label convertJsonObjectToLabel(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");
        String name = jsonObject.getString("name");
        String english = jsonObject.getString("english");
        String german = jsonObject.getString("german");
        return new Label(id, name, english, german);
    }

}
