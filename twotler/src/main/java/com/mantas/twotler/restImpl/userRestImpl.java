package com.mantas.twotler.restImpl;

import com.mantas.twotler.constants.TwotlerConstants;
import com.mantas.twotler.rest.UserRest;
import com.mantas.twotler.serice.UserService;
import com.mantas.twotler.utils.TwotlerUtils;
import com.mantas.twotler.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class userRestImpl implements UserRest {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            return userService.getAllUser();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            return userService.update(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        try {
            return userService.checkToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            return userService.changePassword(requestMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            return userService.forgotPassword(requestMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return TwotlerUtils.getResponseEntity(TwotlerConstants.SOMETHING_WENT_WRONG + " at userRestImpl", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
