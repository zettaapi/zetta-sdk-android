package com.apigee.zettakit;

import android.app.Application;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import com.apigee.zettakit.callbacks.ZIKDevicesCallback;
import com.apigee.zettakit.callbacks.ZIKRootCallback;
import com.apigee.zettakit.callbacks.ZIKServersCallback;

import java.io.IOException;
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
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKServersCallback() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            ZIKServer server = servers.get(1);
                            sharedSession.getDevicesAsync(server, new ZIKDevicesCallback() {
                                @Override
                                public void onSuccess(@NonNull List<ZIKDevice> devices) {
                                    signal.countDown();
                                }
                                @Override
                                public void onFailure(@NonNull IOException exception) {
                                    throw new IllegalStateException("unexpected code path for this test");
                                }
                            });
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull IOException exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull IOException exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKRootParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                Parcel parcel = Parcel.obtain();
                root.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                ZIKRoot createdFromParcel = ZIKRoot.CREATOR.createFromParcel(parcel);
                assertNotNull(createdFromParcel);
                signal.countDown();
            }

            @Override
            public void onFailure(@NonNull IOException exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKServerParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKServersCallback() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            for( ZIKServer server : servers ) {
                                Parcel parcel = Parcel.obtain();
                                server.writeToParcel(parcel, 0);
                                parcel.setDataPosition(0);
                                ZIKServer createdFromParcel = ZIKServer.CREATOR.createFromParcel(parcel);
                                assertNotNull(createdFromParcel);
                            }
                            signal.countDown();
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull IOException exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull IOException exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKDeviceParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKRootCallback() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKServersCallback() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            ZIKServer server = servers.get(1);
                            sharedSession.getDevicesAsync(server, new ZIKDevicesCallback() {
                                @Override
                                public void onSuccess(@NonNull List<ZIKDevice> devices) {
                                    for( ZIKDevice device : devices ) {
                                        Parcel parcel = Parcel.obtain();
                                        device.writeToParcel(parcel, 0);
                                        parcel.setDataPosition(0);
                                        ZIKDevice createdFromParcel = ZIKDevice.CREATOR.createFromParcel(parcel);
                                        assertEquals(device.getDeviceId(),createdFromParcel.getDeviceId());
                                        assertNotNull(createdFromParcel);
                                    }
                                    signal.countDown();
                                }
                                @Override
                                public void onFailure(@NonNull IOException exception) {
                                    throw new IllegalStateException("unexpected code path for this test");
                                }
                            });
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull IOException exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull IOException exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }
}
