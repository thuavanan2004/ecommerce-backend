package com.devdynamo.controllers.admin;

import com.devdynamo.dtos.request.UserRequestDTO;
import com.devdynamo.dtos.response.ResponseData;
import com.devdynamo.dtos.response.ResponseError;
import com.devdynamo.dtos.response.UserResponseDTO;
import com.devdynamo.enums.Role;
import com.devdynamo.enums.UserStatus;
import com.devdynamo.services.UserService;
import com.devdynamo.validators.EnumPattern;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Validated
@Tag(name="Admin users")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class AdminUserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get user", description = "Lấy thông tin người dùng")
    @GetMapping("/{userId}")
    public ResponseData<?> getUser(@PathVariable @Min(1) long userId){
        log.info("Get user by userId={}", userId);
        try{
            UserResponseDTO record = userService.getUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get user success", record);
        }catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get user failed");
        }
    }

    @Operation(summary = "Create user")
    @PostMapping()
    public ResponseData<?> createUser(@Valid @RequestBody UserRequestDTO request){
        log.info("Create user");
        try{
            userService.createUser(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create user success");
        } catch (Exception e) {
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create user failed");
        }
    }

    @Operation(summary = "Update user", description = "Cập nhật thông tin người dùng")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) long userId, @Valid @RequestBody UserRequestDTO user) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, user);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Update user success");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @Operation(summary = "Change status of user", description = "Cập nhật trạng thái người dùng")
    @PatchMapping("/status/{userId}")
    public ResponseData<?> updateStatus(@PathVariable @Min(1) long userId, @RequestParam @EnumPattern( name="status", regexp = "active|inactive") UserStatus status){
        log.info("Request change status, userId={}", userId);
        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Change status success");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }

    @Operation(summary = "Change role of user", description = "Cập nhật quyền người dùng")
    @PatchMapping("/role/{userId}")
    public ResponseData<?> updateRole(@PathVariable @Min(1) long userId, @RequestParam @EnumPattern( name="role", regexp = "customer|admin") Role role){
        log.info("Request change role, userId={}", userId);
        try {
            userService.changeRole(userId, role);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Change role success");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change role failed");
        }
    }

    @Operation(summary = "Delete user permanently", description = "Xóa vĩnh viễn tài khoản")
    @DeleteMapping("/delete/{userId}")
    public ResponseData<?> delete(@PathVariable @Min(1) long userId){
        log.info("Request delete user, userId={}", userId);

        try{
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete user success");
        }catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user failed");
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "")
    @GetMapping("/list")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                       @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                       @RequestParam(required = false) String... sorts) {
        log.info("Request get all of users");
        try{
            return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsersWithSortBy(pageNo, pageSize, sorts));
        }catch (Exception e){
            log.info("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get list user failed");
        }
    }


}
