package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseData<?> getUser(@PathVariable Long id){
        UserResponseDTO record = userService.getUser(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Get user success", record);
    }
}
