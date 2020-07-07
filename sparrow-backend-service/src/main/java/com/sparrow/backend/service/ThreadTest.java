package com.sparrow.backend.service;

import com.sparrow.common.ComUtils;
import com.sparrow.common.ZkLock;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ThreadTest implements Runnable {

    private static Integer baseNum = 0;

    public ThreadTest() {
    }

    @Override
    public void run() {
        ZkLock zkLock = new ZkLock();
        try {
            zkLock.gainLock();

            baseNum += 1;
            String result = ComUtils.formatDateObj(new Date()) + " ---> " + Thread.currentThread().getName() + " : " + baseNum;
            Thread.sleep(200);
            System.out.println(result);

        } catch (Exception ex) {
            log.error(ComUtils.printException(ex));
        } finally {
            zkLock.releaseLock();
        }
    }

}
