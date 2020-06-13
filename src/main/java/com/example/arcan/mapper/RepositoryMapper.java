package com.example.arcan.mapper;

import com.example.arcan.dao.Repository;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RepositoryMapper {
    @Insert("insert into repository(userId, repoId, repoName, createDate, description, status)" +
            " values(#{userId}, #{repoId}, #{repoName}, #{createDate}, #{description}, #{status})")
    void createRepository(Repository repository);

    @Select("select * from repository where userId=#{userId} and status<>\'DELETED\' ")
    List<Repository> findRepoByUserId(String userId);

    @Select("select * from repository where repoId=#{repoId} and status<>\'DELETED\' ")
    Repository findRepoByRepoId(String repoId);

    @Update("update repository set repoName=#{repoName}, description=#{description}, status=#{status}" +
            "where userId=#{userId} and repoId=#{repoId}")
    void updateRepository(Repository repository);

    @Select("select * from repository where (repoName like '%${keyword}%' or description like '%${keyword}%') and userId = #{userId}")
    List<Repository> findRepoByKeyWord(@Param("userId") String userId, @Param("keyword") String keyword);
}
