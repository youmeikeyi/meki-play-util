package com.meki.play.test;

import java.util.PriorityQueue;

/**
 * to-do列表
 * Created by xujinchao on 2015/7/7.
 */
public class ToDoList extends PriorityQueue<ToDoList.ToDoItem> {

    static class ToDoItem implements Comparable<ToDoItem> {

        private char primary;
        private int secondary;
        private String item;

        public ToDoItem(String td, char primary, int secondary){
            this.item = td;
            this.secondary = secondary;
            this.primary = primary;
        }

        @Override
        public int compareTo(ToDoItem arg) {
            if (primary > arg.primary) {
                return 1;
            }
            if (primary == arg.primary) {
                if (secondary > arg.secondary) {
                    return 1;
                } else if (secondary == arg.secondary) {
                    return 0;
                }
            }
            return -1;
        }

        @Override
        public String toString() {
            return "ToDoItem{" +
                    "primary=" + primary +
                    ", secondary=" + secondary +
                    ", item='" + item + '\'' +
                    '}';
        }
    }

    public void add(String td, char pri, int sec) {
        super.add(new ToDoItem(td, pri, sec));
    }

    public static void main(String[] args){
        ToDoList toDoList = new ToDoList();
        toDoList.add("Empty trash", 'C', 4);
        toDoList.add("Feed dog", 'A', 2);
        toDoList.add("Feed bird", 'B', 7);
        toDoList.add("Mow lawn", 'C', 3);
        toDoList.add("Water lawn", 'A', 1);
        toDoList.add("Feed cat", 'B', 1);
        while(!toDoList.isEmpty())
            System.out.println(toDoList.remove());
    }
}
