package com.mantas.twotler.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TwotlerUtils {

    private TwotlerUtils() {

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }
}
