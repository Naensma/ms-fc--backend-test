package com.scmspain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.common.TestUtils;
import com.scmspain.configuration.TestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @After
    public void tearDown() throws Exception {
        mockMvc.perform(deleteAllTweet());
    }

    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        addTweetWithTextAndExpect(TestUtils.VALID_TWEET_TEXT, CREATED.value());
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        addTweetWithTextAndExpect(TestUtils.INVALID_LENGTH_TWEET_TEXT, BAD_REQUEST.value());
    }

    @Test
    public void shouldReturnAllPublishedTweetsInDescOrderWithoutDiscarded() throws Exception {
        addTwoTweets();
        discardTweet(1L);

        List contentList = getListFromEndpoint("/tweet");
        int firstId = (int) ((Map) contentList.get(0)).get("id");

        int expectedId = 5;
        assertThat(contentList.size()).isEqualTo(2);
        assertThat(firstId).isEqualTo(expectedId);
    }

    @Test
    public void shouldReturnAllDiscardedTweetsInDescOrder() throws Exception {
        addTwoTweets();
        discardTweet(1L);
        discardTweet(2L);

        List contentList = getListFromEndpoint("/discardedList");
        int firstId = (int) ((Map) contentList.get(0)).get("id");

        int expectedId = 2;
        assertThat(contentList.size()).isEqualTo(1);
        assertThat(firstId).isEqualTo(expectedId);
    }

    private MockHttpServletRequestBuilder newTweetRequest(String publisher, String tweet) {
        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private MockHttpServletRequestBuilder discardTweetRequest(Long id) {
        return delete("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"id\": \"%d\"}", id));
    }

    private MockHttpServletRequestBuilder deleteAllTweet() {
        return delete("/deleteAll");
    }

    private List getListFromEndpoint(String endpoint) throws Exception {
        String content = mockMvc.perform(get(endpoint))
                .andExpect(status().is(OK.value()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return new ObjectMapper().readValue(content, List.class);
    }

    private void addTwoTweets() throws Exception {
        mockMvc.perform(newTweetRequest(TestUtils.VALID_PUBLISHER_NAME, TestUtils.VALID_TWEET_TEXT))
                .andExpect(status().is(CREATED.value()));
        mockMvc.perform(newTweetRequest(TestUtils.VALID_PUBLISHER_NAME, TestUtils.VALID_TWEET_TEXT))
                .andExpect(status().is(CREATED.value()));
    }

    private void addTweetWithTextAndExpect(String validTweetText, int value) throws Exception {
        mockMvc.perform(newTweetRequest(TestUtils.VALID_PUBLISHER_NAME, validTweetText))
                .andExpect(status().is(value));
    }

    private void discardTweet(Long id) throws Exception {
        mockMvc.perform(discardTweetRequest(id)).andExpect(status().is(NO_CONTENT.value()));
    }
}
