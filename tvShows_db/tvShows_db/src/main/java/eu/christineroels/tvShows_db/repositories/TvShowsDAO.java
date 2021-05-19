package eu.christineroels.tvShows_db.repositories;

import org.bson.Document;

import java.util.List;

public interface TvShowsDAO {
    long countDocuments();
    List<Document> getRandomTvShows(int howMany);
    List<Document> getAllDocumentsRuntimeAscending();
    List<Document> getAllDocumentsRuntimeDescending();
    List<Document> getTvShowsByKeyword(String keyword);
    List<Document> getTvShowsByRuntime(int lowerLimit, int upperLimit);
    List<Document> getTvShowsByGenres(String genre, int howManyOtherGenre);
    List<Document> getTvShowsByAllCriteria(String keyword, int runtimeLowerLimit,
                                           int runtimeUpperLimit, String genre, int
                                                   howManyOtherGenre);
}
