package com.powerinfer.server.utils;

import com.powerinfer.server.entity.Key;
import com.powerinfer.server.service.KeyService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
        if(Math.abs(time-current) > 5 * 60 * 1000){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Response time out.");
            return false;
        }
        // check signature
        ProcessBuilder pb = new ProcessBuilder("/mnt/miniconda3/bin/python", //FIXME: python path
                AddreessManager.getVerifyPythonPath(), pubkey, timestamp_str, signature);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.err.println("[verify.py] "+line);
//            if (!line.equals("True")) {
//                System.err.println("[verify.py]"+line);
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                response.getWriter().write("Signature verification failed.");
//                return false;
//            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Signature verification failed.");
        return false;

//        request.setAttribute("uid", key.getUid());
//
//        return true;
    }
}
