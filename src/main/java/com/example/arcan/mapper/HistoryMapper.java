package com.example.arcan.mapper;

import com.example.arcan.dao.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HistoryMapper {
    @Insert("insert into history(repoId, projectId, createTime, resultId) " +
            "values(#{repoId}, #{projectId}, #{createTime}, #{resultId})")
    void createOneHistory(History history);

    @Select("select * from history where projectId=#{projectId}")
    History getHistoryById(String projectId);

    @Select("select * from history where repoId=#{repoId} order by createTime desc")
    List<History> getHistoryList(String repoId);
}
