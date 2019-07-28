package com.maxwang.buycar.service;

import com.maxwang.buycar.dao.UserDao;
import com.maxwang.buycar.domain.User;
import com.maxwang.buycar.exception.GlobalException;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.MD5Util;
import com.maxwang.buycar.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    /**
     * 根据ID得到User
     * @param username
     * @return
     */
    public User getByName(String username){
        return userDao.getByName(username);
    }


    public User getById(Integer id){
        return userDao.getById(id);
    }

    /**
     *  用户登录
     * @param loginVo
     * @return
     */
    public Boolean login(LoginVo loginVo){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String username = loginVo.getUsername();
        String formPass = loginVo.getPassword();

        User user=getByName(username);

        if (user == null){
            throw new GlobalException(CodeMsg.USERNAME_NOT_EXIST);
        }

        /**
         * 验证密码
         */
        String dbPass = user.getPassword();

        String saltDb = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDb);

        System.out.println(dbPass);
        System.out.println(calcPass);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        return true;
    }


    /**
     *  用户登录
     * @param loginVo
     * @return
     */
    public Boolean AdminLogin(LoginVo loginVo){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String username = loginVo.getUsername();
        String formPass = loginVo.getPassword();

        User user=getByName(username);

        if (user == null){
            throw new GlobalException(CodeMsg.USERNAME_NOT_EXIST);
        }

        if (0 == user.getRole()){
            throw new GlobalException(CodeMsg.AUTHORITY_NOT_ENOUGHT);
        }
        /**
         * 验证密码
         */
        String dbPass = user.getPassword();

        String saltDb = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDb);

        System.out.println(dbPass);
        System.out.println(calcPass);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        return true;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    public boolean register(User user){

        User user1 = userDao.getUsername(user.getUsername());

        if (user1!=null){
            throw new GlobalException(CodeMsg.USERNAME_IS_EXIST);
        }

        User user2 = userDao.getTel(user.getTel());
        if (user2!=null){
            throw new GlobalException(CodeMsg.TEL_IS_EXIST);
        }


        int userId = userDao.userAdd(user);

        if (userId  == 0){
            throw new GlobalException(CodeMsg.REGISTER_ERROR);
        }
        return true;
    }

    /**
     * 查询用户列表
     * @return
     */
    public List<User> getUserList(){
        List<User> list = userDao.getUserList();

        if (list == null){
            throw new GlobalException(CodeMsg.SEARCH_USER_LIST_ERROR);
        }
        return list;
    }

    public void userUpdate(User user){
        int result = userDao.userUpdate(user);

        if (0==result){
            throw new GlobalException(CodeMsg.UPDATE_ERROR);
        }
    }

    public void deleteUser(Integer id) {

        int result = userDao.deleteUser(id);
        if (0 == result){
            throw  new GlobalException(CodeMsg.DELETE_ERROR);
        }
    }

}
