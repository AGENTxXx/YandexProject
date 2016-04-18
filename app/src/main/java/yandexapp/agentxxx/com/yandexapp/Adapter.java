package yandexapp.agentxxx.com.yandexapp;

/**
 * Created by Admin on 07.04.2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class Adapter  extends ArrayAdapter<String> {

    List<ArtistClass> artists;
    ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance

    Context c;
    LayoutInflater inflater;

    DisplayImageOptions options;

    //Вспомогательный класс
    public class ViewHolder {
        TextView nameTv;
        TextView genreTv;
        TextView artCountTv;
        ImageView cover;
    }

    public Adapter(Context context, List<ArtistClass> artists) {
        super(context, R.layout.list_single, (List)artists);

        //Связывааем переданные данные с полями класса
        this.c=context;
        this.artists = artists;


        //Устанавливаем настройки по умолчанию
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(config);

        //Устанавливаем настройки по сохранению/отображению изображения в ListView
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.stub_image)
                .resetViewBeforeLoading()
                .cacheInMemory()
                .cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Проводим инициализацию, если она ещё не проведена
        if(convertView==null){
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_single,null);
        }

        final ViewHolder holder = new ViewHolder();

        //Устанавливаем связи с элментами Layout'ов
        holder.nameTv=(TextView)convertView.findViewById(R.id.nameTv);
        holder.genreTv=(TextView)convertView.findViewById(R.id.ganreTv);
        holder.artCountTv=(TextView)convertView.findViewById(R.id.artCountTv);
        holder.cover=(ImageView)convertView.findViewById(R.id.imageCover);

        //Загружаем изображение с установленными настройками options
        imageLoader.displayImage(artists.get(position).getSmallCover(),holder.cover,options);

        //Устанавливаем значения
        holder.nameTv.setText(artists.get(position).getName());
        holder.genreTv.setText(artists.get(position).getGenres());
        String artInfo = artists.get(position).getAlbums()+" "
                +UniClass.getNumEnding(artists.get(position).getAlbums(), UniClass.endingAlbum)
                +", " + artists.get(position).getTracks()
                + " " + UniClass.getNumEnding(artists.get(position).getTracks(), UniClass.endingTrack);
        holder.artCountTv.setText(artInfo);

        return convertView;
    }
}
