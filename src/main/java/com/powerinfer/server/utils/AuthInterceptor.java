package com.powerinfer.server.utils;

import com.powerinfer.server.entity.Key;
import com.powerinfer.server.service.KeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Signature;
import java.util.Base64;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private KeyService keyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pubkey = request.getHeader("pubkey");
        String timestamp_str = request.getHeader("timestamp");
        String signature = request.getHeader("signature");


        if (pubkey == null || timestamp_str == null || signature == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Status Code:400
            response.getWriter().write("Missing required headers");
            return false;
        }
        // check user pubkey correspondence
        Key key = keyService.getKeyByContent(pubkey);
        if (key == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Status Code:400
            response.getWriter().write("Public key not registered.");
            return false;
        }
        // check timestamp
        long time = Long.parseLong(timestamp_str);
        long current = System.currentTimeMillis();
        if(Math.abs(time-current) > 3 * 60 * 1000){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Response time out.");
            return false;
        }
        // check signature
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(PublicKeyUtils.loadPubKey(pubkey));
        sig.update(timestamp_str.getBytes());
        if (!sig.verify(Base64.getDecoder().decode(signature))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Can't verify signature.");
            return false;
        }
        request.setAttribute("uid", key.getUid());

        return true;
    }
}
