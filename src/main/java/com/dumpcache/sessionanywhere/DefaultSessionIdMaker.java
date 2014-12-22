package com.dumpcache.sessionanywhere;

import com.dumpcache.sessionanywhere.util.UUID;

public class DefaultSessionIdMaker extends UUID implements SessionIDMaker {

    public String makeNewId() {
        return super.nextID();
    }

    public static void main(String args[]) {
        DefaultSessionIdMaker maker = new DefaultSessionIdMaker();
        for (int i = 0; i < 100; i++) {
            System.out.println(maker.makeNewId());
        }
    }

}
