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
import java.util.concurrent.TimeUnit;

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
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRoot("http://stage.zettaapi.org", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServers(root, new ZIKServersCallback() {
                    @Override
                    public void onFinished(@Nullable List<ZIKServer> servers) {
                        if (servers != null && !servers.isEmpty()) {
                            ZIKServer server = servers.get(0);
                            sharedSession.getDevices(server, new ZIKDevicesCallback() {
                                @Override
                                public void onFinished(@Nullable List<ZIKDevice> devices) {
                                    signal.countDown();
                                }
                            });
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                });
            }

            @Override
            public void onError(@NonNull String error) {
                throw new IllegalStateException("unexpected code path for this test");
            }
        });
        signal.await(30, TimeUnit.SECONDS);
    }
}
