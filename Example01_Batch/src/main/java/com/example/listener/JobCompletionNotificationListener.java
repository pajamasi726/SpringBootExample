package com.example.listener;

import com.example.modelo.User;
import org.slf4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by Administrator on 2016-08-15.
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport{

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void afterJob(JobExecution jobExecution){
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!!!JOB 끝났음");

            List<User> result = jdbcTemplate.query("SELECT name, hobby from employee", new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User(rs.getString(1),rs.getString(2));
                }
            });

            for(User person :result ){
                log.info("Found < "+person+" > in the database");
            }
        }
    }
}
