package com.devdynamo.services.Impl;

import com.devdynamo.dtos.request.WishlistRequestDTO;
import com.devdynamo.dtos.response.WishlistReponseDTO;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.entities.WishListEntity;
import com.devdynamo.exceptions.ResourceNotFoundException;
import com.devdynamo.mappers.WishlistMapper;
import com.devdynamo.repositories.ProductRepository;
import com.devdynamo.repositories.UserRepository;
import com.devdynamo.repositories.WishlistRepository;
import com.devdynamo.services.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final WishlistMapper wishlistMapper;


    @Override
    public void addProductToWishlist(WishlistRequestDTO request) {
        ProductEntity product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        UserEntity user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean existEntity = wishlistRepository.existsByUserIdAndProductId(request.getUserId(), request.getProductId());
        if(existEntity){
            log.info("Service: Product is already in wishlist, skipping");
            return;
        }

        WishListEntity entity = new WishListEntity();
        entity.setProduct(product);
        entity.setUser(user);

        wishlistRepository.save(entity);

        log.info("Service: Add product to wishlist successfully");
    }

    @Override
    public void removeProductFromWishlist(WishlistRequestDTO request) {
        WishListEntity entity = wishlistRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        wishlistRepository.delete(entity);
        log.info("Remove product from wishlist successfully");
    }

    @Override
    public List<WishlistReponseDTO> getAllProductFromWishlist(long userId) {
        List<WishListEntity> list = wishlistRepository.findByUserId(userId);
        return list.stream().map(wishlistMapper::toDTO).collect(Collectors.toList());
    }
}
