package yandexapp.agentxxx.com.yandexapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ArrayList<ArtistClass> artists = new ArrayList<>(); //Тут будем хранить всю информацию о артистах/группах
    String fileName = "cache_file.ser";//Имя файла, где будет храниться копия json-документа в случае, если соединени не доступно
    JSONObject mainObject = null; //JSONObject сформированный из полученного json-докемента
    String url_s = "http://download.cdn.yandex.net/mobilization-2016/artists.json"; //Ссылка на JSON-документ

    //Используем для проверки состояния сети
    ConnectivityManager conMgr;
    NetworkInfo activeNetwork;

    ListView lv; //Список
    FloatingActionButton checkBtn; //Ппавающая кнопка обновления соединения (в случае, если не доступна сеть)
    FrameLayout fc; //Layout отвечающий за ситуацию, когда у нас первый запуск приложения и сеть отсутствует
    Adapter adapter; //Adapter для ListView (lv)



    @Override
    public void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fc = (FrameLayout)this.findViewById(R.id.frameConnect); //Связываем переменную с Layout'ом

        File file = getApplicationContext().getFileStreamPath(fileName); //Пытаемся получить файл

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Получаем данные о сети

        //Создаем плавающую кнопку
        checkBtn = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_menu_refresh))
                .withButtonColor(Color.parseColor("#ff6a109f"))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withButtonSize(72)
                .withMargins(0, 0, 16, 16)
                .create();


        //Добавляем к ней событие
        checkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setAlpha(1.0f);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(0.6f);


                    if (checkInternetConnection()) {

                        checkBtn.hideFloatingActionButton();
                        setTitle(R.string.main_name_activity);

                        //Если это первая загрузка и файла кэша нет - снова пытаемся получить данные из сети
                        if (mainObject == null) {
                            findViewById(R.id.connectError).setVisibility(View.GONE);
                            findViewById(R.id.checkBar).setVisibility(View.VISIBLE);
                            new ParseTask().execute();
                        }
                    }
                    else {
                        checkBtn.showFloatingActionButton();
                        findViewById(R.id.connectError).setVisibility(View.VISIBLE);
                        findViewById(R.id.checkBar).setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.connect_error, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

        String json = "{\"artists\":[{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\",\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\",\"description\":\"шведская певица и автор песен. Она привлекла к себе внимание в 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом хип-хоп продюсера Hippie Sabotage на эту песню, который получил название «Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24 сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/1000x1000\"}},{\"id\":2915,\"name\":\"Ne-Yo\",\"genres\":[\"rnb\",\"pop\",\"rap\"],\"tracks\":256,\"albums\":152,\"link\":\"http://www.neyothegentleman.com/\",\"description\":\"обладатель трёх премии Грэмми, американский певец, автор песен, продюсер, актёр, филантроп. В 2009 году журнал Billboard поставил Ни-Йо на 57 место в рейтинге «Артисты десятилетия».\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/15ae00fc.p.2915/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/15ae00fc.p.2915/1000x1000\"}}]}";

        //Проверка соединения
        if (checkInternetConnection()) {
            checkBtn.hideFloatingActionButton();
            findViewById(R.id.connectError).setVisibility(View.GONE);
            findViewById(R.id.checkBar).setVisibility(View.VISIBLE);
            //Если соединение есть и файл существует, то загружаем кэш, а потом проверяем, обновился ли файл в сети
            //Если да - то загражаем новые данные и обновляем локальный файл
            if(file.exists()) {

                try {

                    mainObject = new JSONObject((String)RepositoryClass.readFromFile(getApplicationContext(),fileName));
                    listViewFormatted();    //Формируем ListView

                } catch (JSONException e) {
                    //Ошибка в формировании JSON-объекта. В таком случае обнуляем объект, удаляем файл и с помощью new ParseTask().execute(); пытаемся загрузить файл из сети
                    mainObject = null;
                    file.delete();
                    e.printStackTrace();
                }
                catch (Exception e) {
                    //Неизвестная ошибка
                    e.printStackTrace();
                    return;
                }
                new ParseTask().execute();  //Загружаем json-документ из сети и обрабатываем его
            }
            else {
                new ParseTask().execute();
            }

        } else {
            //Соединения нет, то файл существует. В таком случае загруажем кэш и отображаем кнопку обновления сети
            if (file.exists()) {

                fc.setVisibility(View.GONE);
                setTitle(R.string.offline_mode);
                Toast.makeText(getApplicationContext(), R.string.connect_error, Toast.LENGTH_SHORT).show();

                try {
                    mainObject = new JSONObject((String) RepositoryClass.readFromFile(getApplicationContext(), fileName));
                    listViewFormatted();
                } catch (JSONException e) {
                    //Ошибка в формировании JSON-объекта. В таком случае обнуляем объект, удаляем файл и сообщаем об отсутствии сети
                    mainObject = null;
                    file.delete();

                    setTitle(R.string.main_name_activity);
                    fc.setVisibility(View.VISIBLE);

                    e.printStackTrace();
                }
                catch (Exception e) {
                    //Неизвестная ошибка
                    e.printStackTrace();
                    return;
                }
            }
            else {
                //Иначе показываем сообщение о том, что соединения нет
                Toast.makeText(getApplicationContext(), R.string.connect_error, Toast.LENGTH_LONG).show();
            }

        }

    }

    //Проверяем соединение
    private boolean checkInternetConnection() {
        activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //Метод формирования ListView
    public void listViewFormatted() {
        try {
            artists.clear();
            
            JSONArray artistArr = mainObject.getJSONArray("artists");

            //Заполняем поля класса из полученных данных
            for (int i=0; i<artistArr.length(); i++) {
                artists.add(new ArtistClass(artistArr.getJSONObject(i)));
            }

            lv = (ListView)findViewById(R.id.listView);


            //Подключаем адаптер и передаём данные
            adapter = new Adapter(this, artists);
            lv.setAdapter(adapter);
            fc.setVisibility(View.GONE);
            //Устанавливаем события на клик по item'у ListView
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
                if (checkInternetConnection()) {
                    Intent myIntent = new Intent(MainActivity.this, ArtistInfoActivity.class);
                    myIntent.putExtra("artist", artists.get(pos));
                    startActivity(myIntent);
                }
                else {
                    checkBtn.showFloatingActionButton();
                    Toast.makeText(getApplicationContext(), R.string.connect_error, Toast.LENGTH_LONG).show();
                }
                }
            });

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        // получаем данные с внешнего ресурса в другом потоке
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(url_s);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            return resultJson;
        }

        //Обрабатываем полученные данные
        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            try {
                //Проверяем, были ли загружены кэш-данные
                if (mainObject != null) {
                    JSONObject mainObjectTmp = new JSONObject("{\"artists\":" + strJson + "}");

                    //Сверяем текущий кэш с новыми данными. Если они не совпадают, то обновляем переменную
                    if (!compareJSONFiles(mainObject,mainObjectTmp)) {
                        Toast.makeText(getApplicationContext(), R.string.date_upload, Toast.LENGTH_LONG).show();
                        mainObject = new JSONObject("{\"artists\":" + strJson + "}");
                        RepositoryClass.saveToFile(getApplicationContext(),fileName,mainObject.toString());
                        listViewFormatted();
                    }
                }
                else {
                    mainObject = new JSONObject("{\"artists\":" + strJson + "}");
                    RepositoryClass.saveToFile(getApplicationContext(),fileName,mainObject.toString());
                    listViewFormatted();
                }

            } catch (JSONException e) {
                //Полученные данные имеют не верный формат
                Toast.makeText(getApplicationContext(), R.string.request_error, Toast.LENGTH_LONG).show();
                mainObject = null;
                checkBtn.showFloatingActionButton();
                e.printStackTrace();
            } catch (Exception e) {
                //Неизвестная ошибка
                e.printStackTrace();
            }

        }

        //Сравнение файлов
        private boolean compareJSONFiles(JSONObject job1, JSONObject job2) {
            String aTmp =job1.toString();
            String bTmp =job2.toString();
            if (aTmp.hashCode() == bTmp.hashCode()) {
                return true;
            }
            else {
                return false;
            }
        }

    }
}
