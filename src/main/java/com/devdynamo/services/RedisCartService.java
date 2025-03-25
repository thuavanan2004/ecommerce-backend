package com.devdynamo.services;

import com.devdynamo.dtos.request.MergeCartRequest;
import com.devdynamo.entities.RedisCart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCartService {
    private static final String CART_PREFIX = "cart:";

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addItemToCart(String sessionId, RedisCart newItem) throws JsonProcessingException {
        String key = CART_PREFIX + sessionId;
        String cartJson = redisTemplate.opsForValue().get(key);

        List<RedisCart> cart = (cartJson != null) ?
                objectMapper.readValue(cartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();

        Optional<RedisCart> existingItem = cart.stream()
                .filter(item -> item.getProductId() == newItem.getProductId())
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
        } else {
            cart.add(newItem);
        }

        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(cart));
    }

    public List<RedisCart> getCart(String sessionId) throws JsonProcessingException {
        String key = CART_PREFIX + sessionId;
        String cartJson = redisTemplate.opsForValue().get(key);

        return (cartJson != null) ?
                objectMapper.readValue(cartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();
    }

    public void removeItemFromCart(String sessionId, long productId) throws JsonProcessingException {
        String key = CART_PREFIX + sessionId;
        String cartJson = redisTemplate.opsForValue().get(key);

        if (cartJson != null) {
            List<RedisCart> cart = objectMapper.readValue(cartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class));
            cart.removeIf(item -> item.getProductId() == productId);
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(cart));
        }
    }

    public void clearCart(String sessionId) {
        String key = CART_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    public void mergeCart(MergeCartRequest request) throws JsonProcessingException {
        String guestCartKey = CART_PREFIX + request.getSessionId();
        String userCartKey = CART_PREFIX + request.getUserId();

        String guestCartJson = redisTemplate.opsForValue().get(guestCartKey);
        String userCartJson = redisTemplate.opsForValue().get(userCartKey);

        List<RedisCart> guestCart = (guestCartJson != null) ?
                objectMapper.readValue(guestCartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();

        List<RedisCart> userCart = (userCartJson != null) ?
                objectMapper.readValue(userCartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();


        for (RedisCart guestItem : guestCart) {
            Optional<RedisCart> existingItem = userCart.stream()
                    .filter(userItem -> userItem.getProductId() == guestItem.getProductId())
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + guestItem.getQuantity());
            } else {
                userCart.add(guestItem);
            }
        }

        redisTemplate.opsForValue().set(userCartKey, objectMapper.writeValueAsString(userCart));

        redisTemplate.delete(guestCartKey);
    }
}
