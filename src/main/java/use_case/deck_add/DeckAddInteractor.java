package use_case.deck_add;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeckAddInteractor implements DeckAddInputBoundary {
    private String deckID;
    private String[] cardArray;
    private Integer drawNumber;
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://deckofcardsapi.com/api/";

    public DeckAddInteractor(String deckID, Integer drawNumber) {
        this.deckID = deckID;
        this.drawNumber = drawNumber;
    }

    @Override
    public void execute(DeckAddInputData deckAddInputData) {
        deckID = deckAddInputData.getDeckID();
        cardArray = deckAddInputData.getCardArray();




    }
}
