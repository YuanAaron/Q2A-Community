package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.model.Question;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private static final String SOLR_URL="http://127.0.0.1:8983/solr/test";
    private SolrClient client=new HttpSolrClient.Builder(SOLR_URL).build();

    private static final String QUESTION_TITLE_FIELD="question_title";
    private static final String QUESTION_CONTENT_FIELD="question_content";

    public List<Question> searchQuestion(String keyword, int offset, int count, String hlPre, String hlPost) throws IOException, SolrServerException {
        SolrQuery query=new SolrQuery(keyword);
        query.setStart(offset);
        query.setRows(count);
        query.setHighlight(true);//开启高亮
        query.setHighlightSimplePre(hlPre);//设置前缀
        query.setHighlightSimplePost(hlPost);//设置后缀
        query.set("hl.fl",QUESTION_TITLE_FIELD+","+QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);

        List<Question> questionList=new ArrayList<>();
        //解析高亮的部分
        for (Map.Entry<String,Map<String, List<String>>> entry: response.getHighlighting().entrySet()) {
            Question question=new Question();
            question.setId(Integer.parseInt(entry.getKey()));

            Map<String,List<String>> value=entry.getValue();
            if (value.containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = value.get(QUESTION_TITLE_FIELD);
                if (titleList!=null) {
                    question.setTitle(titleList.get(0));
                }
            }

            if (value.containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = value.get(QUESTION_CONTENT_FIELD);
                if (contentList!=null) {
                    question.setContent(contentList.get(0));
                }
            }
            questionList.add(question);
        }

        return questionList;
    }

    //添加题目时，自动建立索引
    public boolean indexQuestion(int qid,String title,String content) throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", qid);
        document.addField(QUESTION_TITLE_FIELD, title);
        document.addField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(document);
        client.commit();
        return response!=null&&response.getStatus()==0;
    }
}
