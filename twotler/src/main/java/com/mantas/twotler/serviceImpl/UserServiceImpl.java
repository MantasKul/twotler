package com.mantas.twotler.serviceImpl;

import com.mantas.twotler.JWT.JwtFilter;
import com.mantas.twotler.JWT.JwtUtil;
import com.mantas.twotler.JWT.SecurityConfig;
import com.mantas.twotler.JWT.UsersDetailsService;
import com.mantas.twotler.constants.TwotlerConstants;
import com.mantas.twotler.dao.UserDao;
import com.mantas.twotler.model.User;
import com.mantas.twotler.serice.UserService;
import com.mantas.twotler.utils.EmailUtils;
import com.mantas.twotler.utils.TwotlerUtils;
import com.mantas.twotler.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersDetailsService usersDetailsService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return TwotlerUtils.getResponseEntity("Sign Up Successful!", HttpStatus.OK);
                } else {
                    return TwotlerUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return TwotlerUtils.getResponseEntity(TwotlerConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at UserServiceImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            // extracting email and pass from requestMap
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"));
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            // if user is authenticated
            if (auth.isAuthenticated()) {
                // If user is approved (possibly will be removed with status column)
                if(usersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(usersDetailsService.getUserDetail().getEmail(),usersDetailsService.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + " Wait for admin approval."+"\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch(Exception e) {
            log.error("{}", e);
        }

        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials."+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendEmailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return TwotlerUtils.getResponseEntity("User with an id " + requestMap.get("id") + " has been updated successfully", HttpStatus.OK);
                } else {
                    return TwotlerUtils.getResponseEntity("User with " + requestMap.get("id") + " id doesn't exist", HttpStatus.OK);
                }
            } else {
                return TwotlerUtils.getResponseEntity(TwotlerConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setRole("user");
        user.setStatus("true");

        return user;
    }

    private void sendEmailToAllAdmin(String status, String email, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());    // Removes the user which is changing the status
        if(status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:- " + email+"\n is approved by \nADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:- " + email+"\n is disabled by \nADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
        }
    }
}
