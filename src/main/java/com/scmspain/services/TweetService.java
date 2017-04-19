package com.scmspain.services;

import com.scmspain.dao.ITweetDao;
import com.scmspain.entities.Tweet;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TweetService {
    private ITweetDao tweetDao;
    private MetricWriter metricWriter;

    /**
     * Default constructor
     *
     * @param tweetDao implementation of ITweetDao
     * @param metricWriter implementation of MetricWriter
     */
    public TweetService(ITweetDao tweetDao, MetricWriter metricWriter) {
        this.tweetDao = tweetDao;
        this.metricWriter = metricWriter;
    }

    /**
     * Push tweet to repository
     *
     * @param publisher Creator of the Tweet
     * @param text Content of the Tweet
     */
    public void publishTweet(String publisher, String text) {
        if (isValidPublisherAndText(publisher, text)) {
            Tweet tweet = new Tweet();
            tweet.setTweet(text);
            tweet.setPublisher(publisher);

            this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
            tweetDao.addTweet(tweet);
        }
    }

    /**
     * Gets tweet by id and updated discard date.
     *
     * @param id Long
     */
    public void updateTweetDiscardDate(Long id) {
        Tweet tweet = tweetDao.getTweetFromId(id);
        if (tweet != null){
            tweet.setDiscardDate(new Date());
            tweetDao.updateTweetDiscardDate(tweet);
        }
    }

    /**
     * Check validity of publisher and text
     *
     * @param publisher Creator of the Tweet
     * @param text Content of the Tweet
     * @return true if valid
     */
    private boolean isValidPublisherAndText(String publisher, String text) {
        return isValidPublisher(publisher) && isValidText(text);
    }

    /**
     * Looks for Hyperlink and returns text without it for tweet length purposes
     *
     * @param text Content of the Tweet
     * @return text without the hyperlink
     */
    private String removeHyperlink(String text) {
        final String URL_PATTERN = "(http://|https://).*?\\s";
        return text.replaceAll(URL_PATTERN, "");
    }

    /**
     * Check validity of text
     *
     * @param publisher Creator of the Tweet
     * @return true if valid
     */
    private boolean isValidPublisher(String publisher) {
        if(publisher == null || publisher.length() <= 0){
            throw new IllegalArgumentException("Publisher name can't be empty");
        }
        return true;
    }

    /**
     * Check validity of text
     *
     * @param text Content of the Tweet
     * @return true if valid
     */
    private boolean isValidText(String text) {
        if(text == null || text.length() <= 0){
            throw new IllegalArgumentException("Tweet can't be empty");
        }
        if(removeHyperlink(text).length() > 140){
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }
        return true;
    }

    /**
     * Recover tweet from repository
     *
     * @return retrieved Tweet
     */
    public List<Tweet> getTweetsList() {
        return getTweetsListFromQry(ITweetDao.QRY_FILTER_NODISCARD_DESCORDER);
    }

    /**
     * Recover discarded tweet from repository
     *
     * @return retrieved Tweet
     */
    public List<Tweet> getDiscardedTweetsList() {
        return getTweetsListFromQry(ITweetDao.QRY_FILTER_DISCARD_DESCORDER);
    }

    /**
     * Deletes all tweets
     *
     */
    public void deleteAllTweets() {
        tweetDao.deleteAllTweet();
    }

    /**
     * Creates List of Tweets from qryString
     *
     * @param qryString String
     * @return List of Tweet
     */
    private List<Tweet> getTweetsListFromQry(String qryString) {
        List<Tweet> result = new ArrayList<>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        List<Long> ids = tweetDao.idListFromQuery(qryString);
        for (Long id : ids) {
            result.add(tweetDao.getTweetFromId(id));
        }
        return result;
    }
}
