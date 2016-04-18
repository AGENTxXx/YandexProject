import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import yandexapp.agentxxx.com.yandexapp.ArtistClass;

import static org.junit.Assert.assertEquals;
/**
 * Created by Admin on 15.04.2016.
 */
public class NewTest {

    @Test
    public void testCreateFormatter() {
        try{
            JSONObject artistObj = new JSONObject("{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\",\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\",\"description\":\"description\",\"cover\":{\"small\":\"small\",\"big\":\"big\"}}");
            ArtistClass artist = new ArtistClass(artistObj);
            assertEquals("Tove Lo",artist.getName());
            assertEquals((Integer)22,artist.getAlbums());
            assertEquals((Integer)81,artist.getTracks());
            assertEquals("Description",artist.getDescription());
            assertEquals("small",artist.getSmallCover());
            assertEquals("big",artist.getBigCover());
            assertEquals("pop, dance, electronics",artist.getGenres());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
