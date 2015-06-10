package com.meki.play.test;

import java.lang.reflect.Method;
import java.util.*;

/**
 * example uses Sets.difference( ) to show the method differences between various Collection and Map
 * classes in java.util
 * Created by xujinchao on 2015/6/8.
 */
public class ContainerMethodDifferences {

    static Set<String> methodSet(Class<?> type) {
        Set<String> result = new TreeSet<String>();
        for (Method method : type.getMethods()) {
            result.add(method.getName());
        }
        return result;
    }

    static void interfaces(Class<?> type){
        System.out.print("Interfaces in " + type.getSimpleName() + " : ");
        List<String> result = new ArrayList<String>();
        for (Class<?> cls : type.getInterfaces()) {
            result.add(cls.getSimpleName());
        }
        System.out.println(result);
    }

    static Set<String> object = methodSet(Object.class);
    static {
        object.add("clone");
    }

    static void difference(Class<?> superSet, Class<?> subSet){
        System.out.print(superSet.getSimpleName() + " extends " + subSet.getSimpleName() + ", adds: ");
        Set<String> comp = Sets.difference(methodSet(superSet), methodSet(subSet));
        comp.removeAll(object);
        System.out.println(comp);
        interfaces(superSet);
    }

    public static void main(String[] args) {
        System.out.println("Collection: " + methodSet(Collection.class));

        interfaces(Collection.class);
        difference(Set.class, Collection.class);
        difference(HashSet.class, Set.class);
        difference(LinkedHashSet.class, HashSet.class);
        difference(TreeSet.class, Set.class);
        System.out.println();
        difference(List.class, Collection.class);
        difference(ArrayList.class, List.class);
        difference(LinkedList.class, List.class);
        System.out.println();
        difference(Queue.class, Collection.class);
        difference(PriorityQueue.class, Queue.class);
        System.out.println();
        System.out.println("Map: " + methodSet(Map.class));
        difference(HashMap.class, Map.class);
        difference(LinkedHashMap.class, HashMap.class);
        difference(SortedMap.class, Map.class);
        difference(TreeMap.class, Map.class);
    }
}
