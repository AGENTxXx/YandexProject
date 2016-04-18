package yandexapp.agentxxx.com.yandexapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 17.04.2016.
 */



public class ArtistClass implements Parcelable {
    private String name;
    private String genres;
    private Integer tracks;
    private Integer albums;
    private String description;
    private String smallCover;
    private String bigCover;
    private String link;

    public String getName() {
        return name;
    }

    public String getGenres() {
        return genres;
    }

    public Integer getTracks() {
        return tracks;
    }

    public Integer getAlbums() {
        return albums;
    }

    public String getDescription() {
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public String getSmallCover() {
        return smallCover;
    }

    public String getBigCover() {
        return bigCover;
    }

    public String getLink() {
        return link;
    }

    public ArtistClass(JSONObject artistInfo) {
        try {
            this.name = artistInfo.getString("name");
            this.tracks = artistInfo.getInt("tracks");
            this.albums = artistInfo.getInt("albums");
            this.description = artistInfo.getString("description");
            this.genres = genresFormatter(artistInfo.getJSONArray("genres"));
            this.smallCover = artistInfo.getJSONObject("cover").getString("small");
            this.bigCover = artistInfo.getJSONObject("cover").getString("big");
            this.link = artistInfo.getString("link");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArtistClass(Parcel in) {
        String[] data = new String[8];
        in.readStringArray(data);
        this.name = data[0];
        this.genres = data[1];
        this.tracks = Integer.parseInt(data[2]);
        this.albums = Integer.parseInt(data[3]);
        this.description = data[4];
        this.smallCover = data[5];
        this.bigCover = data[6];
        this.link = data[7];
    }

    public String genresFormatter(JSONArray genresArr) {

        try {
            StringBuilder genresBuilder = new StringBuilder();
            genresBuilder.setLength(0);
            if (genresArr.length() > 0) {
                genresBuilder.append(genresArr.getString(0));
                for (int j=1; j<genresArr.length(); j++) {
                    genresBuilder.append(", " + genresArr.getString(j));
                }
            }
            else {
                genresBuilder.append("Информация отсутствует");
            }

            return genresBuilder.toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
            return "Инфомация отсутствует";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { name, genres, tracks.toString(), albums.toString(), description, smallCover, bigCover, link });
    }

    public static final Parcelable.Creator<ArtistClass> CREATOR = new Parcelable.Creator<ArtistClass>() {

        @Override
        public ArtistClass createFromParcel(Parcel source) {
            return new ArtistClass(source);
        }

        @Override
        public ArtistClass[] newArray(int size) {
            return new ArtistClass[size];
        }
    };




}
