package com.maxwang.buycar.service;

import com.maxwang.buycar.dao.EvaluateDao;
import com.maxwang.buycar.domain.Evaluate;
import com.maxwang.buycar.exception.GlobalException;
import com.maxwang.buycar.util.CodeMsg;
import com.maxwang.buycar.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EvaluateService {

    @Autowired
    EvaluateDao evaluateDao;


    public Result<Boolean> updateEvaluate(Evaluate evaluate) {
        int result = evaluateDao.evaluateUpdate(evaluate);
        if (0 == result){
            throw new GlobalException(CodeMsg.EVALUATE_UPDATE_ERROR);
        }
        return Result.success(true);
    }

    public Result<Boolean> evaluateDelete(Integer carId) {
        int result = evaluateDao.evaluateDelete(carId);
        if (0 == result){
            throw new GlobalException(CodeMsg.EVALUATE_DELETE_ERROR);
        }
        return Result.success(true);
    }

}
