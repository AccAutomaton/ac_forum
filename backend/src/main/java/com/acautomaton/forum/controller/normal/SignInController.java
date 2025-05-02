package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.SignInService;
import com.acautomaton.forum.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/signIn")
public class SignInController {
    UserService userService;
    SignInService signInService;

    @Autowired
    public SignInController(UserService userService, SignInService signInService) {
        this.userService = userService;
        this.signInService = signInService;
    }

    @PutMapping("")
    public Response signIn() {
        return Response.success(Map.of("points", signInService.signIn(userService.getCurrentUser().getUid())));
    }

    @GetMapping("/info")
    public Response getSignInInfo(@RequestParam(required = false) @Max(value = 2100, message = "年份不合法") @Min(value = 2000, message = "年份不合法") Integer year,
                                  @RequestParam(required = false) @Max(value = 12, message = "月份不合法") @Min(value = 1, message = "月份不合法") Integer month) {
        if (year == null && month == null) {
            return Response.success(signInService.getSignInInfoOfCurrentMonth(userService.getCurrentUser().getUid()));
        } else if (year != null && month != null) {
            return Response.success(signInService.getSignInInfoOfMonth(userService.getCurrentUser().getUid(), year, month));
        } else {
            throw new ForumIllegalArgumentException("参数不合法");
        }
    }
}
