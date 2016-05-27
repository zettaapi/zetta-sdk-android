package com.apigee.zettakit;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.ApplicationTestCase;

import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.callbacks.ZIKRootCallback;
import com.apigee.zettakit.callbacks.ZIKServersCallback;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testGET() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        ZIKSession.getSharedSession().getRoot("http://v1-staging.iot.apigee.net/", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                ZIKSession.getSharedSession().getServers(root, new ZIKServersCallback() {
                    @Override
                    public void onFinished(@Nullable List<ZIKServer> servers) {
                        if( servers != null && !servers.isEmpty() ) {
                            ZIKServer server = servers.get(0);
                            ZIKSession.getSharedSession().getDevices(server, new ZIKDevicesCallback() {
                                @Override
                                public void onFinished(@Nullable List<ZIKDevice> devices) {
                                    signal.countDown();
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onError(@NonNull String error) {
                signal.countDown();
            }
        });
        signal.await();
    }
}