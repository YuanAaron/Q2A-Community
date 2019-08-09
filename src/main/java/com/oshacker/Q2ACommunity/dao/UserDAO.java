package com.oshacker.Q2ACommunity.dao;

import com.oshacker.Q2ACommunity.model.User;
import org.apache.ibatis.annotations.*;

/*
@Mapper注解的的作用
1.为了把mapper这个DAO交給Spring管理
2.为了不再写mapper映射文件
3.由Mybatis框架根据定义的接口来创建接口的动态代理对象（实现类）
*/

@Mapper
public interface UserDAO {

    String TABLE_NAME="user";
    String INSERT_FIELDS="name,password,salt,head_url";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
//    @Insert("insert into `user` values (null,#{name},#{password},#{salt},#{headUrl})")
//    @Options(useGeneratedKeys = true,keyProperty="id",keyColumn = "id")//返回自增主键（方法二）
    Integer addUser(User user);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    User selectById(Integer id);

    @Update({"update ",TABLE_NAME," set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(Integer id);
}
