package com.mantas.twotler.restImpl;

import com.mantas.twotler.constants.TwotlerConstants;
import com.mantas.twotler.rest.UserRest;
import com.mantas.twotler.serice.UserService;
import com.mantas.twotler.utils.TwotlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
}
