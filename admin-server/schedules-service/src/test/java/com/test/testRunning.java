package com.test;//package com.test;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.test.domain.entity.*;
//import com.test.domain.vo.UserStatusVo;
//import com.test.mapper.*;
//import com.utils.BeanCopyUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.sql.Date;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//public class testRunning {
//
//    @Resource
//    AiDataMapper aiDataMapper;
//    @Resource
//    InfoMapper infoMapper;
//    @Resource
//    SimpleRuleMapper simpleRuleMapper;
//    @Resource
//    RuleMapper ruleMapper;
//    @Resource
//    InfoUserMapper infoUserMapper;
//    @Resource
//    UserMapper userMapper;
//    @Resource
//    InfoRuleMapper infoRuleMapper;
//
//    @Test
//    void testRandom() {
//        for (int i = 0; i <= 100; i++)
//            test();
//    }
//
//    /**
//     * 随机生成一个店的排班
//     */
//    @Test
//    void test() {
//        // 假设为给进来门店id
//        Long id = 2L;
//        // 获取今天的时间,这里是测试所以自定义了
//        // LocalDate localDate = LocalDate.now();
//        // LocalDate 转成当前星期号/
//        // ZoneId zone = ZoneId.systemDefault();
//        // Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
//        // java.util.Date date = Date.from(instant);
//        // Calendar calendar = Calendar.getInstance();
//        // calendar.setTime(date);
//        // int index = calendar.get(Calendar.DAY_OF_WEEK)-1;
//        String testDate = "2023-05-10";
//        // 查数据库查看今天的数据
//        LambdaQueryWrapper<AiData> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(AiData::getData, testDate);
//        AiData aiData = aiDataMapper.selectOne(wrapper);
//        // 查看固定规则,因为每个门店都一样 如果改也是原来这条上面改
//        SimpleRule simpleRule = simpleRuleMapper.selectById(1L);
//        // 查询门店所有的员工信息
//        LambdaQueryWrapper<InfoUser> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(InfoUser::getInfoId, id);
//        List<InfoUser> infoUsers = infoUserMapper.selectList(queryWrapper);
//        // 开始排班
//        // 先把今天不爱来的人给过滤掉
//        // 1.获取当前星期号
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(Date.valueOf(testDate));
//        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//        // 2.过滤掉今天不来的
//        List<InfoUser> workUsers = infoUsers.stream()
//                .filter(infoUser ->
//                        userMapper.selectById(infoUser.getUserId()).getDayPreference().contains(String.valueOf(week)))
//                .collect(Collectors.toList());
//        //找出当天来上班的并且三个时段的人
//        List<InfoUser> morningWorker = workUsers.stream()
//                .filter(infoUser ->
//                        userMapper.selectById(infoUser.getUserId()).getTimePreference().contains("1"))
//                .collect(Collectors.toList());
//        List<UserStatusVo> morningStatus = BeanCopyUtils.copyBeanList(morningWorker, UserStatusVo.class);
//        List<InfoUser> afternoonWorker = workUsers.stream()
//                .filter(infoUser ->
//                        userMapper.selectById(infoUser.getUserId()).getTimePreference().contains("2"))
//                .collect(Collectors.toList());
//        List<UserStatusVo> afternoonStatus = BeanCopyUtils.copyBeanList(afternoonWorker, UserStatusVo.class);
//        List<InfoUser> eveningWorker = workUsers.stream()
//                .filter(infoUser ->
//                        userMapper.selectById(infoUser.getUserId()).getTimePreference().contains("3"))
//                .collect(Collectors.toList());
//        List<UserStatusVo> eveningStatus = BeanCopyUtils.copyBeanList(eveningWorker, UserStatusVo.class);
//        // 根据规则把开门和关门的时间算出来，然后再根据今天是否是周末算出要多少人
//        // 1.查出对应规则的id
//        LambdaQueryWrapper<InfoRule> wrapper1 = new LambdaQueryWrapper<>();
//        wrapper1.eq(InfoRule::getInfoId, id);
//        InfoRule infoRule = infoRuleMapper.selectOne(wrapper1);
//        // 2.根据id查出对应早上的规则
//        Rule rule = ruleMapper.selectById(infoRule.getRuleId());
//        String startRule = rule.getStartRule();
//        String start[] = startRule.split(",");
//        double earlyTime = Double.parseDouble(start[0]);
//        int earlyNum = new Double(
//                Math.ceil((infoMapper.selectById(id).getSize() * 100) / (Double.parseDouble(start[1]) * 100))).intValue();
//        // 3.根据id制订下班规则
//        String endRule = rule.getEndRule();
//        String end[] = endRule.split(",");
//        double leaveTime = Double.parseDouble(end[0]);
//        int provisionNum = Integer.parseInt(end[1]);
//        int leaveNum = new Double(Math.ceil(
//                (infoMapper.selectById(id).getSize() * 100) / (Double.parseDouble(end[2]) * 100))).intValue() + provisionNum;
//        // 根据规则 确定上班和下班的时间
//        double startTime;
//        double endTime;
//        if (week == 6 || week == 0) {
//            startTime = simpleRule.getWeekendStart() - earlyTime;
//            endTime = leaveTime + simpleRule.getWeekendEnd();
//        } else {
//            startTime = simpleRule.getWeekStart() - earlyTime;
//            endTime = leaveTime + simpleRule.getWeekEnd();
//        }
//        if (startTime < 8.0) throw new RuntimeException("开门时间不能早过8点");
//        if (startTime > 9.0) throw new RuntimeException("开门时间不能晚于9点");
//        double i = startTime;
//        double flowRule = Double.parseDouble(rule.getFlowRule()) * 100;
//        int workNum;
//        while (i <= 12.0) {
//            // ①上午？-12 根据权限信息，安排相应的工作
//            // 1.查看喜欢早上上班的人 如果只喜欢早上不喜欢下午，则不安排超过12点的排班
//            // 2.按半小时的时间来检查排班的人员是否充足
//            // 3.早上不能安排一直干到13点的员工，需要给他们吃饭
//            // 4.如果在某一时段员工安排的太多了,需求量没那么大,就让工作时间最长的那名员工下班，以此类推
//            if (startTime == 8.0) {
//                if (i == 8.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int eightFirstNum = new Double(Math.ceil((aiData.getEightFirst() * 100) / flowRule)).intValue();
//                    workNum = earlyNum + eightFirstNum;
//                    if (workNum == 0) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 8.5) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int eightLastNum = new Double(Math.ceil((aiData.getEightLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) if (status.getStatus() == 1) count += 1;
//                    if (count >= eightLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = eightLastNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId)) {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        } else {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        }
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//
//                } else if (i == 9.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineFirstNum = new Double(Math.ceil((aiData.getNineFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) if (status.getStatus() == 1) count += 1;
//                    if (count >= nineFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = nineFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId)) {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        } else {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        }
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 9.5) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineLastNum = new Double(Math.ceil((aiData.getNineLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) if (status.getStatus() == 1) count += 1;
//                    if (count >= nineLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = nineLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId)) {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        } else {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        }
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int tenFirstNum = new Double(Math.ceil((aiData.getTenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = tenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId)) {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        } else {
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        }
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.5) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int tenLastNum = new Double(Math.ceil((aiData.getTenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = tenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                } else if (i == 11.0) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenFirstNum = new Double(Math.ceil((aiData.getElevenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                } else if (i == 11.5) {
//                    if (simpleRule.getLunchEnd() - i > simpleRule.getLunchTime()) {
//                        i += 0.5;
//                        break;
//                    }
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenLastNum = new Double(Math.ceil((aiData.getElevenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                }
//            } else if (startTime == 8.5) {
//                if (i == 8.5) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int eightLastNum = new Double(Math.ceil((aiData.getEightLast() * 100) / flowRule)).intValue();
//                    workNum = earlyNum + eightLastNum;
//                    if (workNum == 0) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 9.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineFirstNum = new Double(Math.ceil((aiData.getNineFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= nineFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineFirstNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 9.5) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineLastNum = new Double(Math.ceil((aiData.getNineLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= nineLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineLastNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int tenFirstNum = new Double(Math.ceil((aiData.getTenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = tenFirstNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.5) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int tenLastNum = new Double(Math.ceil((aiData.getTenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = tenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 11.0) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenFirstNum = new Double(Math.ceil((aiData.getElevenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                } else if (i == 11.5) {
//                    if (simpleRule.getLunchEnd() - i > simpleRule.getLunchTime()) {
//                        i += 0.5;
//                        break;
//                    }
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenLastNum = new Double(Math.ceil((aiData.getElevenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                }
//            } else if (startTime == 9.0) {
//                if (i == 9.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineFirstNum = new Double(Math.ceil((aiData.getNineFirst() * 100) / flowRule)).intValue();
//                    workNum = nineFirstNum + earlyNum;
//                    if (workNum == 0) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 9.5) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineLastNum = new Double(Math.ceil((aiData.getNineLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= nineLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineLastNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.0) {
//                    if (isHaveMan(morningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int tenFirstNum = new Double(Math.ceil((aiData.getTenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = tenFirstNum - count;
//                    // 开始安排人
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(morningStatus)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong;
//                        if (isContinues(morningStatus, afternoonStatus, workerId))
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        else
//                            workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 10.5) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int tenLastNum = new Double(Math.ceil((aiData.getTenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= tenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = tenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 11.0) {
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenFirstNum = new Double(Math.ceil((aiData.getElevenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        morningStatus.get(workerId).setMorningStart(i);
//                        morningStatus.get(workerId).setMorningEnd(i + workLong);
//                    }
//                } else if (i == 11.5) {
//                    if (simpleRule.getLunchEnd() - i > simpleRule.getLunchTime()) {
//                        i += 0.5;
//                        break;
//                    }
//                    if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                    // 根据公式决定要来的人数
//                    int elevenLastNum = new Double(Math.ceil((aiData.getElevenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo status : morningStatus) {
//                        if (isEndMorning(i, status)) status.setStatus(0);
//                        if (status.getStatus() == 1) count += 1;
//                    }
//                    if (count >= elevenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    // 开始安排人
//                    workNum = elevenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManPlusToMorning(i,morningStatus, afternoonStatus, simpleRule)) break;
//                        // 从早上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * morningWorker.size());
//                        while (morningStatus.get(workerId).getStatus() == 1 ||
//                                !isContinues(morningStatus, afternoonStatus, workerId) ||
//                                !isReadyMorning(i, workerId, morningStatus, simpleRule))
//                            workerId = (int) (Math.random() * morningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        morningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (morningStatus.get(workerId).getMorningEnd() == null) {
//                            morningStatus.get(workerId).setMorningStart(i);
//                            morningStatus.get(workerId).setMorningEnd(i + workLong);
//                        } else {
//                            morningStatus.get(workerId).setMorningSecondStart(i);
//                            morningStatus.get(workerId).setMorningSecondEnd(i + workLong);
//                        }
//                    }
//                }
//            }
//            i += 0.5;
//        }
//        while (i >= 12.0 && i < 18.0) {
//            if (i == 12.0) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int twelveFirstNum = new Double(Math.ceil((aiData.getTwelveFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo status : morningStatus) {
//                    if (isEndMorning(i, status)) status.setStatus(0);
//                    if (status.getStatus() == 1) count += 1;
//                }
//                if (count >= twelveFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = twelveFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            isTrue(workerId, morningStatus, afternoonStatus) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    afternoonStatus.get(workerId).setAfternoonStart(i);
//                    afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                }
//            } else if (i == 12.5) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int twelveLastNum = new Double(Math.ceil((aiData.getTwelveLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo status : morningStatus) {
//                    if (isEndMorning(i, status)) status.setStatus(0);
//                    if (status.getStatus() == 1) count += 1;
//                }
//                for (UserStatusVo statusVo : afternoonStatus) if (statusVo.getStatus() == 1) count += 1;
//                if (count >= twelveLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = twelveLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            isTrue(workerId, morningStatus, afternoonStatus) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    afternoonStatus.get(workerId).setAfternoonStart(i);
//                    afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                }
//            } else if (i == 13.0) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int thirteenFirstNum = new Double(Math.ceil((aiData.getThirteenFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo status : morningStatus) {
//                    if (isEndMorning(i, status)) status.setStatus(0);
//                    if (status.getStatus() == 1) count += 1;
//                }
//                for (UserStatusVo statusVo : afternoonStatus) if (statusVo.getStatus() == 1) count += 1;
//                if (count >= thirteenFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = thirteenFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            isTrue(workerId, morningStatus, afternoonStatus) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    afternoonStatus.get(workerId).setAfternoonStart(i);
//                    afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                }
//            } else if (i == 13.5) { // 从13.5开始应没有早上员工在上班 他们需要吃饭
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int thirteenLastNum = new Double(Math.ceil((aiData.getThirteenLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) if (statusVo.getStatus() == 1) count += 1;
//                if (count >= thirteenLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = thirteenLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    afternoonStatus.get(workerId).setAfternoonStart(i);
//                    afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                }
//            } else if (i == 14.0) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int fourteenFirstNum = new Double(Math.ceil((aiData.getFourteenFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= fourteenFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = fourteenFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    afternoonStatus.get(workerId).setAfternoonStart(i);
//                    afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                }
//            } else if (i == 14.5) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int fourteenLastNum = new Double(Math.ceil((aiData.getFourteenLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= fourteenLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = fourteenLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong;
//                    if (isContinues(afternoonStatus, eveningStatus, workerId))
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    else
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 15.0) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int fifteenFirstNum = new Double(Math.ceil((aiData.getFifteenFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= fifteenFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = fifteenFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong;
//                    if (isContinues(afternoonStatus, eveningStatus, workerId))
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    else
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 15.5) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int fifteenLastNum = new Double(Math.ceil((aiData.getFifteenLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= fifteenLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = fifteenLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong;
//                    if (isContinues(afternoonStatus, eveningStatus, workerId))
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                    else
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 16.0) {
//                if (isHaveMan(afternoonStatus)) break;
//                // 根据公式决定要来的人数
//                int sixteenFirstNum = new Double(Math.ceil((aiData.getSixteenFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= sixteenFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = sixteenFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManToWork(i, morningStatus, afternoonStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong;
//                    if (isContinues(afternoonStatus, eveningStatus, workerId))
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                    else
//                        workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 16.5) {
//                if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                // 根据公式决定要来的人数
//                int sixteenLastNum = new Double(Math.ceil((aiData.getSixteenLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= sixteenLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = sixteenLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isContinues(afternoonStatus, eveningStatus, workerId) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2 - 1) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 17.0) {
//                if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                // 根据公式决定要来的人数
//                int seventeenFirstNum = new Double(Math.ceil((aiData.getSeventeenFirst() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= seventeenFirstNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = seventeenFirstNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isContinues(afternoonStatus, eveningStatus, workerId) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            } else if (i == 17.5) {
//                if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                // 根据公式决定要来的人数
//                int seventeenLastNum = new Double(Math.ceil((aiData.getSeventeenLast() * 100) / flowRule)).intValue();
//                // 获取在职员工数
//                int count = 0;
//                for (UserStatusVo statusVo : afternoonStatus) {
//                    if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                    if (statusVo.getStatus() == 1) count += 1;
//                }
//                if (count >= seventeenLastNum) {
//                    i += 0.5;
//                    continue;
//                }
//                workNum = seventeenLastNum - count;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManPlusToAfternoon(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                    // 从下午来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (afternoonStatus.get(workerId).getStatus() == 1 ||
//                            !isContinues(afternoonStatus, eveningStatus, workerId) ||
//                            !isReadyAfternoon(i, workerId, morningStatus, afternoonStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    afternoonStatus.get(workerId).setStatus(1);
//                    Double singleSmall = simpleRule.getSingleSmall();
//                    Double singleTop = simpleRule.getSingleTop();
//                    double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                    if (afternoonStatus.get(workerId).getAfternoonStart() == null) {
//                        afternoonStatus.get(workerId).setAfternoonStart(i);
//                        afternoonStatus.get(workerId).setAfternoonEnd(i + workLong);
//                    } else {
//                        afternoonStatus.get(workerId).setAfternoonSecondStart(i);
//                        afternoonStatus.get(workerId).setAfternoonSecondEnd(i + workLong);
//                    }
//                }
//            }
//            i += 0.5;
//        }
//        if (week == 6 || week == 0) {
//            // ③晚上18-？ 根据权限信息，安排相应的工作
//            // 1.查看喜欢晚上上班的人
//            // 2.晚餐是17-20所以不用考虑晚上能干活的人
//            // 3.开始按小时的时间来检查排班的人员是否充足
//            // 4.如果在某一时段员工安排的太多了,需求量没那么大,就让工作时间最长的那名员工下班，以此类推
//            // ④关店时，根据公式算出需要多少人留店多少时间 根据权限信息，安排相应的工作
//            // 1.先看关门时员工的最短工作时长，如果加上打扫卫生时间>4则直接让他下班，否则继续工作 以此类推
//            // 2.如果人手不够 则安排新的职员回来打扫
//            while (i >= 18.0 && i < simpleRule.getWeekendEnd()) {
//                if (i == 18.0) {
//                    // 根据公式决定要来的人数
//                    int eighteenFirstNum = new Double(Math.ceil((aiData.getEighteenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    if (count >= eighteenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = eighteenFirstNum - count;
//                    if (isHaveMan(eveningStatus)) break;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 + 1) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 18.5) {
//                    // 根据公式决定要来的人数
//                    int eighteenLastNum = new Double(Math.ceil((aiData.getEighteenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    for (UserStatusVo statusVo : eveningStatus) if (statusVo.getStatus() == 1) count += 1;
//                    if (count >= eighteenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = eighteenLastNum - count;
//                    if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(eveningStatus)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 19.0) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineteenFirstNum = new Double(Math.ceil((aiData.getNineteenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    for (UserStatusVo statusVo : eveningStatus) if (statusVo.getStatus() == 1) count += 1;
//                    if (count >= nineteenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineteenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 19.5) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineteenLastNum = new Double(Math.ceil((aiData.getNineteenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    for (UserStatusVo statusVo : eveningStatus) if (statusVo.getStatus() == 1) count += 1;
//                    if (count >= nineteenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineteenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 20.0) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int twentyFirstNum = new Double(Math.ceil((aiData.getTwentyFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : eveningStatus) {
//                        if (isEndEvening(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    if (count >= twentyFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = twentyFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveMan(eveningStatus)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        if (eveningStatus.get(workerId).getEveningStart() == null) {
//                            eveningStatus.get(workerId).setEveningStart(i);
//                            eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                        } else {
//                            eveningStatus.get(workerId).setEveningSecondStart(i);
//                            eveningStatus.get(workerId).setEveningSecondEnd(i + workLong);
//                        }
//                    }
//                }
//                i += 0.5;
//            }
//            while ((i >= simpleRule.getWeekendEnd() - 2.0) && i < endTime) {
//                if (isHaveMan(eveningStatus)) break;
//                // 把所有员工状态置0，这里不需要status了已经！
//                for (UserStatusVo statusVo : eveningStatus)
//                    statusVo.setStatus(0);
//                workNum = leaveNum;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManForClean(leaveTime, simpleRule.getWeekendEnd() - 2.0, eveningStatus, simpleRule))
//                        break;
//                    // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * afternoonWorker.size());
//                    while (eveningStatus.get(workerId).getStatus() == 1 ||
//                            !isContinuesToClean(workerId, leaveTime, simpleRule.getWeekendEnd() - 2.0, eveningStatus, simpleRule))
//                        workerId = (int) (Math.random() * afternoonWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    eveningStatus.get(workerId).setStatus(1);
//                    if (eveningStatus.get(workerId).getEveningStart() == null) {
//                        eveningStatus.get(workerId).setEveningStart(simpleRule.getWeekEnd());
//                        eveningStatus.get(workerId).setEveningEnd(endTime);
//                    } else {
//                        eveningStatus.get(workerId).setEveningSecondStart(simpleRule.getWeekEnd());
//                        eveningStatus.get(workerId).setEveningSecondEnd(endTime);
//                    }
//                }
//                i += leaveTime;
//            }
//        } else {
//            while (i >= 18.0 && i < simpleRule.getWeekEnd()) {
//                if (i == 18.0) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int eighteenFirstNum = new Double(Math.ceil((aiData.getEighteenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    if (count >= eighteenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = eighteenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 1) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 18.5) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int eighteenLastNum = new Double(Math.ceil((aiData.getEighteenLast() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    for (UserStatusVo statusVo : eveningStatus) if (statusVo.getStatus() == 1) count += 1;
//                    if (count >= eighteenLastNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = eighteenLastNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 2) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                } else if (i == 19.0) {
//                    if (isHaveMan(eveningStatus)) break;
//                    // 根据公式决定要来的人数
//                    int nineteenFirstNum = new Double(Math.ceil((aiData.getNineteenFirst() * 100) / flowRule)).intValue();
//                    // 获取在职员工数
//                    int count = 0;
//                    for (UserStatusVo statusVo : afternoonStatus) {
//                        if (isEndAfternoon(i, statusVo)) statusVo.setStatus(0);
//                        if (statusVo.getStatus() == 1) count += 1;
//                    }
//                    for (UserStatusVo statusVo : eveningStatus) if (statusVo.getStatus() == 1) count += 1;
//                    if (count >= nineteenFirstNum) {
//                        i += 0.5;
//                        continue;
//                    }
//                    workNum = nineteenFirstNum - count;
//                    for (int j = 0; j < workNum; j++) {
//                        if (isHaveManToWorkEvening(i, afternoonStatus, eveningStatus, simpleRule)) break;
//                        // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                        int workerId = (int) (Math.random() * eveningWorker.size());
//                        while (eveningStatus.get(workerId).getStatus() == 1 ||
//                                isTrue(workerId, afternoonStatus, eveningStatus) ||
//                                !isReadyEvening(i, workerId, afternoonStatus, eveningStatus, simpleRule))
//                            workerId = (int) (Math.random() * eveningWorker.size());
//                        // 把他的状态设置为工作中，然后给他安排工作的时长
//                        eveningStatus.get(workerId).setStatus(1);
//                        Double singleSmall = simpleRule.getSingleSmall();
//                        Double singleTop = simpleRule.getSingleTop();
//                        double workLong = (int) (Math.random() * ((singleTop - singleSmall) * 2 - 3) + singleSmall * 2) / 2.0;
//                        eveningStatus.get(workerId).setEveningStart(i);
//                        eveningStatus.get(workerId).setEveningEnd(i + workLong);
//                    }
//                }
//                i += 0.5;
//            }
//            while ((i >= simpleRule.getWeekEnd() - 2.0) && i < endTime) {
//                if (isHaveMan(eveningStatus)) break;
//                // 获取在职员工数
//                // 把所有员工状态置0，这里不需要status了已经！
//                for (UserStatusVo statusVo : eveningStatus)
//                    statusVo.setStatus(0);
//                workNum = leaveNum;
//                for (int j = 0; j < workNum; j++) {
//                    if (isHaveManForClean(leaveTime, simpleRule.getWeekEnd() - 2.0, eveningStatus, simpleRule)) break;
//                    // 从晚上来的人中随机抽取一个，如果他已经在工作了 则再随机抽一个
//                    int workerId = (int) (Math.random() * eveningWorker.size());
//                    while (eveningStatus.get(workerId).getStatus() == 1 ||
//                            !isContinuesToClean(workerId, leaveTime, simpleRule.getWeekEnd() - 2.0, eveningStatus, simpleRule))
//                        workerId = (int) (Math.random() * eveningWorker.size());
//                    // 把他的状态设置为工作中，然后给他安排工作的时长
//                    eveningStatus.get(workerId).setStatus(1);
//                    if (eveningStatus.get(workerId).getEveningStart() == null) {
//                        eveningStatus.get(workerId).setEveningStart(simpleRule.getWeekEnd());
//                        eveningStatus.get(workerId).setEveningEnd(endTime);
//                    } else {
//                        eveningStatus.get(workerId).setEveningSecondStart(simpleRule.getWeekEnd());
//                        eveningStatus.get(workerId).setEveningSecondEnd(endTime);
//                    }
//                }
//                i += leaveTime;
//            }
//        }
//        morningStatus.forEach(System.out::println);
//        System.out.println("-------------------");
//        afternoonStatus.forEach(System.out::println);
//        System.out.println("-------------------");
//        eveningStatus.forEach(System.out::println);
//        // 把数据放入数据库
//    }
//
//    /**
//     * 判断它是否在上一时段中有工作
//     *
//     * @param workerId 在集合中的标号
//     * @param Status   status1
//     * @param StatusVo status2
//     * @return 如果在上一段工作时间内还在工作则返回true，否则为false
//     */
//    public boolean isTrue(int workerId, List<UserStatusVo> Status, List<UserStatusVo> StatusVo) {
//        for (UserStatusVo status : Status)
//            if (Objects.equals(status.getUserId(), StatusVo.get(workerId).getUserId())
//                    && status.getStatus() == 1)
//                return true;
//        return false;
//    }
//
//    /**
//     * 判断晚上还有没有人干活
//     *
//     * @param leaveTime     离开时间
//     * @param endWorkTime   结束时间
//     * @param eveningStatus 晚上的status
//     * @param simpleRule    通用规则
//     * @return 当有人时返回false
//     */
//    public boolean isHaveManForClean(double leaveTime,
//                                     double endWorkTime,
//                                     List<UserStatusVo> eveningStatus,
//                                     SimpleRule simpleRule) {
//        int count = 0;
//        for (UserStatusVo statusVo : eveningStatus) {
//            if (statusVo.getEveningEnd() != null) {
//                if ((statusVo.getEveningEnd() - statusVo.getEveningStart() + leaveTime > simpleRule.getSingleTop() &&
//                        statusVo.getStatus() == 0) ||
//                        (statusVo.getEveningEnd() + simpleRule.getWakeTime() > endWorkTime &&
//                                statusVo.getStatus() == 0))
//                    count += 1;
//                if (statusVo.getStatus() == 1)
//                    count += 1;
//            }
//        }
//        return count == eveningStatus.size();
//    }
//
//
//    /**
//     * 判断这个人是不是能留下来打扫卫生
//     *
//     * @param workerId      位次
//     * @param leaveTime     离开时间
//     * @param endWorkTime   今天结束工作的时间
//     * @param eveningStatus 晚上的status
//     * @param simpleRule    通用规则
//     * @return 如果能留下来 则返回true
//     */
//    public boolean isContinuesToClean(int workerId,
//                                      double leaveTime,
//                                      double endWorkTime,
//                                      List<UserStatusVo> eveningStatus,
//                                      SimpleRule simpleRule) {
//        if (eveningStatus.get(workerId).getEveningEnd() == null) return true;
//        return eveningStatus.get(workerId).getEveningEnd() - eveningStatus.get(workerId).getEveningStart() + leaveTime <= simpleRule.getSingleTop() ||
//                eveningStatus.get(workerId).getEveningEnd() + simpleRule.getWakeTime() <= endWorkTime;
//    }
//
//    /**
//     * 判断这时段内是否还有人能工作
//     *
//     * @param statusVos status
//     * @return 如果还有能干活的员工则返回true
//     */
//    public boolean isHaveMan(List<UserStatusVo> statusVos) {
//        int count = 0;
//        for (UserStatusVo statusVo : statusVos) {
//            if (statusVo.getStatus() == 1)
//                count += 1;
//        }
//        return count == statusVos.size();
//    }
//
//    // TODO 有bug
//
//    /**
//     * 判断下午是否有人能干活
//     *
//     * @param i               当前时间
//     * @param morningStatus   早上的status
//     * @param afternoonStatus 下午的status
//     * @param simpleRule      通用规则
//     * @return 如果全部在上班则返回true
//     */
//    public boolean isHaveManToWork(double i,
//                                   List<UserStatusVo> morningStatus,
//                                   List<UserStatusVo> afternoonStatus,
//                                   SimpleRule simpleRule) {
//        int count = 0;
//        for (UserStatusVo statusVo : morningStatus) {
//            if (statusVo.getMorningSecondEnd() != null &&
//                    statusVo.getMorningSecondEnd() >= i - simpleRule.getLunchTime())
//                count += 1;
//            if (statusVo.getMorningEnd() != null &&
//                    statusVo.getMorningSecondEnd() == null &&
//                    statusVo.getMorningEnd() >= i - simpleRule.getLunchTime())
//                count += 1;
//        }
//        for (UserStatusVo statusVo1 : afternoonStatus) {
//            if (statusVo1.getAfternoonSecondEnd() != null &&
//                    statusVo1.getAfternoonSecondEnd() >= i - simpleRule.getWakeTime())
//                count += 1;
//            if (statusVo1.getAfternoonEnd() != null &&
//                    statusVo1.getAfternoonEnd() >= i - simpleRule.getWakeTime() &&
//                    statusVo1.getAfternoonSecondEnd() == null)
//                count += 1;
//        }
//        return count == afternoonStatus.size();
//    }
//
//    // TODO 有bug
//
//    /**
//     * 返回晚上是否有人能干活
//     *
//     * @param i               当前时间
//     * @param afternoonStatus 下午的status
//     * @param eveningStatus   晚上的status
//     * @param simpleRule      通用规则
//     * @return 如果没人能干活则返回true
//     */
//    public boolean isHaveManToWorkEvening(double i,
//                                          List<UserStatusVo> afternoonStatus,
//                                          List<UserStatusVo> eveningStatus,
//                                          SimpleRule simpleRule) {
//        int count = 0;
//        for (UserStatusVo statusVo : afternoonStatus) {
//            if (statusVo.getAfternoonSecondEnd() != null &&
//                    statusVo.getAfternoonSecondEnd() >= i - simpleRule.getWakeTime())
//                count += 1;
//            if (statusVo.getAfternoonEnd() != null &&
//                    statusVo.getAfternoonSecondEnd() == null &&
//                    statusVo.getAfternoonEnd() >= i - simpleRule.getWakeTime())
//                count += 1;
//        }
//        for (UserStatusVo statusVo1 : eveningStatus) {
//            if (statusVo1.getEveningSecondEnd() != null &&
//                    statusVo1.getEveningSecondEnd() >= i - simpleRule.getWakeTime())
//                count += 1;
//            if (statusVo1.getEveningEnd() != null &&
//                    statusVo1.getEveningEnd() >= i - simpleRule.getWakeTime() &&
//                    statusVo1.getEveningSecondEnd() == null)
//                count += 1;
//        }
//        return count == eveningStatus.size();
//    }
//
//
//    /**
//     * 判断是否能连上
//     *
//     * @param i 当前时间
//     * @param statusVos 下午的status
//     * @param statusVoList 晚上的status
//     * @param simpleRule 通用规则
//     * @return 如果没有能连上的就返回true
//     */
//    public boolean isHaveManPlusToAfternoon(double i,
//                                            List<UserStatusVo> statusVos,
//                                            List<UserStatusVo> statusVoList,
//                                            SimpleRule simpleRule) {
//        int count = 0;
//        int working = 0;
//        for (UserStatusVo statusVo : statusVos) {
//            for (UserStatusVo statusVo1 : statusVoList) {
//                if (Objects.equals(statusVo.getUserId(), statusVo1.getUserId())) {
//                    if (statusVo.getAfternoonSecondEnd() != null &&
//                            statusVo.getAfternoonSecondEnd() >= i - simpleRule.getWakeTime())
//                        working += 1;
//                    if (statusVo.getAfternoonEnd() != null &&
//                            statusVo.getAfternoonSecondEnd() == null &&
//                            statusVo.getAfternoonEnd() >= i - simpleRule.getWakeTime()) {
//                        working += 1;
//                    }
//                    count += 1;
//                }
//            }
//        }
//        return count == working;
//    }
//
//    /**
//     * 判断上午和下午有没有能连上的
//     * @param i 当前时间
//     * @param statusVos 早上是status
//     * @param statusVoList 下午的status
//     * @param simpleRule 通用规则
//     * @return 如果没有能连上的就返回true
//     */
//    public boolean isHaveManPlusToMorning(double i,
//                                          List<UserStatusVo> statusVos,
//                                          List<UserStatusVo> statusVoList,
//                                          SimpleRule simpleRule) {
//        int count = 0;
//        int working = 0;
//        for (UserStatusVo statusVo : statusVos) {
//            for (UserStatusVo statusVo1 : statusVoList) {
//                if (Objects.equals(statusVo.getUserId(), statusVo1.getUserId())) {
//                    if (statusVo.getMorningSecondEnd() != null &&
//                            statusVo.getMorningSecondEnd() >= i - simpleRule.getWakeTime())
//                        working += 1;
//                    if (statusVo.getMorningEnd() != null &&
//                            statusVo.getMorningSecondEnd() == null &&
//                            statusVo.getMorningEnd() >= i - simpleRule.getWakeTime()) {
//                        working += 1;
//                    }
//                    count += 1;
//                }
//            }
//        }
//        return count == working;
//    }
//
//    /**
//     * 判断这个人是否能连续上班
//     *
//     * @param statusVos status1
//     * @param status    status2
//     * @param workerId  对应列表里面的id
//     * @return 如果能连续上班 就返回true
//     */
//    public boolean isContinues(List<UserStatusVo> statusVos, List<UserStatusVo> status, int workerId) {
//        Long userId = statusVos.get(workerId).getUserId();
//        for (UserStatusVo statusVo : status)
//            if (userId.equals(statusVo.getUserId()))
//                return true;
//        return false;
//    }
//
//    /**
//     * 判断早班有没有结束
//     *
//     * @param i        当前时间
//     * @param statusVo 早上的status
//     * @return 如果班次结束则返回true
//     */
//    public boolean isEndMorning(double i, UserStatusVo statusVo) {
//        if (statusVo.getMorningEnd() == null)
//            return false;
//        if (statusVo.getStatus() == 1 && statusVo.getMorningEnd() <= i)
//            return true;
//        if (statusVo.getMorningSecondStart() == null)
//            return false;
//        return statusVo.getStatus() == 1 && statusVo.getMorningSecondEnd() <= i;
//    }
//
//    /**
//     * 判断午班有没有结束
//     *
//     * @param i        当前时间
//     * @param statusVo 下午的status
//     * @return 如果班次结束则返回true
//     */
//    public boolean isEndAfternoon(double i, UserStatusVo statusVo) {
//        if (statusVo.getAfternoonEnd() == null)
//            return false;
//        if (statusVo.getStatus() == 1 && statusVo.getAfternoonEnd() <= i)
//            return true;
//        if (statusVo.getAfternoonSecondStart() == null)
//            return false;
//        return statusVo.getStatus() == 1 && statusVo.getAfternoonSecondEnd() <= i;
//    }
//
//    /**
//     * 判断晚班有没有结束
//     *
//     * @param i        当前时间
//     * @param statusVo 晚上的status
//     * @return 如果班次结束则返回true
//     */
//    public boolean isEndEvening(double i, UserStatusVo statusVo) {
//        if (statusVo.getEveningEnd() == null)
//            return false;
//        if (statusVo.getStatus() == 1 && statusVo.getEveningEnd() <= i)
//            return true;
//        if (statusVo.getEveningSecondStart() == null)
//            return false;
//        return statusVo.getStatus() == 1 && statusVo.getEveningSecondEnd() <= i;
//    }
//
//
//    /**
//     * 判断早上上班的是否休息过滤
//     *
//     * @param i          当前时间
//     * @param workerId   位次
//     * @param statusVo   早上的status
//     * @param simpleRule 通用规则
//     * @return 休息过或则没上班的了则返回true
//     */
//    public boolean isReadyMorning(double i, int workerId, List<UserStatusVo> statusVo, SimpleRule simpleRule) {
//        if (statusVo.get(workerId).getMorningEnd() != null)
//            return (i - statusVo.get(workerId).getMorningEnd()) >= simpleRule.getWakeTime();
//        return true;
//    }
//
//    /**
//     * 判断上午下午连着上班的是否休息过
//     *
//     * @param i               当前时间
//     * @param workerId        位次
//     * @param morningStatus   早上是status
//     * @param afternoonStatus 下午是status
//     * @param simpleRule      通用规则
//     * @return 休息过或者没上班则返回true
//     */
//    public boolean isReadyAfternoon(double i,
//                                    int workerId,
//                                    List<UserStatusVo> morningStatus,
//                                    List<UserStatusVo> afternoonStatus,
//                                    SimpleRule simpleRule) {
//        UserStatusVo morning = new UserStatusVo();
//        for (UserStatusVo statusVo : morningStatus) {
//            if (Objects.equals(statusVo.getUserId(), afternoonStatus.get(workerId).getUserId()))
//                morning = statusVo;
//        }
//        if (afternoonStatus.get(workerId).getAfternoonEnd() != null)
//            return (i - afternoonStatus.get(workerId).getAfternoonEnd()) >= simpleRule.getWakeTime();
//        else if (morning.getMorningSecondEnd() != null)
//            return (i - morning.getMorningSecondEnd()) >= simpleRule.getLunchTime();
//        else if (morning.getMorningEnd() != null)
//            return (i - morning.getMorningEnd()) >= simpleRule.getLunchTime();
//        return true;
//    }
//
//    /**
//     * 判断下午和晚上连着上的有没有休息过
//     *
//     * @param i               当前时间
//     * @param workerId        序号
//     * @param afternoonStatus 下午的status
//     * @param eveningStatus   晚上的status
//     * @param simpleRule      通用规则
//     * @return 休息过或着没上班的就返回true
//     */
//    public boolean isReadyEvening(double i,
//                                  int workerId,
//                                  List<UserStatusVo> afternoonStatus,
//                                  List<UserStatusVo> eveningStatus,
//                                  SimpleRule simpleRule) {
//        UserStatusVo afternoon = new UserStatusVo();
//        for (UserStatusVo statusVo : afternoonStatus) {
//            if (Objects.equals(statusVo.getUserId(), eveningStatus.get(workerId).getUserId()))
//                afternoon = statusVo;
//        }
//        if (eveningStatus.get(workerId).getEveningEnd() != null)
//            return (i - eveningStatus.get(workerId).getEveningEnd()) >= simpleRule.getWakeTime();
//        else if (afternoon.getAfternoonSecondEnd() != null)
//            return (i - afternoon.getAfternoonSecondEnd()) >= simpleRule.getDinnerTime();
//        else if (afternoon.getAfternoonEnd() != null)
//            return (i - afternoon.getAfternoonEnd()) >= simpleRule.getDinnerTime();
//        return true;
//    }
//
//    // TODO 判断今天是否工作满8小时
//
//    /**
//     * 获取当前已经工资的时间
//     * 仅限于下午模块使用
//     *
//     * @param workerId        在队列中的排号
//     * @param morningStatus   早上的status
//     * @param afternoonStatus 下午的status
//     * @return 已经工作的时间
//     */
//    public double hasWorkTimeAfternoon(int workerId,
//                                       List<UserStatusVo> morningStatus,
//                                       List<UserStatusVo> afternoonStatus) {
//        double workTime = 0.0;
//        UserStatusVo status = new UserStatusVo();
//        for (UserStatusVo statusVo : morningStatus)
//            if (Objects.equals(statusVo.getUserId(), afternoonStatus.get(workerId).getUserId()))
//                status = statusVo;
//        if (status.getUserId() != null) {
//            if (status.getMorningStart() != null)
//                workTime = workTime + status.getMorningEnd() - status.getMorningStart();
//            if (status.getMorningSecondEnd() != null)
//                workTime = workTime + status.getMorningSecondEnd() - status.getMorningSecondStart();
//        }
//        if (afternoonStatus.get(workerId).getAfternoonEnd() != null)
//            workTime = workTime + afternoonStatus.get(workerId).getAfternoonEnd()
//                    - afternoonStatus.get(workerId).getAfternoonStart();
//        return workTime;
//    }
//
//    /**
//     * 计算工作到晚上的工作时长
//     *
//     * @param workerId        队列中的位置
//     * @param morningStatus   早上的status
//     * @param afternoonStatus 下午的status
//     * @param eveningStatus   晚上的status
//     * @return 工作时长
//     */
//    public double hasWorkTimeEvening(int workerId,
//                                     List<UserStatusVo> morningStatus,
//                                     List<UserStatusVo> afternoonStatus,
//                                     List<UserStatusVo> eveningStatus) {
//        double workTime = 0.0;
//        UserStatusVo morning = new UserStatusVo();
//        UserStatusVo afternoon = new UserStatusVo();
//        for (UserStatusVo statusVo : morningStatus)
//            if (Objects.equals(statusVo.getUserId(), eveningStatus.get(workerId).getUserId()))
//                morning = statusVo;
//        if (morning.getUserId() != null) {
//            if (morning.getMorningStart() != null)
//                workTime = workTime + morning.getMorningEnd() - morning.getMorningStart();
//            if (morning.getMorningSecondEnd() != null)
//                workTime = workTime + morning.getMorningSecondEnd() - morning.getMorningSecondStart();
//        }
//        for (UserStatusVo statusVo : afternoonStatus)
//            if (Objects.equals(statusVo.getUserId(), eveningStatus.get(workerId).getUserId()))
//                afternoon = statusVo;
//        if (afternoon.getUserId() != null) {
//            if (afternoon.getMorningStart() != null)
//                workTime = workTime + afternoon.getMorningEnd() - afternoon.getMorningStart();
//            if (afternoon.getMorningSecondEnd() != null)
//                workTime = workTime + afternoon.getMorningSecondEnd() - afternoon.getMorningSecondStart();
//        }
//        if (eveningStatus.get(workerId).getEveningEnd() != null)
//            workTime = workTime + eveningStatus.get(workerId).getAfternoonEnd()
//                    - eveningStatus.get(workerId).getAfternoonStart();
//        return workTime;
//    }
//
//    // TODO 判断是否这个礼拜时长到了40小时
//    public boolean isWeekFull(int workerId) {
//        return false;
//    }
//
//
//    @Test
//    void testDate() {
//
//    }
//
//}
