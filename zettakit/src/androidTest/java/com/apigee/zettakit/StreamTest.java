package com.apigee.zettakit;

import android.app.Application;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.apigee.zettakit.interfaces.ZIKCallback;
import com.apigee.zettakit.interfaces.ZIKStreamListener;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;

public class StreamTest extends ApplicationTestCase<Application> {
    public StreamTest() {
        super(Application.class);
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
                            if (device != null) {
                                device.fetchAsync(new ZIKCallback<ZIKDevice>() {
                                    @Override
                                    public void onSuccess(@NonNull ZIKDevice device) {
                                        final ZIKStream stream = device.stream("logs");
                                        if (stream != null) {
                                            stream.setStreamListener(new ZIKStreamListener() {
                                                @Override
                                                public void onOpen() {
                                                }

                                                @Override
                                                public void onPong() {
                                                }

                                                @Override
                                                public void onError(ZIKException exception, Response response) {
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
                                    public void onFailure(@NonNull ZIKException exception) {
                                        signal.countDown();
                                    }
                                });
                            }
                        } else {
                            throw new IllegalStateException("unexpected code path for this test");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ZIKException exception) {
                        throw new IllegalStateException("unexpected code path for this test");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull ZIKException exception) {
                throw new IllegalStateException("unexpected code path for this test" + exception.toString());
            }
        });
        signal.await(120, TimeUnit.SECONDS);
    }

    public void testMultiSimultaneousZIKStream() throws InterruptedException {
        final CountDownLatch threadSignal = new CountDownLatch(1);

        final int[] totalExpectedStreams = {0};
        final int[] totalStreamCount = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                ZIKSession sharedSession = ZIKSession.getSharedSession();
                ZIKRoot zikRoot = sharedSession.getRootSync("http://stage.zettaapi.org");
                List<ZIKServer> zikServers = sharedSession.getServersSync(zikRoot);

                int totalAvailableStreams = 0;
                for (ZIKServer zikServer : zikServers) {
                    for (ZIKDevice zikDevice : zikServer.getDevices()) {
                        zikDevice = zikDevice.fetchSync();
                        for (ZIKStream zikStream : zikDevice.getAllStreams()) {
                            if (zikStream.getTitle().equals("logs")) {
                                continue;
                            }
                            totalAvailableStreams++;
                        }
                    }
                }
                totalExpectedStreams[0] = totalAvailableStreams;

                if (zikServers.isEmpty()) {
                    throw new IllegalStateException("unexpected code path for this test");
                }
                for (ZIKServer zikServer : zikServers) {
                    for (ZIKDevice zikDevice : zikServer.getDevices()) {
                        zikDevice = zikDevice.fetchSync();
                        for (final ZIKStream zikStream : zikDevice.getAllStreams()) {
                            if (zikStream.getTitle().equals("logs")) {
                                continue;
                            }
                            zikStream.setStreamListener(new ZIKStreamListener() {
                                @Override
                                public void onOpen() {
                                    totalStreamCount[0]++;
                                }

                                @Override
                                public void onPong() {
                                    // not used
                                }

                                @Override
                                public void onUpdate(Object object) {
                                    // not used
                                }

                                @Override
                                public void onClose() {
                                    if (totalStreamCount[0] == totalExpectedStreams[0]) {
                                        threadSignal.countDown();
                                    }
                                }

                                @Override
                                public void onError(ZIKException exception, Response response) {
                                    throw new IllegalStateException("unexpected code path for this test ", exception);
                                }
                            });
                            zikStream.resume();

                        }
                    }
                }
            }
        }).start();

        try {
            threadSignal.await(120, TimeUnit.SECONDS);
        } catch (Exception e) {
            // ingore timeouts
        }

        Log.d("xxx", "total: " + totalStreamCount[0]);
        assertEquals(totalExpectedStreams[0], totalStreamCount[0]);
    }
}
