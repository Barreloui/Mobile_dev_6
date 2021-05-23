package ua.kpi.comsys.io8302.app_mobiles.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ua.kpi.comsys.io8302.app_mobiles.R;


public class MovieInfo {

    private static final String KEY = "7e9fe69e";

    @SuppressLint("StaticFieldLeak")
    private static View popupView;

    @SuppressLint("StaticFieldLeak")
    private static ProgressBar detailsProgressBar;

    @SuppressLint("StaticFieldLeak")
    private static ImageView poster;

    private static Movie movie;


    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    public void showPopupWindow(final View view, Movie movie) {

        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupView = inflater.inflate(R.layout.popup_movie_info, null);

        detailsProgressBar = popupView.findViewById(R.id.progress_bar_details);
        poster = popupView.findViewById(R.id.movie_info_image);

        MovieInfo.movie = movie;

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        AsyncLoadMovieDetails aTask = new AsyncLoadMovieDetails();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getImdbID());
    }

    protected static void setInfoData(){
        poster.setVisibility(View.INVISIBLE);
        detailsProgressBar.setVisibility(View.VISIBLE);
        new NotificationsFragment.DownloadImageTask(poster, detailsProgressBar,
                popupView.getContext()).execute(movie.getPoster());

        ((TextView) popupView.findViewById(R.id.movie_info_title))      .setText(movie.getTitle());
        ((TextView) popupView.findViewById(R.id.movie_info_year))       .setText(movie.getYear());
        ((TextView) popupView.findViewById(R.id.movie_info_released))   .setText(movie.getReleased());
        ((TextView) popupView.findViewById(R.id.movie_info_runtime))    .setText(movie.getRuntime());
        ((TextView) popupView.findViewById(R.id.movie_info_genre))      .setText(movie.getGenre());
        ((TextView) popupView.findViewById(R.id.movie_info_director))   .setText(movie.getDirector());
        ((TextView) popupView.findViewById(R.id.movie_info_actors))     .setText(movie.getActors());
        ((TextView) popupView.findViewById(R.id.movie_info_plot))       .setText(movie.getPlot());
        ((TextView) popupView.findViewById(R.id.movie_info_language))   .setText(movie.getLanguage());
        ((TextView) popupView.findViewById(R.id.movie_info_country))    .setText(movie.getCountry());
        ((TextView) popupView.findViewById(R.id.movie_info_awards))     .setText(movie.getAwards());
        ((TextView) popupView.findViewById(R.id.movie_info_rating))     .setText(movie.getRating());
        ((TextView) popupView.findViewById(R.id.movie_info_production)) .setText(movie.getProduction());
    }


    private static class AsyncLoadMovieDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MovieInfo.setInfoData();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(String... strings) {
            search(strings[0]);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void search(String imdbID) {
            String jsonUrl = String.format("http://www.omdbapi.com/?apikey=%s&i=%s", KEY, imdbID.trim());
            try {
                parseMovieInfo(getRequest(jsonUrl), movie);
            } catch (ParseException e) {
                System.err.println("Some trouble occurred when parsing Json file.");
                e.printStackTrace();
            }
        }

        private void parseMovieInfo(String jsonText, Movie movie) throws ParseException {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);
            movie.addInfo((String) jsonObject.get("Rated"),
                    (String) jsonObject.get("Released"),
                    (String) jsonObject.get("Runtime"),
                    (String) jsonObject.get("Genre"),
                    (String) jsonObject.get("Director"),
                    (String) jsonObject.get("Writer"),
                    (String) jsonObject.get("Actors"),
                    (String) jsonObject.get("Plot"),
                    (String) jsonObject.get("Language"),
                    (String) jsonObject.get("Country"),
                    (String) jsonObject.get("Awards"),
                    (String) jsonObject.get("imdbRating"),
                    (String) jsonObject.get("imdbVotes"),
                    (String) jsonObject.get("Production"));
        }

        private String getRequest(String url) {
            StringBuilder result = new StringBuilder();

            try {
                URL getReq = new URL(url);
                URLConnection connection = getReq.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();

            } catch (MalformedURLException e) {
                System.err.println(String.format("Malformed URL: <%s>!", url));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }
    }
}

