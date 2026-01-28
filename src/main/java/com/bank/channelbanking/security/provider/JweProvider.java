package com.bank.channelbanking.security.provider;
import com.bank.channelbanking.security.service.CustomUserDetails;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JweProvider {

    private final byte[] sharedKey;
    private final long expirationTime = 3600000;
    public JweProvider(@Value("${jwe.secret-key}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.sharedKey = decodedKey;
    }

    public String createToken(Long userId, String email, Collection<? extends GrantedAuthority> authorities){
        try {
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email)
                    .claim("userId", userId)
                    .claim("roles", roles)
                    .expirationTime(new Date(new Date().getTime() + expirationTime))
                    .issueTime(new Date())
                    .build();
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);
            JWEObject jweObject = new JWEObject(header, new Payload(claimsSet.toJSONObject()));
            jweObject.encrypt(new DirectEncrypter(sharedKey));
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("토큰 생성 실패");
        }
    }

    public Authentication getAuthentication(String token){
        try {
            JWEObject jweObject = JWEObject.parse(token);
            jweObject.decrypt(new DirectDecrypter(sharedKey));
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jweObject.getPayload().toJSONObject());

            if (new Date().after(claimsSet.getExpirationTime())) {
                throw new RuntimeException("토큰 만료");
            }

            Long userId = (Long) claimsSet.getClaim("userId");
            String email = claimsSet.getSubject();
            List<String> roles = (List<String>) claimsSet.getClaim("roles");

            Collection<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            CustomUserDetails principal = new CustomUserDetails(userId, email, null, authorities);

            return new UsernamePasswordAuthenticationToken(principal, null, authorities);

        } catch (Exception e) {
            return null;
        }
    }
}
