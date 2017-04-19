package com.scmspain.services;

import com.scmspain.common.TestUtils;
import com.scmspain.dao.ITweetDao;
import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class TweetServiceTest {
    private ITweetDao tweetDao;
    private TweetService tweetService;
    private Tweet tweet;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MetricWriter metricWriter = mock(MetricWriter.class);
        List<Long> idList = new ArrayList<>();
        idList.add(1L);

        this.tweetDao = mock(ITweetDao.class);
        this.tweet = mock(Tweet.class);
        this.tweetService = new TweetService(tweetDao, metricWriter);

        when(tweetDao.getTweetFromId(any(Long.class))).thenReturn(tweet);
        when(tweetDao.idListFromQuery(any(String.class))).thenReturn(idList);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME, TestUtils.VALID_TWEET_TEXT);

        verify(tweetDao).addTweet(any(Tweet.class));
    }

    @Test
    public void shouldInsertANewTweetWithoutCountingHyperlink() throws Exception {
        String tweetText = TestUtils.VALID_MAX_TWEET_TEXT + TestUtils.HTTP;
        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME, tweetText);

        verify(tweetDao).addTweet(any(Tweet.class));
    }

    @Test
    public void shouldInsertANewTweetWithoutCountingSecureHyperlink() throws Exception {
        String tweetText = TestUtils.VALID_MAX_TWEET_TEXT + TestUtils.HTTPS;
        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME, tweetText);

        verify(tweetDao).addTweet(any(Tweet.class));
    }

    @Test
    public void shouldThrowAnExceptionWithMsgWhenTweetLengthIsZero() {
        expectMessage("Tweet can't be empty");

        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME, TestUtils.EMPTY_STRING);
    }

    @Test
    public void shouldThrowAnExceptionWithMsgWhenTweetLengthIsInvalid() throws Exception {
        expectMessage("Tweet must not be greater than 140 characters");

        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME,
                "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. " +
                        "He fell for her in a big way, but she told him to drop dead. So he did. " +
                        "Then things really got ugly.");
    }

    @Test
    public void shouldThrowAnExceptionWithMsgWhenPublisherLengthIsZero() {
        expectMessage("Publisher name can't be empty");

        tweetService.publishTweet(TestUtils.EMPTY_STRING, TestUtils.EMPTY_STRING);
    }

    @Test
    public void shouldDiscardTweet() {
        final Long id = 1L;
        tweetService.publishTweet(TestUtils.VALID_PUBLISHER_NAME, TestUtils.VALID_TWEET_TEXT);
        tweetService.updateTweetDiscardDate(id);

        verify(tweetDao).getTweetFromId(id);
        verify(tweet).setDiscardDate(any(Date.class));
        verify(tweetDao).updateTweetDiscardDate(tweet);
    }

    @Test
    public void shouldReturnTweetList() {
        List<Tweet> tweetList = tweetService.getTweetsList();

        assertListNCheckUserQry(tweetList, ITweetDao.QRY_FILTER_NODISCARD_DESCORDER);
    }

    @Test
    public void shouldReturnDiscardedTweetList() {
        List<Tweet> tweetList = tweetService.getDiscardedTweetsList();

        assertListNCheckUserQry(tweetList, ITweetDao.QRY_FILTER_DISCARD_DESCORDER);
    }

    private void assertListNCheckUserQry(List<Tweet> tweetList, String qryString) {
        assertThat(tweetList).contains(tweet);
        verify(tweetDao).idListFromQuery(qryString);
        verify(tweetDao).getTweetFromId(any(Long.class));
    }

    private void expectMessage(String message){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(message);
    }
}
