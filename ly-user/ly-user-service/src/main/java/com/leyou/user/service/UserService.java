package com.leyou.user.service;

import com.leyou.user.pojo.User;
import com.leyou.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-13 14:04
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 校验数据是否可用
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }
}
