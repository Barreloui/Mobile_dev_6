package ua.kpi.comsys.io8302.app_mobiles.ui.notifications;

public class Movie {
    private String title;
    private String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String rating;
    private String votes;
    private String production;
    private String imdbID;
    private String type;
    private String poster;

    public Movie(String title, String year, String imdbID, String type, String poster){
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.poster = poster;
    }

    public void addInfo(String rated, String released, String runtime,
                        String genre, String director, String writer,
                        String actors, String plot, String language,
                        String country, String awards, String rating,
                        String votes, String production){
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.rating = rating;
        this.votes = votes;
        this.production = production;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public String getType() {
        return type;
    }

    public String getRating() {
        return rating;
    }

    public String getActors() {
        return actors;
    }

    public String getAwards() {
        return awards;
    }

    public String getCountry() {
        return country;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public String getPlot() {
        return plot;
    }

    public String getProduction() {
        return production;
    }

    public String getRated() {
        return rated;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getVotes() {
        return votes;
    }

    public String getWriter() {
        return writer;
    }
}
