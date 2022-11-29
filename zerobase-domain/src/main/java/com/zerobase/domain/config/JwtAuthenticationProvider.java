package com.zerobase.domain.config;

import com.zerobase.domain.domain.common.UserType;
import com.zerobase.domain.domain.common.UserVo;
import com.zerobase.domain.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Objects;

public class JwtAuthenticationProvider {

    private final String SECRET_KEY = "secretKey";
    private final long TOKEN_VALID_TIME = 1000L* 60 * 60* 24;

    public String createToken(String userPk, Long id, UserType userType){
        Claims claims = Jwts.claims().setSubject(Aes256Util.encrypt(userPk)).setId(Aes256Util.encrypt(id.toString()));
        claims.put("roles",userType);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .compact();
    }
    public boolean validateToken(String jwtToken){
        try{
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claimsJws.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

    public UserVo getUser(String token){
        Claims c = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return new UserVo(Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId())))
                , Aes256Util.decrypt(c.getSubject()));
    }
}
