package com.rumortown.user;

import com.rumortown.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/guest")
    public ApiResponse<GuestPlayerDto> createGuest() {
        return ApiResponse.data(userService.createGuest());
    }
}
