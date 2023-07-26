package com.test;

import com.test.domain.entity.AiData;
import com.test.domain.entity.InfoData;
import com.test.domain.entity.UserScheduling;
import com.test.mapper.AiDataMapper;
import com.test.mapper.InfoDataMapper;
import com.test.mapper.UserSchedulingMapper;
import com.test.utils.AiDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class test {

    @Autowired
    AiDataMapper aiDataMapper;
    @Autowired
    InfoDataMapper infoDataMapper;
    @Autowired
    UserSchedulingMapper userSchedulingMapper;


    @Test
    void TestData() {
        List<UserScheduling> userScheduling = userSchedulingMapper.selectData(2L, "2023-3-10", "2023-3-10");
        System.out.println(userScheduling.size());
    }

    @Test
    void testDelete() throws Exception{
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse("2023-4-1");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date1);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date2 = sdf.parse("2023-4-2");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date2);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date3 = sdf.parse("2023-4-3");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date3);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date4 = sdf.parse("2023-4-4");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date4);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date5 = sdf.parse("2023-4-5");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date5);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date6 = sdf.parse("2023-4-6");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date6);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date7 = sdf.parse("2023-4-7");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date7);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date8 = sdf.parse("2023-4-8");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date8);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date9 = sdf.parse("2023-4-9");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date9);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date10 = sdf.parse("2023-4-10");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date10);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date11 = sdf.parse("2023-4-11");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date11);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date12 = sdf.parse("2023-4-12");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date12);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date13 = sdf.parse("2023-4-13");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date13);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date14 = sdf.parse("2023-4-14");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date14);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date15 = sdf.parse("2023-4-15");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date15);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date16 = sdf.parse("2023-4-16");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date16);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date17 = sdf.parse("2023-4-17");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date17);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date18 = sdf.parse("2023-4-18");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date18);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date19 = sdf.parse("2023-4-19");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date19);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date20 = sdf.parse("2023-4-20");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date20);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date21 = sdf.parse("2023-4-21");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date21);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date22 = sdf.parse("2023-4-22");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date22);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date23 = sdf.parse("2023-4-23");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date23);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date24 = sdf.parse("2023-4-24");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date24);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date25 = sdf.parse("2023-4-25");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date25);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date26 = sdf.parse("2023-4-26");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date26);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date27 = sdf.parse("2023-4-27");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date27);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date28 = sdf.parse("2023-4-28");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date28);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date29 = sdf.parse("2023-4-29");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date29);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
        Date date30 = sdf.parse("2023-4-30");
        for (long i = 1L; i <= 3L; i++) {
            AiData weekData = AiDataUtils.getWeekData(date30);
            aiDataMapper.insert(weekData);
            infoDataMapper.insert(new InfoData(i, weekData.getId()));
        }
    }

    public static void main(String[] args){
        for (int i = 0; i < 1000; i++) {
            double random = ((int) (Math.random() * 1200) + 1500) / 100.0;
            System.out.println(random);
        }
    }


}
