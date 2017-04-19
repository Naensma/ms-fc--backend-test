package com.scmspain.dao;

import com.scmspain.entities.Tweet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class H2TweetDao implements ITweetDao{
    private EntityManager entityManager;

    public H2TweetDao(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Tweet getTweetFromId(Long id) {
        return entityManager.find(Tweet.class, id);
    }

    @Override
    @Transactional
    public void updateTweetDiscardDate(Tweet tweet) {
        entityManager.createQuery(QRY_UPDATE_TWEET_DISCARDDATE)
                .setParameter("id", tweet.getId())
                .setParameter("now", tweet.getDiscardDate())
                .executeUpdate();
    }

    @Override
    @Transactional
    public void addTweet(Tweet tweet) {
        entityManager.persist(tweet);
    }

    @Override
    public List<Long> idListFromQuery(String queryString) {
        TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
        return query.getResultList();
    }

    @Transactional
    public void deleteAllTweet(){
        entityManager.createQuery("DELETE FROM Tweet").executeUpdate();
    }
}
