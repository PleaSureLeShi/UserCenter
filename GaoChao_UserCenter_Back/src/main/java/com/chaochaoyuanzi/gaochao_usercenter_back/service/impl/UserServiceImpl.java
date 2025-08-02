package com.chaochaoyuanzi.gaochao_usercenter_back.service.impl;
//import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chaochaoyuanzi.gaochao_usercenter_back.common.ErrorCode;
import com.chaochaoyuanzi.gaochao_usercenter_back.exception.BusinessException;
import com.chaochaoyuanzi.gaochao_usercenter_back.model.domain.User;
import com.chaochaoyuanzi.gaochao_usercenter_back.service.UserService;
import com.chaochaoyuanzi.gaochao_usercenter_back.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chaochaoyuanzi.gaochao_usercenter_back.constants.UserConstant.USER_LOGIN_STATE;

/**
* @author jack.china.document
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-31 01:38:23
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    private static final String DOG = "java";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if(userPassword.length()<8||checkPassword.length()<8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "中心编号过长");
        }
        //账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //账户不能重复 本步骤进行了一次数据库查询，放在后面可以防止如果前面的条件不匹配，这样就可以不查询数据库，节省性能
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((DOG + userPassword).getBytes());
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassWord(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean savedResult = this.save(user);
        if (!savedResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            //todo修改自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if(userPassword.length()<8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码少于8位");
        }
        //账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((DOG + userPassword).getBytes());
        //账户不能重复 本步骤进行了一次数据库查询，放在后面可以防止如果前面的条件不匹配，这样就可以不查询数据库，节省性能
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed,userAccount can't match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未找到相关用户 请检查是否输入错误");
        }
        //脱敏
        User safetyUser = getSafetyUser(user);
        //记录用户登陆态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            log.info("----------getSafetyUser出错啦----------");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        log.info("你小子,尽搞些退出登录的操作,别被我发现你一直重复登录!");
        throw new BusinessException(ErrorCode.SUCCESS, "退出登录成功");
    }

}




