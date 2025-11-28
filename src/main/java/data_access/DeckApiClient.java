package data_access;

import entity.Card;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.game_start.GameStartDataAccessInterface;
import use_case.playerStand.PlayerStandUserDataAccessInterface;

import java.io.IOException;

/**
 A class that lets the app interact with the deck of cards API
 Reminder: The card draw/deck is a deck; The player/dealer hands are piles
 Also, the piles are part of the deck itself!!
 */
public class DeckApiClient implements GameStartDataAccessInterface,
        PlayerStandUserDataAccessInterface {

    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://deckofcardsapi.com/api/";


    public String createDeck(Boolean shuffled, Boolean jokers) throws IOException {
        String url = API_URL + "/deck/new/";
        if (shuffled) {url += "shuffle/";}
        if (jokers) {url += "?jokers_enabled=true";}


        final JSONObject responseBody = getResponseBody(url);

        if (responseBody.getBoolean("success")) {
            return responseBody.getString("deck_id");
        }

        else {
            throw new IOException(responseBody.getString("message"));
        }

    }

    public Card drawCard(String deckId) throws IOException {
        String url = API_URL + "deck/" + deckId + "/draw/?count=1";
        final JSONObject responseBody = getResponseBody(url);

        if (!responseBody.isEmpty() && responseBody.getBoolean("success")) {
            return getJsonCards(responseBody.getJSONArray("cards"))[0];
        }

        else {
            throw new IOException("There is an error with the API interaction");
        }
    }

    public Card[] drawCards(String deckId, Integer number) throws IOException {
        String url = API_URL + "deck/" + deckId + "/draw/?count=" + number.toString();
        final JSONObject responseBody = getResponseBody(url);


        if (!responseBody.isEmpty() && responseBody.getBoolean("success")) {
            return getJsonCards(responseBody.getJSONArray("cards"));
        }

        else {
            throw new IOException("There is an error with the API interaction");
        }
    }

    /**
    Adds a card to an existing Pile, or creates a pile containing said card
     */
    public void addCard(String deckId, String pileName, Card card) throws IOException {
        String url = API_URL + "deck/" + deckId + "/pile/" + pileName
                + "/add/?cards=" + card.getCode();
        final JSONObject responseBody = getResponseBody(url);

        if (responseBody.isEmpty() || !responseBody.getBoolean("success")) {
            throw new IOException("There is an error with the API interaction");
        }
    }

    public void addCards(String deckId, String pileName, Card[] cards) throws IOException {
        String cardCodes = "";
        for (Card card : cards) {
            cardCodes += card.getCode();
            cardCodes += ",";
        }
        cardCodes = cardCodes.substring(0, cardCodes.length() - 1);

        String url = API_URL + "deck/" + deckId + "/pile/" + pileName
                + "/add/?cards=" + cardCodes;
        final JSONObject responseBody = getResponseBody(url);
        if (responseBody.isEmpty() || !responseBody.getBoolean("success")) {
            throw new IOException("There is an error with the API interaction");
        }
    }


    public Card[] listPile(String deckId, String pileName) throws IOException {
        String url = API_URL + "deck/" + deckId + "/pile/" + pileName + "/list";
        final JSONObject responseBody = getResponseBody(url);
        if (!responseBody.isEmpty() && responseBody.getBoolean("success")) {
            JSONObject pileJson = responseBody.getJSONObject("piles").getJSONObject(pileName);
            return getJsonCards(pileJson.getJSONArray("cards"));
        }

        else {
            throw new IOException("There is an error with the API interaction");
        }
    }

    public void pileRemoveCard(String deckId, String pileName, Card card) throws IOException {
        String url = API_URL + "deck/" + deckId + "/pile/" + pileName
                + "/draw/?cards="+card.getCode();
        final JSONObject responseBody = getResponseBody(url);
        if (responseBody.isEmpty() || !responseBody.getBoolean("success")) {
            throw new IOException("There is an error with the API interaction");
        }
    }

    @NotNull
    private JSONObject getResponseBody(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Response response = client.newCall(request).execute();
        final JSONObject responseBody = new JSONObject(response.body().string());
        return responseBody;
    }

    private Card[] getJsonCards(JSONArray cards) {
        Card[] cardArray = new Card[cards.length()];
        for (int i = 0; i < cards.length(); i++) {
            JSONObject card = cards.getJSONObject(i);
            cardArray[i] = new Card(
                    card.getString("code"),
                    card.getJSONObject("images").getString("png"),
                    card.getString("value"),
                    card.getString("suit")
            );
        }
        return cardArray;
    }

    public void shuffle(String deckId) throws IOException {
        String url = API_URL + "deck/" + deckId + "/shuffle/";
        final JSONObject responseBody = getResponseBody(url);

        if (responseBody.isEmpty() || !responseBody.getBoolean("success")) {
            throw new IOException("There is an error with the API interaction");
        }
    }


}
