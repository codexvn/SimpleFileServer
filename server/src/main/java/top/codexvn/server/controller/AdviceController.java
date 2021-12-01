package top.codexvn.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.codexvn.exception.CustomRuntimeException;
import top.codexvn.vo.JsonView;

@RestControllerAdvice
@Slf4j
public class AdviceController {

    @ExceptionHandler(value = CustomRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonView<Object> exceptionHandler(CustomRuntimeException e){
        log.error("捕捉到异常: {}",e.getMessage(),e);
        return JsonView.getErrorJsonView(null,e.getMessage());
    }
}
