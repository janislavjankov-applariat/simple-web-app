package org.jj.simpleweb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Configuration
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
public class Application {
    private static final String ENV_MONGO_HOST = "MONGO_HOST";
    private static final String ENV_MONGO_PORT = "MONGO_PORT";

    @Autowired
    private DataRepository repository;

    @Bean
    public Mongo mongo() {
        String mongoHost = System.getenv(ENV_MONGO_HOST);
        String mongoPortString = System.getenv(ENV_MONGO_PORT);
        if (mongoHost == null) {
            mongoHost = "localhost";
        }
        int mongoPort = 27017;
        if (mongoPortString != null) {
            mongoPort = Integer.parseInt(mongoPortString);
        }
        return new MongoClient(mongoHost, mongoPort);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "data");
    }

    @RequestMapping("/")
    @ResponseBody
    List<String> root() {
        return repository.findAll().stream().map(x -> x.key).collect(Collectors.toList());
    }

    @RequestMapping("/{key}")
    @ResponseBody
    Data get(@PathVariable String key) {
        return repository.findOne(key);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Data create(@RequestBody Data data) {
        repository.save(data);
        return data;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}