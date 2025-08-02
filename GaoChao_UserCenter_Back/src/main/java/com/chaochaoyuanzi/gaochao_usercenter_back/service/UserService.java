package com.chaochaoyuanzi.gaochao_usercenter_back.service;

import com.chaochaoyuanzi.gaochao_usercenter_back.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jack.china.document
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2024-10-31 01:38:23
 */
public interface UserService extends IService<User> {


    /**
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 校验密码
     * @param planetCode 编号
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originUser);

    /**
     * 实现用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
