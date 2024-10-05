package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RecommendEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.ScoreMapper;
import com.example.mapper.ScoreorderMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分课程信息表业务处理
 **/
@Service
public class ScoreService {

    @Resource
    private ScoreMapper scoreMapper;
    @Resource
    private ScoreorderMapper scoreorderMapper;

    /**
     * 新增
     */
    public void add(Score score) {
        if (RecommendEnum.YES.status.equals(score.getRecommend())) {
            // 校验一下积分专区表里面有没有已经推荐过的课程
            Score dbScore = scoreMapper.getRecommend();
            if (ObjectUtil.isNotEmpty(dbScore)) {
                throw new CustomException(ResultCodeEnum.RECOMMEND_ALREADY_ERROR);
            }
        }
        scoreMapper.insert(score);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        scoreMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            scoreMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Score score) {
        // 如果这个课程的推荐字段是：“是”，要去校验一下
        if (RecommendEnum.YES.status.equals(score.getRecommend())) {
            Score dbScore = scoreMapper.getRecommend();
            if (ObjectUtil.isNotEmpty(dbScore) && !dbScore.getId().equals(score.getId())) {
                throw new CustomException(ResultCodeEnum.RECOMMEND_ALREADY_ERROR);
            }
        }
        scoreMapper.updateById(score);
    }

    /**
     * 根据ID查询
     */
    public Score selectById(Integer id) {
        Score score = scoreMapper.selectById(id);
        Account currentUser = TokenUtils.getCurrentUser();
        Scoreorder orders = new Scoreorder();
        orders.setUserId(currentUser.getId());
        orders.setScoreId(id);
        List<Scoreorder> ordersList = scoreorderMapper.selectAll(orders);
        if (ObjectUtil.isEmpty(ordersList)) {
            score.setFile("");
            score.setVideo("");
        }
        return score;
    }

    /**
     * 查询所有
     */
    public List<Score> selectAll(Score score) {
        return scoreMapper.selectAll(score);
    }

    /**
     * 分页查询
     */
    public PageInfo<Score> selectPage(Score score, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Score> list = scoreMapper.selectAll(score);
        return PageInfo.of(list);
    }

    public Score getRecommend() {
        return scoreMapper.getRecommend();
    }

    public List<Score> selectTop8() {
        return scoreMapper.selectTop8();
    }

}