package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.model.ViewObject;
import com.oshacker.Q2ACommunity.service.FollowService;
import com.oshacker.Q2ACommunity.service.QuestionService;
import com.oshacker.Q2ACommunity.service.SearchService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private static final Logger logger= LoggerFactory.getLogger(SearchController.class);
    
    @Autowired
    private SearchService searchService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @RequestMapping(path={"/search"},method={RequestMethod.GET})
    public String index(Model model, @RequestParam("q") String keyword,
                        @RequestParam(value = "offset",defaultValue ="0") int offset,
                        @RequestParam(value = "count",defaultValue = "10") int count) {
        try {
            List<Question> questionList  = searchService.searchQuestion(keyword, offset,count,"<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                ViewObject vo = new ViewObject();

                Question q=questionService.selectById(question.getId()); //搜索得到的只有title和content
                String title=question.getTitle();
                String content=question.getContent();
                if (title!=null) {
                    q.setTitle(title); //使用搜索出来的标题
                }
                if (content!=null) {
                    q.setContent(content); //使用搜索出来的内容
                }
                vo.set("question",q);
                vo.set("user",userService.getUserById(q.getUserId()));
                vo.set("followCount", followService.getFollowerCount(ConstantUtil.ENTITY_QUESTION,q.getId()));
                vos.add(vo);
            }
            model.addAttribute("vos",vos);
            model.addAttribute("keyword",keyword);
        } catch (Exception e) {
            logger.error("搜索失败",e);
        }
        return "result";
    }
}
