package com.leyou.user.controller;

import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-13 14:04
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可用
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
        Boolean boo = this.userService.checkData(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);
    }
}
