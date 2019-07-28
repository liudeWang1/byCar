package com.maxwang.buycar.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.maxwang.buycar.dao.CarDao;
import com.maxwang.buycar.dao.EvaluateDao;
import com.maxwang.buycar.domain.Car;
import com.maxwang.buycar.domain.Evaluate;
import com.maxwang.buycar.dto.CarSearch;
import com.maxwang.buycar.dto.CarSearchDTO;
import com.maxwang.buycar.exception.GlobalException;
import com.maxwang.buycar.redis.CarKey;
import com.maxwang.buycar.redis.RedisService;
import com.maxwang.buycar.redis.ResultKey;
import com.maxwang.buycar.redis.SuggestKey;
import com.maxwang.buycar.service.search.CarIndexKey;
import com.maxwang.buycar.service.search.CarIndexTemplate;
import com.maxwang.buycar.service.search.SearchService;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.Result;
import com.maxwang.buycar.util.ServiceResult;
import com.maxwang.buycar.vo.CarCompleteVo;
import com.maxwang.buycar.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
public class CarService {

    @Autowired
    CarDao carDao;

    @Autowired
    RedisService redisService;

    @Autowired
    EvaluateDao evaluateDao;

    @Autowired
    SearchService searchService;


    private static final String CAR_IMG = "img";
    private static final String SCOPE = "scope";
    private static final String RESULT = "result";
    private static final String CAR_IMG_PREFIX = "http://";

    private static Logger logger = LoggerFactory.getLogger(CarService.class);

    /**
     * 增加汽车
     *
     * @param carVo
     * @return
     */
    @Transactional
    public boolean addCar(CarCompleteVo carVo) {

        Car car = new Car();

        car.setName(carVo.getName());
        car.setFirm(carVo.getFirm());
        car.setYear(carVo.getYear());
        car.setEngine(carVo.getEngine());
        car.setHorsepower(carVo.getHorsepower());
        car.setGearbox(carVo.getGearbox());
        car.setTorque(carVo.getTorque());
        car.setSize(carVo.getSize());
        car.setWeight(carVo.getWeight());
        car.setStructure(carVo.getStructure());
        car.setEnergy(carVo.getEnergy());
        car.setEmissions(carVo.getEmissions());
        car.setOil(carVo.getOil());
        car.setPrice(carVo.getPrice());

        //图片地址
        String imagePath = redisService.get(CarKey.carKey, CAR_IMG, String.class);
        String realPath = CAR_IMG_PREFIX + imagePath;

        logger.info("新增汽车图片imagePath:" + imagePath);

        logger.info("新增汽车图片realPath:" + realPath);
        car.setImage(realPath);

        int id = carDao.carAdd(car);

        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(CodeMsg.ADD_CAR_ERROR);
        }

        int carId = car.getId();

        logger.info("id:" + carId);

        int carOil = carVo.getCarOil();
        int carSpace = carVo.getCarSpace();
        int carHold = carVo.getCarHold();

        //添加到Evaluate表
        addEvaluate(carId, carOil, carSpace, carHold);

        //添加索引
        CarIndexTemplate carIndexTemplate = new CarIndexTemplate();

        List<String> tags = new ArrayList<>();
        // TODO tags
        if (carVo.getCarOil() >= 7) {
            tags.add("省油");
        }
        if (carVo.getCarHold() >= 7) {
            tags.add("运动");
        } else {
            tags.add("舒适");
        }
        if (carVo.getCarSpace() >= 7) {
            tags.add("空间大");
        }

        carIndexTemplate.setTags(tags);

        carIndexTemplate.setCarId(carId);
        carIndexTemplate.setName(car.getName());
        carIndexTemplate.setFirm(car.getFirm());
        carIndexTemplate.setStructure(car.getStructure());
        carIndexTemplate.setEnergy(car.getEnergy());
        carIndexTemplate.setPrice(car.getPrice());
        carIndexTemplate.setImage(car.getImage());


        Boolean result = searchService.create(carIndexTemplate);

        if (result == false) {
            return false;
        }

