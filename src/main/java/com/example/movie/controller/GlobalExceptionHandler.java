package com.example.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.movie.exception.DuplicateMovieException;
import com.example.movie.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(DuplicateMovieException.class)
    public String handleDuplicate(DuplicateMovieException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }
}
