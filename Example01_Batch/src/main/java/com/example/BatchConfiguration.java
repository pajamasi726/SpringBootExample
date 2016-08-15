package com.example;

import com.example.batch.EmpleadoItemProcessor;
import com.example.modelo.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016-08-15.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {


    @Bean
    public ItemReader<User> reader(DataSource dataSource){

        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<User>();
        String sql = "select name, hobby from employee";

        reader.setSql(sql);
        reader.setDataSource(dataSource);
        reader.setRowMapper(new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(rs.getString(1),rs.getString(2));
            }
        });

        return reader;
    }

    @Bean
    public ItemProcessor<User,User> processor(){
        return new EmpleadoItemProcessor();
    }

    @Bean
    public ItemWriter<User> writer(DataSource dataSource){

        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();

        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());

        writer.setSql("INSERT INTO user (name, hobby) VALUES (:name, :hobby)");

        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<User> reader, ItemWriter<User> writer
    , ItemProcessor<User, User> processor){

        return stepBuilderFactory.get("step1")
                .<User,User>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener){

        return jobs.get("importEmployeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() throws SQLException{
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setUrl("jdbc:mysql://127.0.0.1/batch");

        dataSource.setUsername("root");

        dataSource.setPassword("root");

        return dataSource;
    }


}
