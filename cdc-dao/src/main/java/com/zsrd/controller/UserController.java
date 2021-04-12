package com.zsrd.controller;

import com.zsrd.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("用户管理")
@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class UserController {

    private final IUserService userService;

    @ApiOperation(value = "获取用户列表")
    @GetMapping("/list")
    public ResponseEntity<?> list(){
        return ResponseEntity.ok(userService.list());
    }
}
