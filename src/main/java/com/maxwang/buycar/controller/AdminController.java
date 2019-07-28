package com.maxwang.buycar.controller;

import com.maxwang.buycar.config.WebConfig;
import com.maxwang.buycar.domain.Car;
import com.maxwang.buycar.domain.Essay;
import com.maxwang.buycar.domain.User;
import com.maxwang.buycar.dto.CarScope;
import com.maxwang.buycar.exception.GlobalException;
import com.maxwang.buycar.redis.RedisService;
import com.maxwang.buycar.redis.SuggestKey;
import com.maxwang.buycar.service.CarService;
import com.maxwang.buycar.service.EssayService;
import com.maxwang.buycar.service.UserService;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.Result;
import com.maxwang.buycar.util.ServiceResult;
import com.maxwang.buycar.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Tuple;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.naming.ResourceRef.SCOPE;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    CarService  carService;

    @Autowired
    EssayService essayService;

    @Autowired
    RedisService redisService;

    private  static Logger logger= LoggerFactory.getLogger(AdminController.class);


    @RequestMapping("/index")
    public String toAdmin(){

        return "/admin/test";
    }

    @RequestMapping("/login")
    public String adminLogin(){
        return "/admin/login";
    }

    @RequestMapping("/login_out")
    public String loginOut(HttpServletRequest request){
        HttpSession session = request.getSession();

        if (session != null){
            session.removeAttribute(WebConfig.LOGIN_USER);

        }


        return "/loginTest";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(LoginVo loginVo){

        logger.info(loginVo.toString());

        String passInput=loginVo.getPassword();
        String username=loginVo.getUsername();

        if (StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmpty(username)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }

        userService.AdminLogin(loginVo);

        return Result.success(true);
    }


    @RequestMapping("/showECharts")
    public String showeCharts(){
        return "/admin/echarts";
    }

    /**
     * 展示热搜结果
     * @param model
     * @return
     */
    @RequestMapping("/show")
    @ResponseBody
    public List<CarScope> show(Model model){
        //得到热搜数据
        List<Tuple> list = redisService.getRangeAll(SuggestKey.suggestKey,SCOPE,0,-1);
        List<String> valueList = new ArrayList<>();
        List<Double> scopeList = new ArrayList<>();


        List<CarScope> carScopeList = new ArrayList<>();
        for (Tuple t:
             list) {
            carScopeList.add(new CarScope(t.getElement(),t.getScore()));
            /*valueList.add(t.getElement());
            scopeList.add(t.getScore());*/
        }

        /*model.addAttribute("valueList",valueList);
        model.addAttribute("scopeList",scopeList);*/

        System.out.println("................carScopeList............."+carScopeList);

        model.addAttribute("carScopeList",carScopeList);

        return carScopeList;
    }

    @RequestMapping("/car/to_update/{id}")
    public String carToUpdate(Model model,@PathVariable("id") int id){
        Car car = carService.getCarById(id);
        model.addAttribute("car",car);
        return "/admin/carUpdate";
    }

    /**
     * 用户列表
     * @param model
     * @return
     */
    @RequestMapping("/user_list")
    public String userList(Model model) {

        List<User> userList = userService.getUserList();

        model.addAttribute("userList",userList);

        return "/admin/userList";
    }

    /**
     * 查询用户列表 对用户进行操作
     * @param model
     * @return
     */
    @RequestMapping("/user_manage")
    public String userManage(Model model){

        List<User> userList = userService.getUserList();

        model.addAttribute("userList",userList);

        return "/admin/userManage";
    }

    /**
     * 跳转到更新用户页面
     * @param id
     */
    @RequestMapping("/user_toUpdate/{id}")
    public String toUserUpdate(Model model, @PathVariable("id")Integer id){

        if (id == null){
            throw new GlobalException(CodeMsg.USERID_NOT_FOUND);
        }
        User user = userService.getById(id);
        model.addAttribute("user",user);
        return "/admin/userUpdate";
    }

    /**
     * 更改用户信息
     * @param user
     * @param id
     * @return
     */
    @RequestMapping("/user_doUpdate/{id}")
    public String doUserUpdate(@ModelAttribute User user, @PathVariable("id")Integer id){

        User user1 = userService.getById(id);

        if (user.getUsername() != null){
            user1.setUsername(user.getUsername());
        }
        if (user.getTel() != null){
            user1.setTel(user.getTel());
        }
        if (user.getRole()!=null){
            user1.setRole(user.getRole());
        }

        userService.userUpdate(user1);
        return "/admin/userManage";
    }

    /**
     * 根据ID删除用户
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    public String deleteUserById(@PathVariable("id")Integer id){
        User user = userService.getById(id);
        if (user==null){
            throw new GlobalException(CodeMsg.USERID_NOT_FOUND);
        }
        userService.deleteUser(id);
        return "redirect:/admin/user_manage";
    }

    /**
     * 管理员获取汽车列表
     * @param model
     * @return
     */
    @RequestMapping("/car_list")
    public String carList(Model model){

        List<Car> carList = carService.getAdminCarList();
        model.addAttribute("carList",carList);
        return "/admin/carList";
    }

    @RequestMapping("/car_manage")
    public String carManage(Model model){

        return "/admin/carInsert";
    }

    /**
     * 后台查询文章列表
     * @param model
     * @return
     */
    @RequestMapping("/essay_list")
    public String essayList(Model model){

        List<Essay> essayList = essayService.getEssayList();
        model.addAttribute("essayList",essayList);

        return "/admin/essayList";
    }

    /**
     * 文章管理
     * @param model
     * @return
     */
    @RequestMapping("/essay_manage")
    public String essayManage(Model model){
        return "/admin/essayInsert";
    }
}
