package com.leyou.auth.test;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-14 10:00
 */
public class JwtTest {
    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 生成公钥和私钥
     *
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     *
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 解析token
     *
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3MzY5NzQ4MH0.hf5PNQvy0H9K_Tz-wvSne3NdjshsmbXs1E_MtFGzwFALcxM8BxGa1v8U2Cq258SeP6UhWf3Xa7EA3dhgLbv4Fd0Rxfwk5HKAZ88Zo3JSsl94o-1d6lw9D0ozx4XLo3hELcICOUj0m94XdszeFRa0N4YDVgqpIyJvlkCHkuqwVOo";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
