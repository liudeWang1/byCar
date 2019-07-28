package com.maxwang.buycar.controller;

import com.maxwang.buycar.domain.Car;
import com.maxwang.buycar.domain.Evaluate;
import com.maxwang.buycar.dto.CarSearch;
import com.maxwang.buycar.redis.RedisService;
import com.maxwang.buycar.service.CarService;
import com.maxwang.buycar.service.EvaluateService;
import com.maxwang.buycar.service.search.CarIndexTemplate;
import com.maxwang.buycar.service.search.SearchService;
import com.maxwang.buycar.util.ApiResponse;
import com.maxwang.buycar.util.Result;
import com.maxwang.buycar.util.ServiceResult;
import com.maxwang.buycar.vo.CarCompleteVo;
import com.maxwang.buycar.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/car")
public class CarController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CarService carService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private EvaluateService evaluateService;

    private static Logger logger = LoggerFactory.getLogger(CarController.class);


    @RequestMapping("index")
    public String carIndex() {
        return "/index";
    }

    @RequestMapping("to_list")
    public String carList() {
        return "/test00001";
    }

    @RequestMapping("to_add")
    public String ToAddCar() {
        return "/admin/carInsert";
    }

    @RequestMapping("test")
    public Result<Boolean> Test() {
        return Result.success(true);
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * 查询入口
     * @param carSearch
     * @param model
     * @return
     */
    @RequestMapping(value = "query")
    public String queryCar(@ModelAttribute CarSearch carSearch,Model model){

        List<ResultVo> resultVoList = carService.query(carSearch);

        model.addAttribute("list", resultVoList);

        logger.info("list:" + resultVoList.size());
        System.out.println(resultVoList.toString());

        return "test00001::table_refresh";
    }


    /**
     * 自动补全关键词
     * @param prefix
     * @return
     */
    @GetMapping("/query/autocomplete")
    @ResponseBody
    public ApiResponse autocomplete(@RequestParam(value = "prefix") String prefix){

        if (prefix.isEmpty()){
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult<List<String>> result = searchService.suggest(prefix);
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 热搜榜
     * @return
     */
    @GetMapping("/complete")
    @ResponseBody
    public ApiResponse complete(){
        ServiceResult<List<String>> listServiceResult = carService.getScope();
        return ApiResponse.ofSuccess(listServiceResult.getResult());
    }

    /**
     * 添加汽车
     * @param carVo
     * @return
     */
    @RequestMapping("addCar")
    @ResponseBody
    public Result<Boolean> addCar(CarCompleteVo carVo) {


        carService.addCar(carVo);

        return Result.success(true);
    }



    /**
     * 全款买车
     * @param model
     * @param
     * @return
     */
    @RequestMapping("/calcq")
    public String carCalcQ(Model model) {
        return "calc_q";
    }

    /**
     * 分期买车
     * @return
     */
    @RequestMapping("/calcf")
    public String carCalcF() {
        return "calc_f";
    }

    /**
     * 获取汽车参数详情
     * @param model
     * @param carId
     * @return
     */
    @RequestMapping("/detail/{id}")
    public String carDetail(Model model,@PathVariable("id") Integer carId){

        Car car = carService.getCarByCarId(carId);

        model.addAttribute("car",car);

        return "/carDetail";
    }

    /**
     * 跳转到更新页面
     * @param model
     * @param carId
     * @return
     */
    @RequestMapping("/to_update/{id}")
    public String toUpdate(Model model,@PathVariable("id") Integer carId){

        CarCompleteVo carCompleteVo = carService.getCompleteVoById(carId);
        Car car = carService.getCarById(carId);
        model.addAttribute("car",carCompleteVo);
        return "/admin/carUpdate";
    }

    /**
     * 更新汽车信息
     * @param carVo
     * @param carId
     * @return
     */
    @RequestMapping("/do_update/{id}")
    public String carUpdate(@ModelAttribute CarCompleteVo carVo,@PathVariable("id") Integer carId){
        Car car1 = carService.getCarById(carId);
        car1.setName(carVo.getName());
        car1.setFirm(carVo.getFirm());
        car1.setEnergy(carVo.getEnergy());
        car1.setPrice(carVo.getPrice());
        car1.setEmissions(carVo.getEmissions());
        car1.setStructure(carVo.getStructure());
        car1.setWeight(carVo.getWeight());
        car1.setSize(carVo.getSize());
        car1.setTorque(carVo.getTorque());
        car1.setOil(carVo.getOil());
        car1.setGearbox(carVo.getGearbox());
        car1.setHorsepower(carVo.getHorsepower());
        car1.setEngine(carVo.getEngine());
        car1.setYear(carVo.getYear());
        car1.setId(carId);

        carService.carUpdate(car1);

        //更新标准库
        Evaluate evaluate1 = carService.getEvaluate(carId);
        evaluate1.setCarId(carId);
        evaluate1.setCarHold(carVo.getCarHold());
        evaluate1.setCarOil(carVo.getCarOil());
        evaluate1.setCarSpace(carVo.getCarSpace());
        evaluateService.updateEvaluate(evaluate1);


        //TODO 更新索引
        CarIndexTemplate carIndexTemplate = new CarIndexTemplate();
        carIndexTemplate.setCarId(carId);
        carIndexTemplate.setImage(car1.getImage());
        carIndexTemplate.setEnergy(carVo.getEnergy());
        carIndexTemplate.setFirm(carVo.getFirm());
        carIndexTemplate.setStructure(carVo.getStructure());
        carIndexTemplate.setName(carVo.getName());
        carIndexTemplate.setPrice(carVo.getPrice());


        Evaluate evaluate = carService.getEvaluate(carId);
        List<String> tags = new ArrayList<>();
        if (evaluate.getCarOil() >= 7) {
            tags.add("省油");
        }
        if (evaluate.getCarHold() >= 7) {
            tags.add("运动");
        } else {
            tags.add("舒适");
        }
        if (evaluate.getCarSpace() >= 7) {
            tags.add("空间大");
        }

        carIndexTemplate.setTags(tags);

        searchService.update(carIndexTemplate);
        return "/admin/carList";
    }


    @RequestMapping("/delete/{id}")
    public String carDelete(@PathVariable("id") Integer carId){

        //TODO 应该把汽车和标准放到一个事务中
        carService.carDelete(carId);
        evaluateService.evaluateDelete(carId);
        //TODO 删除索引文档
        searchService.delete(carId);
        return "redirect:/admin/car_list";
    }
}
