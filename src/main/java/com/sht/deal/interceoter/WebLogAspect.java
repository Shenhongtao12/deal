package com.sht.deal.interceoter;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 输出运行日志
 * @author Sht
 * @project deal
 * @date 2020/3/21 - 14:38
 **/
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    /**
     * 进入方法时间戳
     */
    private Long startTime;
    /**
     * 方法结束时间戳(计时)
     */
    private Long endTime;

    public WebLogAspect() {
    }


    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    @Pointcut("execution(public * com.sht.deal.controller.*.*(..)) && !execution(public * com.sht.deal.controller.Classify1Controller.findAll(..))")
    public void webLogPointcut() {
    }



    /**
     * 前置通知：
     * 1. 在执行目标方法之前执行，比如请求接口之前的登录验证;
     * 2. 在前置通知中设置请求日志信息，如开始时间，请求参数，注解内容等
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLogPointcut()")
    public void doBefore(JoinPoint joinPoint) {

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取请求头中的User-Agent
        //UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //打印请求的内容
        startTime = System.currentTimeMillis();
        log.info("请求开始时间：" + LocalDateTime.now());
        String url = request.getRequestURL().toString();
        log.info("请求Url：" + url.substring(url.indexOf("/api")));
        log.info("请求方式：" + request.getMethod());
        log.info("请求ip：" + request.getRemoteAddr());
        //log.info("请求方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("请求参数：" + Arrays.toString(joinPoint.getArgs()));
        // 系统信息
        //log.info("浏览器：{}", userAgent.getBrowser().toString());
        //log.info("浏览器版本：{}", userAgent.getBrowserVersion());
        //log.info("操作系统: {}", userAgent.getOperatingSystem().toString());
    }

    /**
     * 返回通知：
     * 1. 在目标方法正常结束之后执行
     * 1. 在返回通知中补充请求日志信息，如返回时间，方法耗时，返回值，并且保存日志信息
     *
     * @param ret
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "webLogPointcut()")
    public void doAfterReturning(Object ret) throws Throwable {
        endTime = System.currentTimeMillis();
        log.info("请求结束时间：" + LocalDateTime.now());
        log.info("请求耗时：" + (endTime - startTime) + " 毫秒");
        // 处理完请求，返回内容
        log.info("请求返回：" + ret);
    }

    /**
     * 异常通知：
     * 1. 在目标方法非正常结束，发生异常或者抛出异常时执行
     * 1. 在异常通知中设置异常信息，并将其保存
     *
     * @param throwable
     */
    @AfterThrowing(value = "webLogPointcut()", throwing = "throwable")
    public void doAfterThrowing(Throwable throwable) {
        // 保存异常日志记录
        log.error("发生异常时间：" + LocalDateTime.now());
        log.error("抛出异常：" + throwable.getMessage());
    }





    //@Before和@AfterReturning部分也可使用以下代码替代
    /**
     * 环绕通知
     * 在执行方法前后调用Advice，这是最常用的方法，相当于@Before和@AfterReturning全部做的事儿
     * @param pjp
     * @return
     * @throws Throwable
     */
    /*
    @Around("webLogPointcut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取请求头中的User-Agent
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //打印请求的内容
        startTime = System.currentTimeMillis();
        log.info("请求Url : {}" , request.getRequestURL().toString());
        log.info("请求方式 : {}" , request.getMethod());
        log.info("请求ip : {}" , request.getRemoteAddr());
        log.info("请求方法 : " , pjp.getSignature().getDeclaringTypeName() , "." , pjp.getSignature().getName());
        log.info("请求参数 : {}" , Arrays.toString(pjp.getArgs()));
        // 系统信息
        log.info("浏览器：{}", userAgent.getBrowser().toString());
        log.info("浏览器版本：{}",userAgent.getBrowserVersion());
        log.info("操作系统: {}", userAgent.getOperatingSystem().toString());
        // pjp.proceed()：当我们执行完切面代码之后，还有继续处理业务相关的代码。proceed()方法会继续执行业务代码，并且其返回值，就是业务处理完成之后的返回值。
        Object ret = pjp.proceed();
        log.info("请求结束时间："+ LocalDateTime.now());
        log.info("请求耗时：{}" , (System.currentTimeMillis() - startTime));
        // 处理完请求，返回内容
        log.info("请求返回 : " , ret);
        return ret;
    }
    */
}
