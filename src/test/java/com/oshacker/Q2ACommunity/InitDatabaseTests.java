package com.oshacker.Q2ACommunity;

import com.oshacker.Q2ACommunity.dao.QuestionDAO;
import com.oshacker.Q2ACommunity.dao.UserDAO;
import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Sql("/init-schema.sql") //新建表，存在会覆盖
//@MapperScan("com.oshacker.Q2ACommunity.dao") //有了它，dao中的接口不再需要@Mapper
public class InitDatabaseTests {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private QuestionDAO questionDAO;

	@Test
	public void initDatabase() {
		Random random=new Random();

		for (int i=1;i<=10;i++) {
			User user = new User();
			//牛客头像API：http://images.nowcoder.com/head/%dt.png
            //Github头像API：https://avatars.githubusercontent.com/u/%d
			user.setHeadUrl(String.format("https://avatars.githubusercontent.com/u/%d", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("yy");
			user.setSalt("");
			userDAO.addUser(user);

			user.setPassword("xx");
			userDAO.updatePassword(user);

			Question question=new Question();
			question.setCommentCount(i);
			Date date=new Date();
			date.setTime(date.getTime()+1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i);
			question.setTitle(String.format("TITLE{%d}",i));
			question.setContent(String.format("Balalalalaala Content %d",i));
			questionDAO.addQuestion(question);
		}

		Assert.assertEquals("xx",userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));

        //System.out.println(questionDAO.selectLatestQuestions(0,0,10));
	}

}
