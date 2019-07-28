package com.maxwang.buycar.dao;

import com.maxwang.buycar.domain.Car;
import com.maxwang.buycar.dto.CarSearchDTO;
import com.maxwang.buycar.vo.CarCompleteVo;
import com.maxwang.buycar.vo.ResultVo;
import org.apache.ibatis.annotations.*;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper
public interface CarDao {


    @Insert("insert into car(name,firm,year,engine,horsepower,gearbox,torque,size,weight,structure,energy,emissions,oil,price,image) values(#{name},#{firm},#{year},#{engine},#{horsepower},#{gearbox},#{torque},#{size},#{weight},#{structure},#{energy},#{emissions},#{oil},#{price},#{image})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=int.class, before=false, statement="select last_insert_id()")
    int carAdd(Car car);

    @SelectProvider(type = FindCar.class,method = "searchCar")
    List<ResultVo> searchCar(int minPrice, int maxPrice, Integer oil, Integer space, Integer hold,String energy, String structure);


    class FindCar{
        public String searchCar(int minPrice, int maxPrice, Integer oil, Integer space, Integer hold,String energy, String structure){

            String sql = "select c.id ,c.name, c.firm, c.price, c.image from car c inner join evaluate e on c.id = e.car_id ";

            if (0 != oil){
                sql += "and e.car_oil >  "+6+" ";
            }
            if (0 != space){
                sql += "and e.car_space >  "+7+" ";
            }
            if (0 != hold){
                sql += "and e.car_hold >  "+7+" ";
            }
            if (minPrice >= 0){
                sql += "and c.price > "+minPrice +" ";
            }
            if (maxPrice != 1000){
                sql += "and c.price < "+maxPrice +" ";
            }
            if (!energy.equals("不限")){
                sql+= "and c.energy = '"+energy+"' ";
            }
            if (!structure.equals("不限")){
                sql+= "and c.structure = '"+structure+"'";
            }
            return sql;
        }
    }



    @SelectProvider(type = FindCarVo.class,method = "searchCarDTO")
    List<ResultVo> searchCarDTO(CarSearchDTO carSearchDTO);


    class FindCarVo{
        public String searchCarDTO(CarSearchDTO carSearchDTO){

            String sql = "select c.id ,c.name, c.firm, c.price, c.image from car c inner join evaluate e on c.id = e.car_id ";

            if (0 != carSearchDTO.getCarOil()){
                sql += "and e.car_oil >  "+6+" ";
            }
            if (0 != carSearchDTO.getCarSpace()){
                sql += "and e.car_space >  "+7+" ";
            }
            if (0 != carSearchDTO.getCarHold()){
                sql += "and e.car_hold >  "+7+" ";
            }
            if (carSearchDTO.getMinPrice() >= 0){
                sql += "and c.price > "+carSearchDTO.getMinPrice() +" ";
            }
            if (carSearchDTO.getMaxPrice() != 1000){
                sql += "and c.price < "+carSearchDTO.getMaxPrice() +" ";
            }
            if (!carSearchDTO.getEnergy().equals("不限")){
                sql+= "and c.energy = '"+carSearchDTO.getEnergy()+"' ";
            }
            if (!carSearchDTO.getStructure().equals("不限")){
                sql+= "and c.structure = '"+carSearchDTO.getStructure()+"'";
            }
            return sql;
        }
    }



    @Select("select * from car where id = #{id}")
    Car getCarById(Integer id);


    @Select("select * from car")
    List<Car> getAdminCarList();

    @Update("update car set name=#{name},firm=#{firm},year=#{year},engine=#{engine},horsepower=#{horsepower},gearbox=#{gearbox},torque=#{torque},size=#{size},weight=#{weight},structure=#{structure},energy=#{energy},emissions=#{emissions},oil=#{oil},price=#{price} where id=#{id} ")
    int carUpdate(Car car);

    @Delete("delete from car where id=#{id}")
    int carDelete(@Param("id")Integer carId);

    @Select("SELECT c.`name`,c.firm,c.`year`,c.`engine`,c.horsepower,c.gearbox,c.torque,c.size,c.weight,c.structure,\n" +
            "c.energy,c.emissions,c.oil,c.price,c.image,\n" +
            "e.car_id,e.car_oil,e.car_hold,e.car_space\n" +
            "FROM car c INNER JOIN evaluate e ON c.id = e.car_id WHERE c.id=#{id} ")
    CarCompleteVo getCarEvaluateById(@Param("id")Integer carId);

}
