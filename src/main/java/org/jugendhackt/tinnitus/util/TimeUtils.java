package org.jugendhackt.tinnitus.util;

public class TimeUtils {

    public static int incrementHoursBy(int start, int i) {
        
        if(start > 24) {
            return -1;
        }
        
        if(start >= 23) {
            if(start == 23) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return start + i;
        }
        
    }

    
    
}
