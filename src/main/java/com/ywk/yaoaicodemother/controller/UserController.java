package com.ywk.yaoaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import com.ywk.yaoaicodemother.annotations.AuthCheck;
import com.ywk.yaoaicodemother.common.BaseResponse;
import com.ywk.yaoaicodemother.common.DeleteRequest;
import com.ywk.yaoaicodemother.contanst.UserConstant;
import com.ywk.yaoaicodemother.exception.BusinessException;
import com.ywk.yaoaicodemother.exception.ErrorCode;
import com.ywk.yaoaicodemother.model.dto.*;
import com.ywk.yaoaicodemother.model.vo.LoginUserVO;
import com.ywk.yaoaicodemother.model.vo.UserVO;
import com.ywk.yaoaicodemother.utils.ResultUtils;
import com.ywk.yaoaicodemother.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ywk.yaoaicodemother.model.entity.User;
import com.ywk.yaoaicodemother.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://github.com/by7750">by7750</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册。
     *
     * @param userRegisterRequest 注册参数
     * @return 注册结果
     */
    @PostMapping("register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long res = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(res);
    }

    /**
     * 用户登录。
     *
     * @param userLoginRequest 登录参数
     * @param request 请求
     * @return 登录结果
     */
    @PostMapping("login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isNull(userLoginRequest), ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户。
     *
     * @param request 请求
     * @return 登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销。
     *
     * @param request 请求
     * @return 注销结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPwd(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

//    /**
//     * 保存用户。
//     *
//     * @param user 用户
//     * @return {@code true} 保存成功，{@code false} 保存失败
//     */
//    @PostMapping("save")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public boolean save(@RequestBody User user) {
//        return userService.save(user);
//    }
//
//    /**
//     * 根据主键删除用户。
//     *
//     * @param id 主键
//     * @return {@code true} 删除成功，{@code false} 删除失败
//     */
//    @DeleteMapping("remove/{id}")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public boolean remove(@PathVariable Long id) {
//        return userService.removeById(id);
//    }
//
//    /**
//     * 根据主键更新用户。
//     *
//     * @param user 用户
//     * @return {@code true} 更新成功，{@code false} 更新失败
//     */
//    @PutMapping("update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public boolean update(@RequestBody User user) {
//        return userService.updateById(user);
//    }
//
//    /**
//     * 查询所有用户。
//     *
//     * @return 所有数据
//     */
//    @GetMapping("list")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public List<User> list() {
//        return userService.list();
//    }
//
//    /**
//     * 根据主键获取用户。
//     *
//     * @param id 用户主键
//     * @return 用户详情
//     */
//    @GetMapping("getInfo/{id}")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public User getInfo(@PathVariable Long id) {
//        return userService.getById(id);
//    }
//
//    /**
//     * 分页查询用户。
//     *
//     * @param page 分页对象
//     * @return 分页对象
//     */
//    @GetMapping("page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public Page<User> page(Page<User> page) {
//        return userService.page(page);
//    }

}
