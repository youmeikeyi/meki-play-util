/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.meki.play;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xujinchao on 19/2/22.
 */
public class MpSort {

    /**
     * 选择排序
     * 基本思想:先比较出最大的放在首位,再选第二大的放在已排序后面;直到全部有序
     *
     * @param array
     */
    public static void selectionSort(int[] array) {
        int size = array.length;
        for (int i = 0; i < size - 1; i++) {
            //            int k = array[i];
            for (int j = 1; j < i; j++) {
                if (array[j] < array[i]) {
                    swap(array, i, j);
                    // swap 次数太多,记录需要交换的索引
                }
            }
            System.out.println("round:" + (i + 1));
            System.out.println(JSON.toJSON(array));
        }

    }

    public static void selectionSort2(int[] array) {
        int size = array.length;
        int swapIndex;
        for (int i = 0; i < size - 1; i++) {
            swapIndex = i;
            for (int j = 1; j < i; j++) {
                if (array[j] < array[i]) {
                    swapIndex = j;
                }
            }
            swap(array, i, swapIndex);
            System.out.println("round:" + (i + 1));
            System.out.println(JSON.toJSON(array));
        }

    }

    /**
     * 冒泡排序
     * 基本思想:
     * 优化:
     * 1.未发生交换,说明提前有序,提前结束
     * 2.记录上一次交换位置,较少后面有序的对比,
     *
     * @param array
     */
    public static void maopao(int[] array) {
        int size = array.length;
        int i = size - 1;

        int pos = -1;
        while (i > 0) {
            boolean swap = false;

            for (int j = 0; j < i; j++) {
                if (array[j] < array[j + 1]) {
                    swap(array, j, j + 1);
                    swap = true;
                    pos = j;
                }

            }
            if (!swap) {
                break;
            }
            System.out.println("round:" + (size - i));
            System.out.println(JSON.toJSON(array));
            i--;  // 改为i = pos
        }

    }

    /**
     * 冒泡优化,一趟排序找到最大值最小值
     *
     * @param array
     */
    public static void maopao2(int[] array) {
        int high = array.length;
        int low = 0;

        while (low < high) {

            for (int j = low; j < high - 1; j++) {
                if (array[j] > array[j + 1]) {
                    swap(array, j, j + 1);
                }
            }
            high--;
            System.out.println(JSON.toJSON(array));
            for (int j = high; j > low; j--) {
                if (array[j] < array[j - 1]) {
                    swap(array, j, j - 1);
                }
            }
            low++;
            System.out.println(JSON.toJSON(array));
        }

    }

    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 直接插入排序
     * 基本思想:假定第一元素有序,维持一个有序队列,遍历插入合适位置,统一后移,放入相应位置。
     *
     * @param array
     */
    public static void directAdd(int[] array) {
        for (int i = 1; i < array.length; i++) {
            // 记录 移动位置
            int k = array[i];
            //            int j = i - 1;
            //            while (j >= 0 && array[j] > k) {
            //                array[j + 1] = array[j];
            //                j--;
            //            }
            //            array[j + 1] = k;

            // 二分
            int low = 0, high = i - 1;
            while (low <= high) {
                int mid = low + (high - low) / 2;
                if (k < array[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }

            }
            for (int j = i - 1; j >= low; j--) {
                array[j + 1] = array[j];
            }
            array[low] = k;
            System.out.println(JSON.toJSON(array));
        }
    }

    //    /**
    //     * 二分查找插入位置
    //     */
    //    public static int getAddPos(int[] array, int low, int high, int value) {
    //        if (array.length == 0 || low < 0 || high < 0 || low > high) {
    //            return -1;
    //        }
    //        int mid = low + (high - low) / 2;
    //        if (array[mid] > value) {
    //            getAddPos(array, 0, mid, value);
    //        } else if (array[mid] < value) {
    //            getAddPos(array, mid + 1, high, value);
    //        }
    //        return mid;
    //    }

    /**
     * 归并排序
     *
     * @param array
     */
    public static int[] mergeSort(int[] array) {
        int size = array.length;
        if (size < 2) {
            return array;
        }
        int low = 0;
        int hight = size - 1;
        int mid = (size - 1) / 2;
        int[] left = new int[mid - low + 1];
        int[] right = new int[hight - mid];
        System.arraycopy(array, 0, left, 0, mid - low + 1);
        System.arraycopy(array, mid + 1, right, 0, hight - mid);
        return merge(mergeSort(left), mergeSort(right));
    }

    public static int[] merge(int[] a, int[] b) {
        int i = 0, j = 0, k = 0;
        int c[] = new int[a.length + b.length];
        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                c[k++] = a[i++];
            } else {
                c[k++] = b[j++];
            }
        }

