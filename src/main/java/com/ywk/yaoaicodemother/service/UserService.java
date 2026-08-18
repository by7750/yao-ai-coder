package com.ywk.yaoaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ywk.yaoaicodemother.model.dto.UserQueryRequest;
import com.ywk.yaoaicodemother.model.entity.User;
import com.ywk.yaoaicodemother.model.vo.LoginUserVO;
import com.ywk.yaoaicodemother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/by7750">by7750</a>
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request HttpServletRequest
     * @return 登录结果
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request HttpServletRequest
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param user 用户
     * @return 当前登录用户
     */
    LoginUserVO getLoginUserVO(User  user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 加密
     * @param userPassword 密码
     * @return md5加密后密码
     */
    String getEncryptPwd(String userPassword);

    /**
     * 获取脱敏用户信息
     *
     * @param user 用户信息
     * @return 脱敏用户信息
     */
    UserVO getUserVO(User user);


    /**
     * 获取脱敏用户信息列表
     *
     * @param userList 用户信息列表
     * @return 脱敏用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 根据查询条件获取查询包装器
     * @param userQueryRequest 查询条件
     * @return 查询包装器
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
