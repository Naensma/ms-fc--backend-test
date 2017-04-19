package com.scmspain.controller;

import com.scmspain.controller.command.DiscardTweetCommand;
import com.scmspain.controller.command.PublishTweetCommand;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@RestController
public class TweetController {
    private TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweet")
    public List<Tweet> listAllTweets() {
        return this.tweetService.getTweetsList();
    }

    @PostMapping("/tweet")
    @ResponseStatus(CREATED)
    public void publishTweet(@RequestBody PublishTweetCommand publishTweetCommand) {
        this.tweetService.publishTweet(publishTweetCommand.getPublisher(), publishTweetCommand.getTweet());
    }

    @DeleteMapping("/tweet")
    @ResponseStatus(NO_CONTENT)
    public void discardTweet(@RequestBody DiscardTweetCommand discardTweetCommand) {
        this.tweetService.updateTweetDiscardDate(discardTweetCommand.getId());
    }

    @GetMapping("/discardedList")
    public List<Tweet> discardTweet() {
        return this.tweetService.getDiscardedTweetsList();
    }

    @DeleteMapping("/deleteAll")
    @ResponseStatus(NO_CONTENT)
    public void deleteAllTweets() {
        this.tweetService.deleteAllTweets();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Object invalidArgumentException(IllegalArgumentException ex) {
        return new Object() {
            public String message = ex.getMessage();
            public String exceptionClass = ex.getClass().getSimpleName();
        };
    }
}
