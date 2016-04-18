package yandexapp.agentxxx.com.yandexapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.regex.Pattern;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Admin on 12.04.2016.
 */
public class ArtistInfoActivity extends ActionBarActivity {



    boolean isZoomImage = false;
    RelativeLayout fmIv;
    ImageView zoomIv;

    PhotoViewAttacher mAttacher;

    //Вспомогательный класс
    public class ArtistHolder {
        TextView genresTv;
        TextView bioTv;
        TextView artInfoTv;
        ImageView coverIv;
        TextView linkTv;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.artist_info);

        //Анимация
        this.overridePendingTransition(R.anim.right_in, R.anim.left_out);

        //Получение переданных данных
        Intent intent = getIntent();
        ArtistClass artist = (ArtistClass)getIntent().getParcelableExtra("artist");

        //Устанавливаем заголовок в ActionBar
        setTitle(artist.getName());

        final ArtistHolder holder = new ArtistHolder();

        //Связываем элементы с полями
        holder.genresTv = (TextView)this.findViewById(R.id.genresTv);
        holder.bioTv = (TextView)this.findViewById(R.id.bioTv);
        holder.artInfoTv = (TextView)this.findViewById(R.id.artInfoTv);
        holder.coverIv = (ImageView)this.findViewById(R.id.coverIv);
        holder.linkTv = (TextView)this.findViewById(R.id.linkTv);

        final FrameLayout fm = (FrameLayout)this.findViewById(R.id.frameProgressBar);
        fmIv = (RelativeLayout)this.findViewById(R.id.frameImageView);
        zoomIv = (ImageView)this.findViewById(R.id.zoomImageView);

        //Заносим данные в элементы Layout'а
        holder.genresTv.setText(artist.getGenres());
        holder.bioTv.setText(artist.getDescription());
        String artInfo = artist.getAlbums() + " "+UniClass.getNumEnding(artist.getAlbums(),UniClass.endingAlbum)+" · " + artist.getTracks() + " " + UniClass.getNumEnding(artist.getTracks(),UniClass.endingTrack);
        holder.artInfoTv.setText(artInfo);
        if (artist.getLink() != null) {
            String link = "Сайт: " + artist.getLink();
            holder.linkTv.setText(link);
            Pattern pattern = Pattern.compile(artist.getLink());
            Linkify.addLinks(holder.linkTv, pattern, "http://");
        }
        else {
            holder.linkTv.setText("Сайт не указан");
        }


        ImageLoader imageLoader = ImageLoader.getInstance();

        //Загружаем изображение и прячем Layout с ProgressBar'ом
        imageLoader.loadImage(artist.getBigCover(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.coverIv.setImageBitmap(loadedImage);
                fm.setVisibility(View.GONE);
            }
        });

        //Событие зуммирования изображения
        holder.coverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomIv.setImageDrawable(holder.coverIv.getDrawable());

                Drawable bitmap = holder.coverIv.getDrawable();
                zoomIv.setImageDrawable(bitmap);
                mAttacher = new PhotoViewAttacher(zoomIv);

                isZoomImage = true;

                fmIv.setVisibility(View.VISIBLE);
            }
        });

    }

    //Переопределение кнопки Back
    @Override
    public void onBackPressed() {
        //Если изображение было зуммировано, то прячем Layout с изображением иначе переходив в прерыдущее Activity
        if (isZoomImage) {
            isZoomImage = false;
            fmIv.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Обработка кнопки Back
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
