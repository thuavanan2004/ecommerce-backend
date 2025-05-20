package com.devdynamo.services;

import com.devdynamo.dtos.request.MergeCartRequest;
import com.devdynamo.entities.*;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.CartItemMapper;
import com.devdynamo.repositories.CartItemRepository;
import com.devdynamo.repositories.CartRepository;
import com.devdynamo.repositories.ProductRepository;
import com.devdynamo.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCartService {
    private static final String CART_PREFIX = "cart:";

    private final StringRedisTemplate redisTemplate;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final CartItemMapper cartItemMapper;

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

    public void mergeCartToDB(MergeCartRequest request) throws JsonProcessingException {
        String guestCartKey = CART_PREFIX + request.getSessionId();

        String guestCartJson = redisTemplate.opsForValue().get(guestCartKey);

        List<RedisCart> guestCart = (guestCartJson != null) ?
                objectMapper.readValue(guestCartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();


        // Xác nhận user tồn tại
        userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Tìm hoặc tạo mới cart
        CartEntity cartEntity = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserId(request.getUserId());
                    newCart.setSessionId(request.getSessionId());
                    return cartRepository.save(newCart);
                });

        // XÓA toàn bộ cart item cũ của cart này
        long cartId =  cartEntity.getId();
        cartItemRepository.deleteByCartId(cartEntity.getId());
        if (guestCart.isEmpty()) {
            return;
        }


        // Lấy thông tin sản phẩm từ Redis
        Set<Long> productIds = guestCart.stream().map(RedisCart::getProductId).collect(Collectors.toSet());
        Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, p -> p));

        // Tạo mới danh sách cart items để lưu
        List<CartItemEntity> cartItemsToSave = new ArrayList<>();

        for (RedisCart redisCartItem : guestCart) {
            ProductEntity product = productMap.get(redisCartItem.getProductId());
            if (product == null) continue;

            CartItemEntity cartItemEntity = new CartItemEntity();
            cartItemEntity.setCart(cartEntity);
            cartItemEntity.setProduct(product);
            cartItemEntity.setQuantity(redisCartItem.getQuantity());

            cartItemsToSave.add(cartItemEntity);
        }

        if (!cartItemsToSave.isEmpty()) {
            cartItemRepository.saveAll(cartItemsToSave);
        }

    }


    public void mergeCart(List<RedisCart> userCart, List<RedisCart> guestCart, String guestCartKey) throws JsonProcessingException {

        for (RedisCart userItem : userCart) {
            Optional<RedisCart> existingItem = guestCart.stream()
                    .filter(guestItem -> userItem.getProductId() == guestItem.getProductId())
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + userItem.getQuantity());
            } else {
                guestCart.add(userItem);
            }
        }

        redisTemplate.opsForValue().set(guestCartKey, objectMapper.writeValueAsString(guestCartKey));

    }

    public void mergeCartToRedis(MergeCartRequest request) throws JsonProcessingException{
        String guestCartKey = CART_PREFIX + request.getSessionId();

        userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartEntity cart = cartRepository.findByUserId(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        List<CartItemEntity> listCartItem = cartItemRepository.findAllByCartId(cart.getId());
        List<RedisCart> userCart = listCartItem.stream().map(cartItemMapper::toRedisCart).toList();

        String existCartJson = redisTemplate.opsForValue().get(guestCartKey);

        List<RedisCart> guestCart = (existCartJson != null ) ?
                objectMapper.readValue(existCartJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RedisCart.class)) :
                new ArrayList<>();

        mergeCart(userCart, guestCart, guestCartKey);

        redisTemplate.opsForValue().set(guestCartKey, objectMapper.writeValueAsString(userCart));
    }
}
