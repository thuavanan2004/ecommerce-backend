package com.devdynamo.services;

import com.devdynamo.dtos.request.WishlistRequestDTO;
import com.devdynamo.dtos.response.WishlistReponseDTO;

import java.util.List;

public interface WishlistService {
    void addProductToWishlist(WishlistRequestDTO request);

    void removeProductFromWishlist(WishlistRequestDTO request);

    List<WishlistReponseDTO> getAllProductFromWishlist(long userId);

}
