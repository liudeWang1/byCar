package com.maxwang.buycar.dao;


import com.maxwang.buycar.domain.Car;
import com.maxwang.buycar.domain.Essay;
import com.maxwang.buycar.domain.Evaluate;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EvaluateDao {

    @Insert("insert into evaluate(id,car_id,car_oil,car_space,car_hold) values(#{id},#{carId},#{carOil},#{carSpace},#{carHold})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=int.class, before=false, statement="select last_insert_id()")
    int evaluateAdd(Evaluate evaluate);

    @Select("select * from evaluate e where car_id = #{car_id}")
    Evaluate getEvaluateByCarId(@Param("car_id")Integer carId);


    @Update("update evaluate set car_oil=#{carOil},car_space=#{carSpace},car_hold=#{carHold} where car_id=#{carId} ")
    int evaluateUpdate(Evaluate evaluate);

    @Delete("delete from evaluate where car_id=#{car_id}")
    int evaluateDelete(@Param("car_id")Integer carId);
}
