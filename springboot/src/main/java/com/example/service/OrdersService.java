package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.MemberEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.entity.Orders;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper;
import com.example.mapper.OrdersMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单信息表业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增
     */
    public void add(Orders orders) {
        orders.setTime(DateUtil.now());
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));

        //价格
        Account currentUser = TokenUtils.getCurrentUser();
        User user = userMapper.selectById(currentUser.getId());
        Course course = courseMapper.selectById(orders.getCourseId());
        Double price = course.getPrice();
        if(MemberEnum.YES.info.equals(user.getMember())){ //是会员
            //会员价
            price = course.getPrice() * course.getDiscount() *0.1;
        }
        //判断余额是否足够
        if(price > user.getAccount()){
            throw new CustomException(ResultCodeEnum.BALANCE_NOT_ENOUGH);
        }
        //创建订单信息
        orders.setPrice(price);
        ordersMapper.insert(orders);
        //扣除余额
        user.setAccount(user.getAccount() - price);
        userMapper.updateById(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

}