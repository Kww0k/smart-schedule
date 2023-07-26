package com.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Silvery
 * @Date: 2023/7/22 16:47
 */
public class Main {

    public static void main(String[] args) {
        List<Integer> list =new ArrayList<>();
        list.add(1);
        list.add(2);
        List<Integer> list1 = list.stream().filter(integer -> integer == 1).toList();
        System.out.println(list1);
    }
}
