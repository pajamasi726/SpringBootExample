package com.example.batch;

import com.example.modelo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by Administrator on 2016-08-15.
 */
public class EmpleadoItemProcessor implements ItemProcessor<User, User>{

    private static final Logger log = LoggerFactory.getLogger(EmpleadoItemProcessor.class);

    @Override
    public User process(User item) throws Exception {

        String name = item.getName().toUpperCase();
        String hobby = item.getHobby().toUpperCase();

        User userProcess = new User(name,hobby);

        return userProcess;
    }
}
