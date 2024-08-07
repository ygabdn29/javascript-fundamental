package com.example.demo.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {
  public static ResponseEntity<Object> generateResponseEntity(HttpStatus httpStatus, String message){
    Map<String, Object> response = new HashMap<>();
    response.put("status", httpStatus);
    response.put("message", message);
    return new ResponseEntity<>(response, httpStatus);
  }

  public static ResponseEntity<Object> generateResponseEntity(HttpStatus httpStatus, String message, Object data){
    Map<String, Object> response = new HashMap<>();
    response.put("status", httpStatus);
    response.put("message", message);
    response.put("data", data);
    return new ResponseEntity<>(response,httpStatus);
  }
}