        while (i <= a.length - 1) {
            c[k++] = a[i++];
        }
        while (j <= b.length - 1) {
            c[k++] = b[j++];
        }
        System.out.println(JSON.toJSON(c));
        return c;
    }

    /**
     * 快排
     *
     * @param array
     */
    public static void quickSort(int[] array, int left, int right) {

        //        int low = 0, high = array.length - 1;
        // i位置表示小于key的集合索引,下一个位置是大于哨兵的索引,即遇到小于哨兵时需swap的位置
        int i = left - 1;
        if (left < right) {
            int key = array[right];
            for (int j = left; j <= right; j++) {
                if (array[j] <= key) {
                    i++;
                    //                    int temp = array[i];
                    //                    array[i] = array[j];
                    //                    array[j] = temp;
                    swap(array, i, j);
                }
            }
            System.out.println(JSON.toJSON(array));
            quickSort(array, left, i - 1);
            quickSort(array, i + 1, right);
        }

    }

    /**
     * 堆排序
     * 基本思想:堆排序（Heapsort）是指利用堆这种数据结构所设计的一种排序算法。堆积是一个近似完全二叉树的结构，并同时满足堆积的性质：即子结点的键值或索引总是小于（或者大于）它的父节点。
     * <p/>
     * (2)算法描述和实现
     * 具体算法描述如下：
     * <p/>
     * <1>.将初始待排序关键字序列(R1,R2....Rn)构建成大顶堆，此堆为初始的无序区；
     * <2>.将堆顶元素R[1]与最后一个元素R[n]交换，此时得到新的无序区(R1,R2,......Rn-1)和新的有序区(Rn),且满足R[1,2...n-1]<=R[n]；
     * <3>.由于交换后新的堆顶R[1]可能违反堆的性质，因此需要对当前无序区(R1,R2,......Rn-1)调整为新堆，然后 再次将R[1]与无序区最后一个元素交换，得到新的无序区(R1,R2....Rn-2)
     * 和新的有序区(Rn-1,Rn)。不断重复此过程直到有序区的元 素个数为n-1，则整个排序过程完成。
     *
     * @param array
     */
    public static void heapSort(int[] array) {
        if (array.length < 0) {
            return;
        }
        int heapSize = array.length;
        int x = heapSize / 2 - 1;
        int temp;
        for (int i = x; i >= 0; i--) {
            // 初始建堆
            heapify(array, i, heapSize);
        }
        System.out.println(JSON.toJSON(array));

        for (int j = heapSize - 1; j >= 1; j--) {
            temp = array[0];
            array[0] = array[j];
            array[j] = temp;

            heapify(array, 0, --heapSize);
            System.out.println(JSON.toJSON(array));

        }
        System.out.println(JSON.toJSON(array));
    }

    public static void heapify(int[] array, int x, int length) {
        int leftChild = 2 * x + 1;
        int rightChild = 2 * x + 2;

        int largest = x;
        if (leftChild < length && array[leftChild] > array[largest]) {
            largest = leftChild;
        }

        if (rightChild < length && array[rightChild] > array[largest]) {
            largest = rightChild;
        }

        if (largest != x) {
            swap(array, x, largest);

            heapify(array, largest, length);
        }
    }

    /**
     * 计数排序
     *
     * @param array
     */
    public static int[] countSort(int[] array) {
        int[] result = new int[array.length];

        // can use map to count num,  set 100 if you know the max-min size
        int[] tmp = new int[100];

        int min, max;
        min = max = array[0];
        // find min and max, make clear tmp size
        for (int i = 0; i < array.length; i++) {
            if (min >= array[i]) {
                min = array[i];
            }
            if (max <= array[i]) {
                max = array[i];
            }
            //            if (tmp[array[i]] == 0) {
            //                tmp[array[i]] = 1;
            //            } else {
            tmp[array[i]] += 1;
            //            }

        }

        // tmp count num of elements, convert to calculate index
        for (int j = min; j < max; j++) {
            tmp[j + 1] += tmp[j];
        }

        // new order
        for (int k = array.length - 1; k >= 0; k--) {
            result[tmp[array[k]] - 1] = array[k];
            tmp[array[k]]--;
        }
        System.out.println(JSON.toJSON(result));
        return result;
    }

    /**
     * 桶排序
     *
     * @param array
     * @param bucketNum
     */
    public static void bucketSort(int[] array, int bucketNum) {
        if (array.length < 0 || bucketNum < 1) {
            return;
        }
        int min, max;

        // 计算值域范围
        min = max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
            }
            if (max < array[i]) {
                max = array[i];
            }
        }

        // 计算bucket空间
        int space = (max - min + 1) / bucketNum;
        // bucket大小不好确认,最坏的情况是length
        int[][] buckets = new int[bucketNum][array.length];
        // 同类型转换为Map<Integer, List<Integer>>,或先把桶初始化为min-1.
        // 通过bucket空间计算桶索引, 每个桶内做插入排序,完成后桶顺序拼接
