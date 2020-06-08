package com.example.acm;

import java.util.ArrayList;
import java.util.List;

public class AcmTest {

    public static void main(String[] args) {
        Player<String> player = new Player<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(String.valueOf(i));
        }
        player.init(list);
        String current = null;
        while ((current = player.getAndMoveNext()) != null) {
            System.out.println(current);
        }
    }

    static class Node<E> {
        E current;
        Node<E> next;

        public Node(E current) {
            this.current = current;
        }
    }

    static class Player<E> {
        Node<E> head;
        Node<E> tail = null;

        public void init(List<E> inits) {
            for (E item : inits) {
                if (head == null) {
                    tail = new Node<>(item);
                    head = tail;
                    tail.next = null;
                } else {
                    tail.next = new Node<>(item);
                    tail = tail.next;
                }
            }
        }

        public void add(List<E> lists) {
            if (head == null) {
                init(lists);
            } else {
                for (E item : lists) {
                    tail.next = new Node<>(item);
                    tail = tail.next;
                }
            }
        }

        public E getAndMoveNext() {
            if (head != null) {
                E current = head.current;
                head = head.next;
                return current;
            } else {
                return null;
            }
        }
    }
}
