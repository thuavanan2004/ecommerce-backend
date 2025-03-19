package com.devdynamo.repositories;

import com.devdynamo.entities.RedisToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
