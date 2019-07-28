package com.maxwang.buycar.controller;

import com.maxwang.buycar.domain.Essay;
import com.maxwang.buycar.service.EssayService;
import com.maxwang.buycar.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/essay")
public class EssayController {

    @Autowired
    EssayService essayService;

    /**
     * 添加文章
     * @param title
     * @param content
     * @return
     */
    @RequestMapping("/addEssay")
    @ResponseBody
    public Result<Boolean> addEssay(@RequestParam("title") String title, @RequestParam("content") String content){

        Essay essay=new Essay();
        essay.setTitle(title);
        essay.setContent(content);
        essayService.addEssay(essay);

        return Result.success(true);
    }

    /**
     * 获得文章列表
     * @param model
     * @return
     */
    @RequestMapping("/list")
    public String essayList(Model model){

        List<Essay> essayList = essayService.getEssayList();
        model.addAttribute("essayList",essayList);

        return "essayList";
    }


    /**
     * 获取文章详情
     * @param model
     * @param essayId
     * @return
     */
    @RequestMapping("/detail/{id}")
    public String essayDetail(Model model,@PathVariable("id") Integer essayId){

        Essay essay = essayService.getEssayDetail(essayId);

        model.addAttribute("essay",essay);
        return "essayDetail";
    }

    /**
     * 跳转到修改文章页面
     * @param model
     * @param essayId
     * @return
     */
    @RequestMapping("/to_update/{id}")
    public String toUpdate(Model model,@PathVariable("id") Integer essayId){

        Essay essay = essayService.getById(essayId);
        model.addAttribute("essay",essay);

        return "/admin/essayUpdate";
    }

    /**
     * 更新文章
     * @param id
     * @param essay
     * @return
     */
    @RequestMapping("/do_update/{id}")
    public String doUpdate(@PathVariable("id")Integer id,@ModelAttribute Essay essay){

        Essay essay1 = essayService.getById(id);
        essay1.setTitle(essay.getTitle());
        essay1.setContent(essay.getContent());
        essay1.setId(id);
        essayService.updateEssay(essay1);
        return "/admin/essayList";
    }

    /**
     * 根据ID删除指定文章
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id")Integer id){
        Essay essay = essayService.getById(id);
        essayService.essayDelete(id);
        return "redirect:/admin/essay_list";
    }
}
