package ua.kpi.comsys.io8302.app_mobiles.ui.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ua.kpi.comsys.io8302.app_mobiles.R;


public class NotificationsFragment extends Fragment {

    private static final String KEY = "7e9fe69e";

    private static HashMap<ConstraintLayout, Movie> moviesMap;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout moviesList;
    @SuppressLint("StaticFieldLeak")
    protected static View root;
    @SuppressLint("StaticFieldLeak")
    private static TextView emptyResultsView;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar progressBar;
    private static Set<ConstraintLayout> tempSet;


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notifications, container, false);

        setRetainInstance(true);

        moviesList = root.findViewById(R.id.scroll_lay);
        moviesMap = new HashMap<>();
        SearchView simpleSearchView = root.findViewById(R.id.search_view);
        emptyResultsView = root.findViewById(R.id.no_movies_view);
        progressBar = root.findViewById(R.id.progressBar);

        tempSet = new HashSet<>();

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                tempSet.addAll(moviesMap.keySet());

                if (s.length() >= 3) {
                    AsyncLoadMovies aTask = new AsyncLoadMovies();
                    emptyResultsView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
                } else {
                    for (ConstraintLayout constraintLayout : tempSet) {
                        binClicked(constraintLayout);
                    }
                    tempSet.clear();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                tempSet.addAll(moviesMap.keySet());

                if (s.length() >= 3) {
                    AsyncLoadMovies aTask = new AsyncLoadMovies();
                    emptyResultsView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
                } else {
                    for (ConstraintLayout constraintLayout : tempSet) {
                        binClicked(constraintLayout);
                    }
                    tempSet.clear();
                }

                return false;
            }
        });

        changeLaySizes();

        return root;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLaySizes();
    }

    private void changeLaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (ConstraintLayout movieshelf :
                moviesMap.keySet()) {
            movieshelf.getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(width/3, width/3));
        }
    }

    public static void binClicked(SwipeLayout swipeLayout){
        moviesMap.remove(swipeLayout);
        moviesList.removeView(swipeLayout);
    }

    private static void binClicked(ConstraintLayout key){
        moviesMap.remove(key);

        moviesList.removeView(((SwipeLayout) key.getParent()));
        if (moviesMap.keySet().isEmpty()){
            emptyResultsView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected static void loadMovies(ArrayList<Movie> movies) {
        if (movies != null) {
            if (movies.size() > 0) {
                emptyResultsView.setVisibility(View.GONE);
                for (Movie movie : movies) {
                    addNewMovie(root, moviesList, movie);
                }
                for (ConstraintLayout constraintLayout : tempSet) {
                    binClicked(constraintLayout);
                }
                tempSet.clear();
            } else {
                emptyResultsView.setVisibility(View.VISIBLE);
                for (ConstraintLayout constraintLayout : tempSet) {
                    binClicked(constraintLayout);
                }
                tempSet.clear();
            }
        } else {
            Toast.makeText(root.getContext(),
                    "Unable to load data for some reason (check Internet connection)",
                    Toast.LENGTH_LONG).show();
        }

        progressBar.setVisibility(View.GONE);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void addNewMovie(View root, LinearLayout movieList, Movie movie){
        SwipeLayout swipeLay = new SwipeLayout(root.getContext());
        swipeLay.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLay.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
        movieList.addView(swipeLay);

        ImageButton btnBin = new ImageButton(root.getContext());
        btnBin.setPadding(50, 0, 50, 0);
        btnBin.setBackgroundColor(Color.RED);
        btnBin.setImageResource(R.drawable.ic_delete_white_48dp);

        LinearLayout.LayoutParams btnBinParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        btnBinParams.gravity = Gravity.RIGHT;
        swipeLay.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLay.addView(btnBin, 0, btnBinParams);

        ConstraintLayout movieLayTmp = new ConstraintLayout(root.getContext());
        movieLayTmp.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        swipeLay.addView(movieLayTmp);

        btnBin.setOnClickListener(v -> binClicked(swipeLay));
        movieLayTmp.setOnClickListener(v -> {
            if (movie.getImdbID().length() != 0 && !movie.getImdbID().equals("noid")) {
                MovieInfo popUpClass = new MovieInfo();
                popUpClass.showPopupWindow(v, movie);
            }
        });

        ProgressBar loadingImageBar = new ProgressBar(root.getContext());
        loadingImageBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(root.getContext(), R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingImageBar.setVisibility(View.GONE);
        loadingImageBar.setId(loadingImageBar.hashCode());
        movieLayTmp.addView(loadingImageBar);

        ImageView imageTmp = new ImageView(root.getContext());
        imageTmp.setId(imageTmp.hashCode());
        ConstraintLayout.LayoutParams imgParams =
                new ConstraintLayout.LayoutParams(300, 300);
        imageTmp.setImageResource(android.R.drawable.ic_media_play);
        if (movie.getPoster().length() != 0) {
            imageTmp.setVisibility(View.INVISIBLE);
            loadingImageBar.setVisibility(View.VISIBLE);
            new DownloadImageTask(imageTmp, loadingImageBar, root.getContext()).execute(movie.getPoster());
        }

        movieLayTmp.addView(imageTmp, 0, imgParams);

        ConstraintLayout textConstraint = new ConstraintLayout(root.getContext());
        textConstraint.setId(textConstraint.hashCode());
        movieLayTmp.addView(textConstraint, 1);

        TextView textTitle = new TextView(root.getContext());
        textTitle.setId(textTitle.hashCode());
        textTitle.setPadding(0, 1, 5, 1);
        textTitle.setText(movie.getTitle());
        ConstraintLayout.LayoutParams textTitleParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textConstraint.addView(textTitle, 0, textTitleParams);

        TextView textYear = new TextView(root.getContext());
        textYear.setText(movie.getYear());
        textYear.setPadding(0, 1, 5, 1);
        textYear.setId(textYear.hashCode());
        ConstraintLayout.LayoutParams textYearParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textConstraint.addView(textYear, 1, textYearParams);

        TextView textType = new TextView(root.getContext());
        textType.setText(movie.getType());
        textType.setPadding(0, 0, 5, 4);
        textType.setId(textType.hashCode());
        ConstraintLayout.LayoutParams textTypeParams =
                new ConstraintLayout.LayoutParams(ConstraintSet.WRAP_CONTENT,
                        ConstraintSet.WRAP_CONTENT);
        textConstraint.addView(textType, 2, textTypeParams);

        ConstraintSet textConstraintSet = new ConstraintSet();
        textConstraintSet.clone(textConstraint);

        textConstraintSet.connect(textTitle.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        textConstraintSet.connect(textTitle.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        textConstraintSet.connect(textYear.getId(), ConstraintSet.TOP,
                textTitle.getId(), ConstraintSet.BOTTOM);
        textConstraintSet.connect(textYear.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        textConstraintSet.connect(textYear.getId(), ConstraintSet.BOTTOM,
                textType.getId(), ConstraintSet.TOP);
        textConstraintSet.connect(textType.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        textConstraintSet.connect(textType.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);

        textConstraintSet.setVerticalBias(textYear.getId(), 0.3f);

        textConstraintSet.setMargin(textYear.getId(), ConstraintSet.TOP, 3);
        textConstraintSet.setMargin(textYear.getId(), ConstraintSet.BOTTOM, 3);

        textConstraintSet.applyTo(textConstraint);

        ConstraintSet movieLayTmpSet = new ConstraintSet();
        movieLayTmpSet.clone(movieLayTmp);

        movieLayTmpSet.connect(imageTmp.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        movieLayTmpSet.connect(imageTmp.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        movieLayTmpSet.connect(imageTmp.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        movieLayTmpSet.connect(textConstraint.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        movieLayTmpSet.connect(textConstraint.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        movieLayTmpSet.connect(textConstraint.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        movieLayTmpSet.connect(textConstraint.getId(), ConstraintSet.START,
                imageTmp.getId(), ConstraintSet.END);

        movieLayTmpSet.constrainWidth(textConstraint.getId(), ConstraintSet.MATCH_CONSTRAINT);
        movieLayTmpSet.constrainHeight(textConstraint.getId(), ConstraintSet.MATCH_CONSTRAINT);

        movieLayTmpSet.applyTo(movieLayTmp);

        moviesMap.put(movieLayTmp, movie);
    }

    private static class AsyncLoadMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            return search(strings[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            NotificationsFragment.loadMovies(movies);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private ArrayList<Movie> search(String newText){
            String jsonUrl = String.format("http://www.omdbapi.com/?apikey=%s&s=%s&page=1", KEY,
                    newText.trim().replace("\\s+", "+"));
            try {
                ArrayList<Movie> movies = parseMovies(getRequest(jsonUrl));
                return movies;
            } catch (ParseException e) {
                System.err.println("Some trouble occurred when parsing Json file.");
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Movie> parseMovies(String jsonText) throws ParseException {
            ArrayList<Movie> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray movies = (JSONArray) jsonObject.get("Search");

            if (movies != null) {
                for (Object movie : movies) {
                    JSONObject tmp = (JSONObject) movie;
                    result.add(new Movie(
                            (String) tmp.get("Title"),
                            (String) tmp.get("Year"),
                            (String) tmp.get("imdbID"),
                            (String) tmp.get("Type"),
                            (String) tmp.get("Poster")
                    ));
                }
            }

            return result;
        }

        private String getRequest(String url){
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


    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;
        @SuppressLint("StaticFieldLeak")
        ProgressBar loadingBar;
        @SuppressLint("StaticFieldLeak")
        Context context;

        public DownloadImageTask(ImageView bmImage, ProgressBar loadingBar, Context context) {
            this.bmImage = bmImage;
            this.loadingBar = loadingBar;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }

            loadingBar.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}