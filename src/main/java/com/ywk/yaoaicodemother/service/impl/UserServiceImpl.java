package com.ywk.yaoaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ywk.yaoaicodemother.contanst.UserConstant;
import com.ywk.yaoaicodemother.exception.BusinessException;
import com.ywk.yaoaicodemother.exception.ErrorCode;
import com.ywk.yaoaicodemother.model.dto.UserQueryRequest;
import com.ywk.yaoaicodemother.model.entity.User;
import com.ywk.yaoaicodemother.mapper.UserMapper;
import com.ywk.yaoaicodemother.model.enums.UserRoleEnum;
import com.ywk.yaoaicodemother.model.vo.LoginUserVO;
import com.ywk.yaoaicodemother.model.vo.UserVO;
import com.ywk.yaoaicodemother.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/by7750">by7750</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 检验参数
        if (StrUtil.hasBlank(userAccount, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        }
        if (StrUtil.length(userPassword) < 4 ||  StrUtil.length(checkPassword) < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于4");
        }
        if (!StrUtil.equals(userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 查询用户
        long acctCnt = this.mapper.selectCountByQuery(new QueryWrapper().eq("userAccount", userAccount));
        if (acctCnt > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 加密
        String encPwd = getEncryptPwd(userPassword);
        // 创建用户
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encPwd)
                .userName("无名")
                .userRole(UserRoleEnum.USER.getValue())
                .build();
        boolean res = this.save(user);
        if (!res) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 检验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        }
        // 密码长度
        if (StrUtil.length(userPassword) < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于4");
        }
        // 查询用户
        User user = this.mapper.selectOneByQuery(new QueryWrapper()
                .eq("userAccount", userAccount)
                .eq("userPassword", getEncryptPwd(userPassword)));
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        // 获取登录用户信息
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setId(user.getId());
        loginUserVO.setUserAccount(user.getUserAccount());
        loginUserVO.setUserName(user.getUserName());
        loginUserVO.setUserAvatar(user.getUserAvatar());
        loginUserVO.setUserRole(user.getUserRole());
        // 登录成功，保存用户登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return loginUserVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 判断用户是否登录
        User userObj = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询用户信息
        User user = this.getById(userObj.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user != null) {
            LoginUserVO loginUserVO = new LoginUserVO();
            loginUserVO.setId(user.getId());
            loginUserVO.setUserAccount(user.getUserAccount());
            loginUserVO.setUserName(user.getUserName());
            loginUserVO.setUserAvatar(user.getUserAvatar());
            loginUserVO.setUserRole(user.getUserRole());
            return loginUserVO;
        }
        return null;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }


    @Override
    public String getEncryptPwd(String userPassword) {
        String SALT = "YWK";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

}
