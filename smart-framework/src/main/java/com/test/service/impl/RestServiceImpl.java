package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.domain.ResponseResult;
import com.test.domain.dto.RestDto;
import com.test.domain.entity.*;
import com.test.domain.vo.PageRestVo;
import com.test.domain.vo.RestInfoVo;
import com.test.mapper.*;
import com.test.service.RestService;
import com.test.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 申请休息表(Rest)表服务实现类
 *
 * @author makejava
 * @since 2023-03-14 16:07:32
 */
@Service("restService")
public class RestServiceImpl extends ServiceImpl<RestMapper, Rest> implements RestService {

    @Autowired
    UserRestMapper userRestMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserSchedulingMapper userSchedulingMapper;
    @Autowired
    DaysMapper daysMapper;

    @Override
    public ResponseResult getApply(Integer pageNum, Integer pageSize) {
        Long count = baseMapper.selectCount(null);
        Page<Rest> page = page(new Page<>(pageNum, pageSize), null);
        List<RestInfoVo> restInfoVos = BeanCopyUtils.copyBeanList(page.getRecords(), RestInfoVo.class);
        for (RestInfoVo restInfoVo : restInfoVos)
            restInfoVo.setName(userRestMapper.selectNameById(restInfoVo.getId()));
        PageRestVo pageRestVo = new PageRestVo(restInfoVos, count);
        return ResponseResult.okResult(pageRestVo);
    }

    @Override
    public ResponseResult allowApply(RestDto rest) {
        if (rest.getStartTime().endsWith(":00"))
            rest.setStartTime(rest.getStartTime().replace(":00", ".0"));
        else if (rest.getStartTime().endsWith(":30"))
            rest.setStartTime(rest.getStartTime().replace(":30", ".5"));
        if (rest.getEndTime().endsWith(":00"))
            rest.setEndTime(rest.getEndTime().replace(":00", ".0"));
        else if (rest.getEndTime().endsWith(":30"))
            rest.setEndTime(rest.getEndTime().replace(":30", ".5"));
        LambdaQueryWrapper<UserRest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRest::getRestId, rest.getId());
        UserRest userRest = userRestMapper.selectOne(wrapper);
        List<Days> daysList = userSchedulingMapper.selectDays(userRest.getUserId(), rest.getDate(), rest.getDate());
        for (Days days : daysList) {
            if (days.getMorningStart() != null &&
                    rest.getStartTime().equals(days.getMorningStart().toString()) &&
                    rest.getEndTime().equals(days.getMorningEnd().toString())) {
                days.setMorningStart(null);
                days.setMorningEnd(null);
            }
            if (days.getMorningSecondStart() != null &&
                    rest.getStartTime().equals(days.getMorningSecondStart().toString()) &&
                    rest.getEndTime().equals(days.getMorningSecondEnd().toString())) {
                days.setMorningSecondStart(null);
                days.setMorningSecondEnd(null);
            }
            if (days.getAfternoonStart() != null &&
                    rest.getStartTime().equals(days.getAfternoonStart().toString()) &&
                    rest.getEndTime().equals(days.getAfternoonEnd().toString())) {
                days.setAfternoonStart(null);
                days.setAfternoonEnd(null);
            }
            if (days.getAfternoonSecondStart() != null &&
                    rest.getStartTime().equals(days.getAfternoonSecondStart().toString()) &&
                    rest.getEndTime().equals(days.getAfternoonEnd().toString())) {
                days.setAfternoonSecondStart(null);
                days.setAfternoonSecondEnd(null);
            }
            if (days.getEveningEnd() != null &&
                    rest.getStartTime().equals(days.getEveningStart().toString()) &&
                    rest.getEndTime().equals(days.getEveningEnd().toString())) {
                days.setEveningStart(null);
                days.setEveningEnd(null);
            }
            if (days.getEveningSecondEnd() != null &&
                    rest.getStartTime().equals(days.getEveningSecondStart().toString()) &&
                    rest.getEndTime().equals(days.getEveningSecondEnd().toString())) {
                days.setEveningSecondStart(null);
                days.setEveningSecondEnd(null);
            }
            if (days.getMorningEnd() == null &&
                    days.getMorningSecondEnd() == null &&
                    days.getAfternoonEnd() == null &&
                    days.getAfternoonSecondEnd() == null &&
                    days.getEveningEnd() == null &&
                    days.getEveningSecondEnd() == null) {
                daysMapper.deleteById(days);
                LambdaQueryWrapper<UserScheduling> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(UserScheduling::getSchedulingId, days.getId());
                userSchedulingMapper.delete(wrapper1);
                baseMapper.deleteById(rest.getId());
            } else {
                daysMapper.updateById(days);
                baseMapper.deleteById(rest.getId());
            }
        }
        return ResponseResult.okResult();
    }
}