//        for (int i = array.length - 1; i >= 0; i--) {
        for (int i = 0; i < array.length; i ++) {

            int index = (array[i] - min) / space;
            if (buckets[index].length == 0) {
                buckets[index][0] = array[i];
            } else {
                // 注意:length已经默认初始化为长度length的0数组了,有问题!
                int j = buckets[index].length - 1;
                while (j >= 0 && buckets[index][j] > array[i]) {
                    buckets[index][j + 1] = buckets[index][j];
                    j--;
                }
                buckets[index][j + 1] = array[i];
            }
        }

        // 遍历桶放入一个result
        int[] result = new int[array.length];
        int rIndex = 0;
        for (int i = 0; i < bucketNum; i ++) {
            if (buckets[i].length > 0) {
                for (int j =0; j < buckets[i].length; j ++) {
                    result[rIndex++] = buckets[i][j];
                }
            }
        }
        System.out.println(JSON.toJSON(result));
    }

    /**
     * 桶排序
     *
     * @param array
     * @param bucketNum
     */
    public static void bucketSortOfGood(int[] array, int bucketNum) {
        if (array.length < 0 || bucketNum < 1) {
            return;
        }
        int min, max;

        // 计算值域范围
        min = max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (min > array[i]) {
                min = array[i];
            }
            if (max < array[i]) {
                max = array[i];
            }
        }
        // 计算bucket空间
        int space = (max - min + 1) / bucketNum;
        // bucket大小不好确认,最坏的情况是length
        // 同类型转换为Map<Integer, List<Integer>>,或先把桶初始化为min-1.
        // 通过bucket空间计算桶索引, 每个桶内做插入排序,完成后桶顺序拼接
        // 按照bucket索引取map
        Map<Integer, List<Integer>> bucketMap = Maps.newConcurrentMap();
        for (int i = 0; i < array.length; i ++) {

            int index = (array[i] - min) / space;
            if (bucketMap.get(index) == null) {
                List<Integer> bucket = new ArrayList<>(array.length);
                bucket.add(array[i]);
                bucketMap.put(index, bucket);
            } else {
                // 注意:length已经默认初始化为长度length的0数组了,有问题!
                List<Integer> bucket = bucketMap.get(index);
                int j = bucket.size() - 1;
                // 想添加先要扩容1
                if (j + 1 >= bucket.size()) {
                    bucket.add(-1);
                }
                while (j >= 0 && bucket.get(j) > array[i]) {

                    bucket.set(j + 1, bucket.get(j));
                    j--;
                }
                if (j + 1 >= bucket.size()) {
                    bucket.remove(j);
                } else {
                    bucket.set(j + 1, array[i]);
                }

            }
        }
        System.out.println(JSON.toJSON(bucketMap));
        // 遍历桶放入一个result
        int[] result = new int[array.length];
        int rIndex = 0;
        for (int i = 0; i <= bucketNum; i ++) {
            List<Integer> bucket = bucketMap.get(i);
            if (bucket != null && bucket.size() > 0) {
                for (int j =0; j < bucket.size(); j ++) {
                    result[rIndex ++] = bucket.get(j);
                }
            }
        }
        System.out.println(JSON.toJSON(result));
    }

    public static void main(String[] args) {
        int[] sortAarry = new int[] {2, 3, 9, 2, 4, 7, 5, 22, 99, 11, 43, 55, 8, 5};
        //        sortAarry = new int[] {1, 2, 3};
        System.out.println(JSON.toJSON(sortAarry));
        //        maopao2(sortAarry);
        //        directAdd(sortAarry);
        //        mergeSort(sortAarry);
//        quickSort(sortAarry, 0, sortAarry.length - 1);
        //        heapSort(sortAarry);
        //        countSort(sortAarry);
        bucketSortOfGood(sortAarry, 5);
    }

}
