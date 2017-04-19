package com.scmspain.dao;

import com.scmspain.entities.Tweet;

import java.util.List;

public interface ITweetDao {

    String QRY_FILTER_NODISCARD_DESCORDER = "SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND discardDate IS NULL ORDER BY id DESC";
    String QRY_FILTER_DISCARD_DESCORDER = "SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND discardDate <> NULL ORDER BY discardDate DESC";
    String QRY_UPDATE_TWEET_DISCARDDATE = "UPDATE Tweet SET discardDate = :now WHERE id = :id";

    /**
     * Return list of ids from queryString
     *
     * @param queryString
     * @return
     */
    List<Long> idListFromQuery(String queryString);

    /**
     * Get tweet by id.
     *
     * @param id Long
     * @return Tweet
     */
    Tweet getTweetFromId(Long id);

    /**
     * Update discard date of tweet.
     *
     * @param tweet to be updated.
     */
    void updateTweetDiscardDate(Tweet tweet);

    /**
     * Add tweet.
     *
     * @param tweet to addTweet.
     */
    void addTweet(Tweet tweet);

    /**
     * Delete all tweets.
     *
     */
    void deleteAllTweet();
}
