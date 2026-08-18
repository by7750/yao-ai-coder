package com.ywk.yaoaicodemother.aop;

import cn.hutool.core.util.StrUtil;
import com.ywk.yaoaicodemother.annotations.AuthCheck;
import com.ywk.yaoaicodemother.exception.BusinessException;
import com.ywk.yaoaicodemother.exception.ErrorCode;
import com.ywk.yaoaicodemother.model.entity.User;
import com.ywk.yaoaicodemother.model.enums.UserRoleEnum;
import com.ywk.yaoaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 身份认证切面
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 校验用户身份
        // 如果没有指定角色，则直接通过
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 从请求头中获取用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        // 如果用户未登录，则抛出未登录异常
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        if (userRoleEnum == null || !StrUtil.equals(mustRole, loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
