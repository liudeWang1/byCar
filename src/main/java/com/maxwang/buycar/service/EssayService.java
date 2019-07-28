package com.maxwang.buycar.service;

import com.maxwang.buycar.dao.EssayDao;

import com.maxwang.buycar.domain.Essay;
import com.maxwang.buycar.exception.GlobalException;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.Result;
import com.maxwang.buycar.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EssayService {

    @Autowired
    EssayDao essayDao;

    /**
     * 添加文章
     * @param essay
     * @return
     */
    public boolean addEssay(Essay essay){

        int id = essayDao.essayAdd(essay);

        if (StringUtils.isEmpty(id)){
            throw new GlobalException(CodeMsg.ADD_CAR_ERROR);
        }
        return true;
    }

    public List<Essay> getEssayList(){

        List<Essay> essayList = essayDao.getEssayList();

        if (essayList == null){
            throw new GlobalException(CodeMsg.ESSAY_SEARCH_ERROR);
        }
        return essayList;
    }

    public Essay getEssayDetail(Integer essayId) {

        Essay result = essayDao.getContentById(essayId);

        if (result == null){
            throw new GlobalException(CodeMsg.ESSAY_SEARCH_DETAIL_ERROR);
        }
        return result;
    }

    public Essay getById(Integer essayId) {
        Essay essay = essayDao.getEssayById(essayId);
        if (essay == null){
            throw  new GlobalException(CodeMsg.ESSAY_NOT_EXIST);
        }
        return essay;
    }

    public Result<Boolean> updateEssay(Essay essay) {
        int result = essayDao.essayUpdate(essay);
        if (0 == result){
            throw new GlobalException(CodeMsg.ESSAY_UPDATE_ERROR);
        }
        return Result.success(true);
    }

    public Result<Boolean> essayDelete(Integer id) {
        int result = essayDao.essayDelete(id);
        if (0 == result){
            throw new GlobalException(CodeMsg.ESSAY_UPDATE_ERROR);
        }
        return Result.success(true);
    }
}
