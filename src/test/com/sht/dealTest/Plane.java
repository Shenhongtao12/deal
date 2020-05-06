package com.sht.dealTest;


import java.util.Scanner;

/**
 * @author Sht
 * @project deal
 * @date 2020/4/16 - 19:44
 **/
public class Plane {

    public static void main(String[] args) {
        System.out.println("请输入地图的长与宽：");
        Scanner input = new Scanner(System.in);
        int m = input.nextInt(); //地图的长
        int n = input.nextInt(); //地图的宽
        int N = (n + 2) / 3 * m;  //输入的字符串数量为：(n+2)/3*m;4*6的为：(4+2)/3*4=8
        char[][] str = new char[N][6];
        char[][] map = new char[m][n];
        System.out.println("请输入地图数据：");
        for (int i = 0; i < N; i++) {
            str[i] = input.next().trim().toCharArray();
        }
        /*for (int i = 0; i < str.length; i++) {
            System.out.println(str[i]);
        }*/
        int num = str.length -1;
        System.out.println("str.length - 1: "+num);
        System.out.println("-------输出------");
        //逆时针拍摄，先合并左边的数据，后合并右边的数据
        for (int i = 0; i < m; i++) {
            map[i][0] = str[i][4];  //左边的数据在[4]的位置
            map[i][1] = str[i][0];  //中间的数据在[0]的位置
            map[i][2] = str[i][2];  //右边的数据在[2]的位置
        }
        //合并右边的数据
        for (int i = 0; i < m; i++) {
            map[i][3] = str[num-i][4];  //左边的数据在[4]的位置
            map[i][4] = str[num-i][0];  //中间的数据在[0]的位置
            map[i][5] = str[num-i][2];  //右边的数据在[2]的位置
        }

        for (int i = 0; i < map.length; i++) {
            System.out.println(map[i]);
        }
    }

}
