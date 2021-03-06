package com.leyou.controller;

import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.entity.UserInfo1;
import com.leyou.service.AuthService;
import com.leyou.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;
    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request, HttpServletResponse response
            ){
      String token=  authService.accredit(username,password);
      if (StringUtils.isBlank(token)){
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      return ResponseEntity.ok().build();
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo1> verify(@CookieValue("LY_TOKEN")String token,
                                            HttpServletRequest request,HttpServletResponse response
                                            ){
        try {
            UserInfo1 userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            String newToken=JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),newToken,jwtProperties.getCookieMaxAge(),null,true);
            return ResponseEntity.ok(userInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
