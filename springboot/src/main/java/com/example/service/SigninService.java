package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Signin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.SigninMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 签到信息表业务处理
 **/
@Service
public class SigninService {

    @Resource
    private SigninMapper signinMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 新增
     */
    public void add(Signin signin) {
        //获取今天时间
        String today = DateUtil.format(new Date(),"yyyy-MM-dd");
        //判断用户今天是否签到
        Signin todaySignin = signinMapper.selectByUserIdAndDay(signin.getUserId(), today);
        if(ObjectUtil.isNotEmpty(todaySignin)){
            throw new CustomException(ResultCodeEnum.SIGN_ALREADY_ERROR);
        }
        //查询用户是否有签到记录
        Signin record = signinMapper.selectByUserId(signin.getUserId());
        if(ObjectUtil.isNotEmpty(record)){ //有数据
            //把签到数据更新
            record.setTime(DateUtil.now());
            record.setDay(today);
            signinMapper.updateById(record);
        }else{ //没数据
            //新增数据
            signin.setTime(DateUtil.now());
            signin.setDay(today);
            signinMapper.insert(signin);
        }
        //签到成功加10积分
        User user = userMapper.selectById(signin.getUserId());
        user.setScore(user.getScore() + 10);
        userMapper.updateById(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        signinMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            signinMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Signin signin) {
        signinMapper.updateById(signin);
    }

    /**
     * 根据ID查询
     */
    public Signin selectById(Integer id) {
        return signinMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Signin> selectAll(Signin signin) {
        return signinMapper.selectAll(signin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Signin> selectPage(Signin signin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Signin> list = signinMapper.selectAll(signin);
        return PageInfo.of(list);
    }

    public Signin selectByUserId(Integer id) {
        return signinMapper.selectByUserId(id);
    }
}