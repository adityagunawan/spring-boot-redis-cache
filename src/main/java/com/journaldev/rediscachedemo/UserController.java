package com.journaldev.rediscachedemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Cacheable(value = "users", key = "#userId", unless = "#result.followers < 12000")
    @Cacheable(value = "users", key = "#userId")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable String userId) {
        LOG.info("Getting user with ID {}.", userId);
        return userRepository.findOne(Long.valueOf(userId));
    }

//    @Cacheable(value = "users", key="#userId.concat('-').concat(#followers)")
    @Cacheable(value = "users", keyGenerator = "keyGenerator")
    @RequestMapping(value = "/two-params/{userId}/{followers}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long userId, @PathVariable Long followers) {
        LOG.info("Getting user with ID {} and follower {}.", userId, followers);
        return userRepository.findOneByIdAndFollowers(userId, followers);
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public User updatePersonByID(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(value = "users", allEntries=true)
    @DeleteMapping("/{userId}")
    public void deleteUserByID(@PathVariable Long userId) {
        LOG.info("deleting person with id {}", userId);
        userRepository.delete(userId);
    }
}
