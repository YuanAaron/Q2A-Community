package com.oshacker.Q2ACommunity.async.handler;

import com.oshacker.Q2ACommunity.async.EventHandler;
import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    private SearchService searchService;

    @Override
    public void dohandle(EventModel eventModel) {
        try {
            searchService.indexQuestion(eventModel.getEntityId(),
                    eventModel.getExt("title"), eventModel.getExt("content"));
        } catch (Exception e) {
            logger.error("增加题目索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADDQUESTION);
    }
}
