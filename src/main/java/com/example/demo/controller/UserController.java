package com.example.demo.controller;

import java.util.List;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.domain.User;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> findAll() {
        log.info("Query the database for all the users");
        return userRepository.findAll();
    }

    @RequestMapping(value = "/users/{rowKey}", method = RequestMethod.GET)
    @ResponseBody
    public User find(@PathVariable("rowKey") String rowKey) {
        log.info("Query the database for the row key = " + rowKey);
        return userRepository.find(rowKey);
    }

    @RequestMapping(value = "/users/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public List<User> find(@PathVariable("start") String start, @PathVariable("end") String end) {
        log.info("Query the database for the row keys between " + start + " and " + end);
        return userRepository.scan(start, end);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseBody
    public User save(@RequestBody User user) {
        log.info("Save the user into the database: " + user.toString());
        return userRepository.save(user);
    }

    @RequestMapping(value = "users/{rowKey}", method = RequestMethod.DELETE)
    public User delete(@PathVariable("rowKey") String rowKey) {
        log.info("Delete the user with the row key = " + rowKey);
        return userRepository.delete(rowKey);
    }

    @RequestMapping(value = "users/{rowKey}/{col}", method = RequestMethod.DELETE)
    public User delete(@PathVariable("rowKey") String rowKey, @PathVariable("col") String col) {
        log.info("Delete the user with the row key = " + rowKey + " by the column " + col);
        return userRepository.deleteColumn(rowKey, col);
    }
}
