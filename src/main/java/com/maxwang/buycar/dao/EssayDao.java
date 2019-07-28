package com.maxwang.buycar.dao;

import com.maxwang.buycar.domain.Essay;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EssayDao {

    @Insert("insert into essay(id,title,content) values(#{id},#{title},#{content})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=int.class, before=false, statement="select last_insert_id()")
    int essayAdd(Essay essay);

    @Select("select * from essay")
    List<Essay> getEssayList();


    @Select("select * from essay e where id = #{id}")
    Essay getContentById(Integer id);

    @Select("select * from essay e where id = #{id}")
    Essay getEssayById(@Param("id")Integer essayId);

    @Update("update essay set title=#{title},content=#{content} where id=#{id} ")
    int essayUpdate(Essay essay);

    @Delete("delete from essay where id=#{id}")
    int essayDelete(Integer id);
}
