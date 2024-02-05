import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Movie movie1 = new Movie("Inception", "Action", 4.5);
        Movie movie2 = new Movie("The Shawshank Redemption", "Drama", 4.8);
        Movie movie3 = new Movie("The Godfather", "Crime", 4.7);
        Movie movie4 = new Movie("The Dark Knight", "Action", 4.9);
        User user1 = new User("Alice");
        User user2 = new User("Bob");
        User user3 = new User("Charlie");
        user1.rateMovie(movie1, 5);
        user1.rateMovie(movie2, 4);
        user2.rateMovie(movie2, 5);
        user2.rateMovie(movie3, 4);
        user3.rateMovie(movie1, 5);
        user3.rateMovie(movie3, 4);
        user3.rateMovie(movie4, 4);
        System.out.println("Recommendations for " + user1.getUsername() + ":");
        List<Movie> recommendationsForUser1 = recommendMovies(user1, Arrays.asList(user2, user3));
        recommendationsForUser1.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println("Recommendations for " + user2.getUsername() + ":");
        List<Movie> recommendationsForUser2 = recommendMovies(user2, Arrays.asList(user1, user3));
        recommendationsForUser2.forEach(movie -> System.out.println(movie.getTitle()));
    }
    public static List<Movie> recommendMovies(User user, List<User> otherUsers) {
        Map<User, Double> similarityScores = new HashMap<>();
        for (User otherUser : otherUsers) {
            if (!otherUser.equals(user)) {
                double similarity = computeSimilarity(user, otherUser);
                similarityScores.put(otherUser, similarity);
            }
        }


        List<User> similarUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        Set<Movie> recommendedMovies = new HashSet<>();
        for (User similarUser : similarUsers) {
            Set<Movie> likedMovies = similarUser.getRatings().entrySet().stream()
                    .filter(entry -> entry.getValue() >= 4)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            likedMovies.removeAll(user.getRatings().keySet());
            recommendedMovies.addAll(likedMovies);
        }

        return new ArrayList<>(recommendedMovies);
    }
    private static double computeSimilarity(User user1, User user2) {
        Map<Movie, Integer> ratings1 = user1.getRatings();
        Map<Movie, Integer> ratings2 = user2.getRatings();
        Set<Movie> commonMovies = new HashSet<>(ratings1.keySet());
        commonMovies.retainAll(ratings2.keySet());
        double sumXY = 0;
        double sumX = 0;
        double sumY = 0;
        double sumX2 = 0;
        double sumY2 = 0;
        int n = commonMovies.size();

        for (Movie movie : commonMovies) {
            int rating1 = ratings1.get(movie);
            int rating2 = ratings2.get(movie);

            sumXY += rating1 * rating2;
            sumX += rating1;
            sumY += rating2;
            sumX2 += rating1 * rating1;
            sumY2 += rating2 * rating2;
        }

        double numerator = sumXY - (sumX * sumY / n);
        double denominator = Math.sqrt((sumX2 - sumX * sumX / n) * (sumY2 - sumY * sumY / n));
        if (denominator == 0) {
            return 0;
        }
        return numerator / denominator;
    }
}

class Movie {
    private String title;
    private String genre;
    private double rating;

    public Movie(String title, String genre, double rating) {
        this.title = title;
        this.genre = genre;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }
}

class User {
    private String username;
    private Map<Movie, Integer> ratings;

    public User(String username) {
        this.username = username;
        this.ratings = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void rateMovie(Movie movie, int rating) {
        ratings.put(movie, rating);
    }

    public Map<Movie, Integer> getRatings() {
        return ratings;
    }
}
