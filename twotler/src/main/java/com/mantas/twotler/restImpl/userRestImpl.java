package com.mantas.twotler.restImpl;

import com.google.common.base.Strings;
import com.mantas.twotler.JWT.JwtFilter;
import com.mantas.twotler.JWT.JwtUtil;
import com.mantas.twotler.JWT.UsersDetailsService;
import com.mantas.twotler.constants.TwotlerConstants;
import com.mantas.twotler.dao.UserDao;
import com.mantas.twotler.model.User;
import com.mantas.twotler.rest.UserRest;
import com.mantas.twotler.utils.EmailUtils;
import com.mantas.twotler.utils.TwotlerUtils;
import com.mantas.twotler.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class userRestImpl implements UserRest {

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
        try{
            // Check if email/name/password has been entered
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                // check if such email already exists in database
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return TwotlerUtils.getResponseEntity("Sign Up Successful!", HttpStatus.OK);
                } else {
                    return TwotlerUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return TwotlerUtils.getResponseEntity(TwotlerConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
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
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        try {
            return TwotlerUtils.getResponseEntity("true", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());

            if(!user.equals(null)) {
                if(user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return TwotlerUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);
                }
                return TwotlerUtils.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);
            }
            return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));

            if(user != null && !Strings.isNullOrEmpty(user.getEmail())) {
                emailUtils.forgotPasswordMail(user.getEmail(), "Credentials by Twotler System", user.getPassword());
            }

            return TwotlerUtils.getResponseEntity("Check your email for password", HttpStatus.OK); // We return "check email" msg for evey email doesn't matter if it exists or not for security reasons
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
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
