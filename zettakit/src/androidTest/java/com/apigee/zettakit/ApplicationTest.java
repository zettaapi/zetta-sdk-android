package com.apigee.zettakit;

import android.app.Application;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKStreamListener;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;

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
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKCallback<ZIKRoot>() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKCallback<List<ZIKServer>>() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            ZIKServer server = servers.get(1);
                            sharedSession.getDevicesAsync(server, new ZIKCallback<List<ZIKDevice>>() {
                                @Override
                                public void onSuccess(@NonNull List<ZIKDevice> devices) {
                                    signal.countDown();
                                }
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    throw new IllegalStateException("unexpected code path for this test");
                                }
                            });
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKStream() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKCallback<ZIKRoot>() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKCallback<List<ZIKServer>>() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            ZIKServer server = servers.get(1);
                            ZIKDevice device = server.getDeviceNamed("Porch Light");
                            if( device != null ) {
                                device.fetchAsync(new ZIKCallback<ZIKDevice>() {
                                    @Override
                                    public void onSuccess(@NonNull ZIKDevice device) {
                                        final ZIKStream stream = device.stream("logs");
                                        if( stream != null ) {
                                            stream.setStreamListener(new ZIKStreamListener() {
                                                @Override
                                                public void onOpen() { }
                                                @Override
                                                public void onPong() { }

                                                @Override
                                                public void onError(Exception exception, Response response) {
                                                    throw new IllegalStateException("unexpected code path for this test");
                                                }

                                                @Override
                                                public void onUpdate(Object object) {
                                                    assertTrue(object instanceof ZIKLogStreamEntry);
                                                    ZIKLogStreamEntry streamEntry = (ZIKLogStreamEntry) object;
                                                    System.out.print(streamEntry.getDeviceState());
                                                    stream.close();
                                                }

                                                @Override
                                                public void onClose() {
                                                    signal.countDown();
                                                }
                                            });
                                            stream.resume();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        signal.countDown();
                                    }
                                });
                            }
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKRootParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKCallback<ZIKRoot>() {
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
            public void onFailure(@NonNull Exception exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKServerParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKCallback<ZIKRoot>() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKCallback<List<ZIKServer>>() {
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
                    public void onFailure(@NonNull Exception exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testZIKDeviceParcelable() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZIKSession.init(this.getContext());
        final ZIKSession sharedSession = ZIKSession.getSharedSession();
        sharedSession.getRootAsync("http://stage.zettaapi.org", new ZIKCallback<ZIKRoot>() {
            @Override
            public void onSuccess(@NonNull ZIKRoot root) {
                sharedSession.getServersAsync(root, new ZIKCallback<List<ZIKServer>>() {
                    @Override
                    public void onSuccess(@NonNull List<ZIKServer> servers) {
                        if (!servers.isEmpty()) {
                            ZIKServer server = servers.get(1);
                            sharedSession.getDevicesAsync(server, new ZIKCallback<List<ZIKDevice>>() {
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
                                public void onFailure(@NonNull Exception exception) {
                                    throw new IllegalStateException("unexpected code path for this test");
                                }
                            });
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }
}