        return true;
    }

    /**
     * 添加到 标准库
     *
     * @param carId
     * @param carOil
     * @param carSpace
     * @param carHold
     * @return
     */
    private boolean addEvaluate(int carId, int carOil, int carSpace, int carHold) {
        Evaluate evaluate = new Evaluate();
        evaluate.setCarId(carId);
        evaluate.setCarOil(carOil);
        evaluate.setCarSpace(carSpace);
        evaluate.setCarHold(carHold);

        int evaluateId = evaluateDao.evaluateAdd(evaluate);

        logger.info("evaluateId:" + evaluateId);

        if (StringUtils.isEmpty(evaluateId)) {
            throw new GlobalException(CodeMsg.ADD_CAR_ERROR);
        }

        return true;
    }

    /**
     * 根据条件查找汽车
     *
     * @param minPrice
     * @param maxPrice
     * @param oil
     * @param space
     * @param hold
     * @return
     */
    public List<ResultVo> searchCar(int minPrice, int maxPrice, Integer oil, Integer space, Integer hold,String energy, String structure) {

        List<ResultVo> resultVoList = carDao.searchCar(minPrice, maxPrice, oil, space, hold,energy,structure);

        if (resultVoList == null) {
            throw new GlobalException(CodeMsg.SEARCH_ERROR);
        }
        return resultVoList;
    }



    public List<ResultVo> searchCarDTO(CarSearchDTO carSearchDTO) {

        String jsonObject = JSONObject.toJSONString(carSearchDTO);

        List<ResultVo> resultVoList = new ArrayList<>();
        List<String> stringList = redisService.lRange(ResultKey.resultKey,jsonObject,0,-1);
        if (stringList.size() == 0){
            resultVoList = carDao.searchCarDTO(carSearchDTO);
            for (ResultVo r:
                    resultVoList) {
                //将每条ResultVo数据转为Json
                redisService.lPush(ResultKey.resultKey,jsonObject,JSONObject.toJSONString(r));
            }

            System.out.println("。。。。。。。。。。。。调用数据库。。。。。。。。。。。。。。。");

        }else{
            for (String s:
                    stringList) {
                //将json串转化为ResultVo对象
                ResultVo resultVo = JSONObject.parseObject(s,ResultVo.class);
                resultVoList.add(resultVo);
            }
            System.out.println("。。。。。。。。。。。。未调用数据库。。。。。。。。。。。。。。。");
        }




        if (resultVoList == null) {
            throw new GlobalException(CodeMsg.SEARCH_ERROR);
        }
        return resultVoList;
    }

    /**
     * 根据ID查找汽车
     *
     * @param id
     * @return
     */
    public Car getCarById(int id) {
        Car car = carDao.getCarById(id);

        if (car == null) {
            throw new GlobalException(CodeMsg.SEARCH_ERROR);
        }
        return car;
    }

    /**
     * 根据汽车ID查找carCompleteVo
     * @param id
     * @return
     */
    public CarCompleteVo getCompleteVoById(int id) {
        CarCompleteVo carCompleteVo = carDao.getCarEvaluateById(id);

        if (carCompleteVo == null) {
            throw new GlobalException(CodeMsg.CARCOMPLETEVO_IS_NULL);
        }
        return carCompleteVo;
    }

    /**
     * 后台查找汽车列表
     *
     * @return
     */
    public List<Car> getAdminCarList() {

        List<Car> carList = carDao.getAdminCarList();
        if (carList == null) {
            throw new GlobalException(CodeMsg.SEARCH_ADMIN_CAR_ERROR);
        }
        return carList;
    }


    /**
     * 查询入口
     *
     * @param carSearch
     * @return
     */
    public List<ResultVo> query(CarSearch carSearch) {

        System.out.println("carSearch......."+carSearch.toString());

        if (carSearch.getKeywords() != null && !carSearch.getKeywords().isEmpty()) {
            logger.info("调用搜索引擎。。。。。。"+carSearch.getKeywords());
            List<Map<String, Object>> mapList = searchService.query(carSearch);

            //将关键字放入集合中,判断有序集合中是否有值
            if (redisService.getScope(SuggestKey.suggestKey,SCOPE,carSearch.getKeywords()) == null){
                redisService.zSet(SuggestKey.suggestKey,SCOPE,1.0,carSearch.getKeywords());
            }else {
                redisService.addScope(SuggestKey.suggestKey,SCOPE,1,carSearch.getKeywords());
            }

            return wrapperCarResult(mapList);
        }
        logger.info("调用数据库。。。。。。。。");
        return sQuery(carSearch);
    }

    /**
     * 包装es查询结果
     *
     * @param mapList
     * @return
     */
    private List<ResultVo> wrapperCarResult(List<Map<String, Object>> mapList) {

        List<ResultVo> resultVoList = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            ResultVo resultVo = new ResultVo();
            resultVo.setId((Integer) map.get(CarIndexKey.CAR_ID));
            resultVo.setFirm((String) map.get(CarIndexKey.FIRM));
            resultVo.setImage((String) map.get(CarIndexKey.IMAGE));
            resultVo.setPrice((Double) map.get(CarIndexKey.PRICE));
            resultVo.setName((String) map.get(CarIndexKey.NAME));

            resultVoList.add(resultVo);
        }
        return resultVoList;
    }

    /**
     * 查询热点数据
     * @return
     */
    public ServiceResult<List<String>> getScope(){

        Set<String> result = redisService.getRange(SuggestKey.suggestKey,SCOPE,0,4);

        List<String> list = Lists.newArrayList(result.toArray(new String[]{}));

        return ServiceResult.of(list);
    }

    /**
     * 不通过es的查询
     *
     * @param carSearch
     * @return
     */
    private List<ResultVo> simpleQuery(CarSearch carSearch) {

        int minPrice = 0;
        int maxPrice = 1000;

        if (carSearch.getPrice()== 0){
            minPrice = 0;
            maxPrice = 1000;
        } else if (carSearch.getPrice()== 8){
            maxPrice=8;
        }else if (carSearch.getPrice()== 12){
            minPrice = 8;
            maxPrice = 12;
        }else if (carSearch.getPrice()== 18){
            minPrice = 12;
            maxPrice = 18;
        }else if (carSearch.getPrice()== 25){
            minPrice = 18;
            maxPrice = 25;
        }else if (carSearch.getPrice()== 40){
            minPrice = 25;
            maxPrice = 40;
        }else if (carSearch.getPrice()== 80){
            minPrice = 40;
            maxPrice = 80;
        }else {
            minPrice = 80;
        }

        logger.info("minPrice.........."+minPrice);
        logger.info("maxPrice.........."+maxPrice);

        List<ResultVo> resultVoList= searchCar(minPrice, maxPrice, carSearch.getCarOil(), carSearch.getCarSpace(), carSearch.getCarHold(),carSearch.getEnergy(),carSearch.getStructure());

        return resultVoList;

    }

    private List<ResultVo> sQuery(CarSearch carSearch) {

        CarSearchDTO carSearchDTO = new CarSearchDTO();

        carSearchDTO.setMinPrice(0);
        carSearchDTO.setMaxPrice(1000);

        if (carSearch.getPrice()== 0){
            carSearchDTO.setMinPrice(0);
            carSearchDTO.setMaxPrice(1000);
        } else if (carSearch.getPrice()== 8){
            carSearchDTO.setMaxPrice(8);
        }else if (carSearch.getPrice()== 12){
            carSearchDTO.setMinPrice(8);
            carSearchDTO.setMaxPrice(12);
        }else if (carSearch.getPrice()== 18){
            carSearchDTO.setMinPrice(12);
            carSearchDTO.setMaxPrice(18);
        }else if (carSearch.getPrice()== 25){
            carSearchDTO.setMinPrice(18);
            carSearchDTO.setMaxPrice(25);
        }else if (carSearch.getPrice()== 40){
            carSearchDTO.setMinPrice(25);
            carSearchDTO.setMaxPrice(40);
        }else if (carSearch.getPrice()== 80){
            carSearchDTO.setMinPrice(40);
            carSearchDTO.setMaxPrice(80);
        }else {
            carSearchDTO.setMinPrice(80);
        }

        carSearchDTO.setCarHold(carSearch.getCarHold());
        carSearchDTO.setCarOil(carSearch.getCarOil());
        carSearchDTO.setCarSpace(carSearch.getCarSpace());
        carSearchDTO.setEnergy(carSearch.getEnergy());
        carSearchDTO.setStructure(carSearch.getStructure());

        List<ResultVo> resultVoList = searchCarDTO(carSearchDTO);

        return resultVoList;

    }

    /**
     * 根据汽车ID获取汽车详情
     *
     * @param carId
     * @return
     */
    public Car getCarByCarId(Integer carId) {

        Car car = carDao.getCarById(carId);
        if (car == null) {
            throw new GlobalException(CodeMsg.CAR_DETAIL_NOT_FOUND);
        }
        return car;
    }

    /**
     * 更新汽车
     * @param car
     * @return
     */
    public Result<Boolean> carUpdate(Car car) {
        int result = carDao.carUpdate(car);
        if (0 == result){
            throw new GlobalException(CodeMsg.CAR_UPDATE_ERROR);
        }
        return Result.success(true);
    }

    /**
     * 获取汽车标准
     * @param carId
     * @return
     */
    public Evaluate getEvaluate(Integer carId){
        Evaluate evaluate = evaluateDao.getEvaluateByCarId(carId);
        return evaluate;
    }


    public Result<Boolean> carDelete(Integer carId) {
        int result = carDao.carDelete(carId);
        if (0 == result){
            throw new GlobalException(CodeMsg.ESSAY_UPDATE_ERROR);
        }
        return Result.success(true);
    }
}
