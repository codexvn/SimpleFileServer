package top.codexvn.server.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class DefaultAspect {
    @Pointcut("execution( * top.codexvn.server.controller.*.* (..))")
    public void defaultPointcut() {
    }

    @Around("defaultPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        log.info("执行{}方法，参数为{}", signature, ArrayUtils.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("方法执行完成，结果为{}", result);
            return result;
        } catch (Throwable e) {
            log.info("{}方法执行出错", signature);
            throw e;
        }


    }
}