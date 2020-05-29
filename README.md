# StompForAndroid
依赖StompProtocolAndroid类库，引入项目使用

* 本文参考 https://github.com/NaikSoftware/StompProtocolAndroid

# 使用方法

## 1.引入类库
```
dependencies {
    api "com.github.NaikSoftware:StompProtocolAndroid:1.6.4"
    api "io.reactivex.rxjava2:rxjava:2.2.5"
}
```
## 2.初始化stompClient对象 并监听连接状态
```
 stompClient = Stomp.over(OKHTTP, Const.address);

            stompClient.lifecycle().subscribe(lifecycleEvent -> {
                //关注lifecycleEvent的回调来决定是否重连
                switch (lifecycleEvent.getType()) {
                    case OPENED:

                        Log.d(Const.TAG, "forlan debug stomp connection opened");
                        break;
                    case ERROR:

                        Log.e(Const.TAG, "forlan debug stomp connection error is ", lifecycleEvent.getException());
                        break;
                    case CLOSED:

                        Log.d(Const.TAG, "forlan debug stomp connection closed");
                        break;
                }
            });
            
```

## 3.发起连接 headers信息根据具体情况填入
```
 ArrayList<StompHeader> headers = new ArrayList<>();
            headers.add(new StompHeader("userId", "103"));
            //这里必须添加headers 否则会报错 headers可以添加用户的认证相关信息
            stompClient.connect(headers);


```

## 4.发起订阅信息
```

            stompClient.topic(Const.broadcastResponse)
                    .subscribe(new DisposableSubscriber<StompMessage>() {
                        @Override
                        public void onNext(StompMessage stompMessage) {

                            Log.d(Const.TAG, "Received== " + stompMessage.toString());
                            Log.i(Const.TAG, "Receive: " + stompMessage.getPayload());
                            runOnUiThread(() -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(stompMessage.getPayload());
                                    resultText.append(jsonObject.getString("response") + "\n");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.e(Const.TAG, "Stomp topic error", t);
                        }

                        @Override
                        public void onComplete() {
                            Log.e(Const.TAG, "Stomp connection onComplete");
                        }
                    });

```
## 5.发送消息 由相对应的订阅渠道返回信息

```
  stompClient.send(Const.broadcast).subscribe();
```
## 6.如果需要取消订阅，可以通过这种方式

```
 private CompositeDisposable compositeDisposable;

 private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

 public void topicData() {
    resetSubscriptions();
    Disposable dispTopic =  stompClient.topic(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    Log.i(TAG, stompMessage.getPayload());
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
     compositeDisposable.add(dispTopic);

    }

//取消订阅
public void unSubcribe() {
        compositeDisposable.dispose();
}


```

## 7.设置全局异常监听(如果不设置，连接出现异常时会出现The exception was not handled due to missing onError，并闪退)
```
   RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                L.i("throwable==", throwable.getMessage());
            }
        });
```
-----------------------完成--------------------

有遇到其他问题可以加我VX进行沟通
 VX：459005147 备注：android交流


