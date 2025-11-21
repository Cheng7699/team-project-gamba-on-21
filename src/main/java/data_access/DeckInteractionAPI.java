package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;


public class DeckInteractionAPI implements DeckInteraction {

    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://deckofcardsapi.com/api/";

    String createDeck(Integer deck_count, Boolean shuffled, Boolean jokers) throws IOException {
        String url = API_URL + "/deck/new/";
        if (shuffled) {url += "shuffle/";}
        url += "?deck_count="+deck_count.toString();
        if (jokers) {url += "&jokers_enabled=true";}

        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Response response = client.newCall(request).execute();
        final JSONObject responseBody = new JSONObject(response.body().string());

        if (responseBody.getBoolean("success")) {
            String deck_id = responseBody.getString("deck_id");

            return deck_id;
        }

        else {
            throw new IOException(responseBody.getString("message"));
        }

    }

}
