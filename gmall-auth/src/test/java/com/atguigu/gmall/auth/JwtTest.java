package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    //存放秘钥的路径
    private static final String pubKeyPath = "E:\\Sso\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\Sso\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {//获取秘钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzExMTE5MTd9.Poqivcf37in1fOjtB0hDFzwZwwbDmRGVfs3rEoQVerw1AO0UAciweuKcuR5GbkCqoBB5DxYEswgZ6y4qfybIO3ot6zXpYki6cdPQtPzKn7Vv4DRQqOJ4hgUtoGW27u_5Sd-e_v3MU6dEhVBUHUVG-R9UQfmLm1iuiv4YBLVVj-atQkcTZvgjZnucV2_VtH9oY-IhwuQmtG-gecWo_wksh-iRT-TrydBjUtamKLX1xUZ1I6qSL9XoSH1ax7PVVrh0KWW5WzUxY9BZqQ6qSfbHvPvpQtmaRLPc2g7hKTK0StKdBjCC7lenFSPYrd2DcaGRXQTrcObaQWCGyVRWbMgeZA";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}