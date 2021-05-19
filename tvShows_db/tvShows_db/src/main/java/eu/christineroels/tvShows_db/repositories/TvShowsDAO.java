package eu.christineroels.tvShows_db.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TvShowsDAO {
    //Instantiate a MongoClient object to connect
    //to my database running on the localhost on the default port 27017
    private MongoClient getConnection(){
        return MongoClients.create();
    }

    //Create a MongoDatabase object to access the database I need
    private final MongoDatabase moviesDb = getConnection().getDatabase("MediasData");
    //Create a MongoCollection<Bson Doc> object to access the collection I need
    private final MongoCollection<Document> tvShowsColl = moviesDb.getCollection("tvShows");

    //---------------------------------COUNT----------------------------------------------------
    //Returns how many documents there is in the collection
    public long countDocuments(){
        return tvShowsColl.countDocuments();
    }
    //---------------------------------GET RANDOM------------------------------------------
    //Returns one to three (depends on value for 'howMany') random documents from the collection
    public List<Document> getRandomTvShows(int howMany){
        List<Document> randomTvShowsList = new ArrayList<>();
        //[{$sample: {size: howMany}}]
        tvShowsColl.aggregate(Collections.singletonList(
                Aggregates.sample(howMany)
                )
                ).forEach(randomTvShowsList::add);
        return  randomTvShowsList;
    }
    //---------------------------------GET ALL----------------------------------------------------
    //Returns as a list ascending by runtimes with a selection of fields, the entire catalog of available tvShows
    public List<Document> getAllDocumentsRuntimeAscending(){
        List<Document> allTvShowsDocs = new ArrayList<>();
        for (Document cursor: tvShowsColl.find().projection(
                // projection of name: 1, genres: 1, summary: 1, runtime: 1, "rating.average": 1
                new Document("name", 1)
                .append("genres",1)
                .append("summary",1)
                .append("runtime",1)
                .append("rating.average",1)
                //sorting by ascending runtimes
        ).sort(Sorts.ascending("runtime"))
             ) {
            allTvShowsDocs.add(cursor);
        }
        return allTvShowsDocs;
    }
    //Returns as a list descending by runtimes with a selection of fields, the entire catalog of available tvShows
    public List<Document> getAllDocumentsRuntimeDescending(){
        List<Document> allTvShowsDocs = new ArrayList<>();
        for (Document cursor: tvShowsColl.find().projection(
                // projection of name: 1, genres: 1, summary: 1, runtime: 1, "rating.average": 1
                Projections.fields(Projections.include("name", "genres","summary","runtime","rating.average"))
                //sorting by ascending runtimes
        ).sort(Sorts.descending("runtime"))
        ) {
            allTvShowsDocs.add(cursor);
        }
        return allTvShowsDocs;
    }
    //---------------------------------QUERY BY FILTER CRITERIA-------------------------------------------------
    //Returns a list of documents, based on a keyword (no case-sensitivity) to find in the summary fields
    public List<Document> getTvShowsByKeyword(String keyword){
        List<Document> allTvShowsDocsByKeyword = new ArrayList<>();
        for (Document cursor: tvShowsColl.find(
                //{summary: { $regex: /the keyword/ , $options: 'i'}}
                Filters.regex("summary",keyword,"i")
        )){
            allTvShowsDocsByKeyword.add(cursor);
        }
        return allTvShowsDocsByKeyword;
    }
    //Returns a list of documents, based on a range of runtime
    public List<Document> getTvShowsByRuntime(int lowerLimit, int upperLimit){
        List<Document> allTvShowsDocsByRuntime = new ArrayList<>();
        for (Document cursor: tvShowsColl.find(
            //db.tvShows.find({$and: [{runtime: {$lte: n}},{runtime: {$gte: n}}]}).sort({runtime: -1}).count()
            Filters.and(Filters.gte("runtime", lowerLimit), Filters.lte("runtime", upperLimit))
        ).sort(Sorts.descending("runtime"))){
            allTvShowsDocsByRuntime.add(cursor);
        }
        return allTvShowsDocsByRuntime;
    }
    //Returns a list of documents, based on a genre and the option for a mixed-genres show
    public List<Document> getTvShowsByGenres(String genre, int howManyOtherGenre){
        List<Document> allTvShowsDocsByGenres = new ArrayList<>();
        for (Document cursor: tvShowsColl.find(
                //db.tvShows.find({genres: {$eq: a genre of choice, $size: a number of choice}}).sort({id: 1})
            Filters.and(Filters.eq("genres", genre), Filters.size("genres", howManyOtherGenre))
        )){
            allTvShowsDocsByGenres.add(cursor);
        }
        return allTvShowsDocsByGenres;
    }
    //Returns a list of documents, based on every criteria at the disposition to the user
    public List<Document> getTvShowsByAllCriteria(String keyword, int runtimeLowerLimit,
                                                  int runtimeUpperLimit, String genre, int
                                                  howManyOtherGenre){
        List<Document> allTvShowsDocsByCriteria = new ArrayList<>();
        for (Document cursor: tvShowsColl.find(
                Filters.and(
                        //keyword
                        Filters.regex("summary",keyword,"i"),
                        //runtime
                        Filters.gte("runtime", runtimeLowerLimit),
                        Filters.lte("runtime", runtimeUpperLimit),
                        //genres
                        Filters.eq("genres", genre),
                        Filters.size("genres", howManyOtherGenre)
                ))){
            allTvShowsDocsByCriteria.add(cursor);
        }
        return allTvShowsDocsByCriteria;
    }

}
