package com.maxwang.buycar.controller;

import com.maxwang.buycar.config.WebConfig;
import com.maxwang.buycar.domain.User;
import com.maxwang.buycar.service.UserService;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.MD5Util;
import com.maxwang.buycar.util.Result;
import com.maxwang.buycar.util.ValidatorUtil;
import com.maxwang.buycar.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final String SALT = "123456";

    private  static Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;


    /**
     * 跳转到登录界面
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(){
        return "loginTest";
    }

    /**
     * 登录操作
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletRequest request, LoginVo loginVo){

        logger.info(loginVo.toString());

        String passInput=loginVo.getPassword();
        String username=loginVo.getUsername();

        if (StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmpty(username)){
            return Result.error(CodeMsg.USERNAME_IS_NULL);
        }

        userService.login(loginVo);

        HttpSession session = request.getSession(true);

        session.setAttribute(WebConfig.LOGIN_USER,loginVo.getUsername());

        return Result.success(true);
    }

    /**
     * 跳转到注册界面
     * @return
     */
    @RequestMapping("/to_register")
    public String toRegister(){
        return "register";
    }


    /**
     * 用户注册
     * @param tel
     * @param password
     * @param username
     * @return
     */
    @RequestMapping("/do_register")
    @ResponseBody
    public Result<Boolean> doRegister(@RequestParam("tel") String tel,@RequestParam("password") String password,@RequestParam("username") String username){

        if (StringUtils.isEmpty(password)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmpty(tel)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if (!ValidatorUtil.isMobile(tel)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }

        String dbPassword = MD5Util.formPassToDBPass(password,SALT);

        User user=new User();
        user.setUsername(username);
        user.setPassword(dbPassword);
        user.setTel(tel);
        user.setSalt(SALT);
        user.setRole(0);

        userService.register(user);

        return Result.success(true);
    }
}
