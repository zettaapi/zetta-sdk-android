package com.apigee.zettakit;

import android.app.Application;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import com.apigee.zettakit.interfaces.ZIKCallback;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TransitionTest extends ApplicationTestCase<Application> {
    public TransitionTest() {
        super(Application.class);
    }

    public void testTransitionWithoutInput() throws InterruptedException {
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
                            final ZIKServer server = servers.get(1);
                            final ZIKDevice device = server.getDeviceNamed("Family Room Light");
                            if( device != null ) {
                                device.fetchAsync(new ZIKCallback<ZIKDevice>() {
                                    @Override
                                    public void onSuccess(@NonNull ZIKDevice result) {
                                        ZIKTransition transitionToExecute = null;
                                        for( ZIKTransition transition : result.getTransitions() ) {
                                            if( transition.getName().equalsIgnoreCase("turn-on") ) {
                                                transitionToExecute = transition;
                                                break;
                                            } else if( transition.getName().equalsIgnoreCase("turn-off")) {
                                                transitionToExecute = transition;
                                                break;
                                            }
                                        }
                                        if( transitionToExecute != null ) {
                                            result.transition(transitionToExecute.getName(), new ZIKCallback<ZIKDevice>() {
                                                @Override
                                                public void onSuccess(@NonNull ZIKDevice result) {
                                                    signal.countDown();
                                                }
                                                @Override
                                                public void onFailure(@NonNull ZIKException exception) {
                                                    throw new IllegalStateException("unexpected code path for this test");
                                                }
                                            });
                                        } else {
                                            throw new IllegalStateException("unexpected code path for this test");
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull ZIKException exception) {
                                        throw new IllegalStateException("unexpected code path for this test");
                                    }
                                });
                            } else {
                                throw new IllegalStateException("unexpected code path for this test");
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

    public void testTransitionWithInput() throws InterruptedException {
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
                            final ZIKServer server = servers.get(1);
                            final ZIKDevice device = server.getDeviceNamed("Family Room Light");
                            if( device != null ) {
                                device.fetchAsync(new ZIKCallback<ZIKDevice>() {
                                    @Override
                                    public void onSuccess(@NonNull ZIKDevice result) {
                                        HashMap<String,Object> brightnessMap = new HashMap<>();
                                        final Double newBrightness = Math.random();
                                        brightnessMap.put("brightness",newBrightness);
                                        result.transition("set-brightness", brightnessMap,  new ZIKCallback<ZIKDevice>() {
                                            @Override
                                            public void onSuccess(@NonNull ZIKDevice result) {
                                                Double resultBrightness = Double.parseDouble((String)result.getProperties().get("brightness"));
                                                assertTrue(newBrightness.equals(resultBrightness));
                                                signal.countDown();
                                            }
                                            @Override
                                            public void onFailure(@NonNull ZIKException exception) {
                                                throw new IllegalStateException("unexpected code path for this test");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NonNull ZIKException exception) {
                                        throw new IllegalStateException("unexpected code path for this test");
                                    }
                                });
                            } else {
                                throw new IllegalStateException("unexpected code path for this test");
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
}

