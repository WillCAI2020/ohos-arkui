package cn.crcrc.arkui_example.ability;


import cn.crcrc.arkui_example.utils.Utils;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.ace.ability.AceInternalAbility;
import ohos.app.AbilityContext;
import ohos.event.commonevent.*;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;
import ohos.utils.IntentConstants;
import ohos.utils.net.Uri;

import java.util.concurrent.CountDownLatch;

public class getPhotoInternalAbility extends AceInternalAbility {

    /* InternalAbility 关联信息 */
    private static final String BUNDLE_NAME = "cn.crcrc.arkui_example";
    private static final String ABILITY_NAME = "cn.crcrc.arkui_example.ability.getPhotoInternalAbility";
    private static getPhotoInternalAbility instance;
    private AbilityContext abilityContext;

    // 定义日志标签
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0xF0303, "APP LOG");

    /* 公共事件相关变量 */
    private boolean isSubscribe = false;
    private EventRunner runner = EventRunner.create();
    private EventHandler handler = new EventHandler(runner);
    private CommonEventSubscriber subscriber;

    /* 所需量传递 */
    private CountDownLatch latch;
    private Utils utils;
    private String photoUriString;

    // 如果多个Ability实例都需要注册当前InternalAbility实例，需要更改构造函数，设定自己的bundleName和abilityName
    public getPhotoInternalAbility() {
        super(BUNDLE_NAME, ABILITY_NAME);
    }

    /**
     * Internal ability 注册接口。
     */
    public static void register(AbilityContext abilityContext) {
        instance = new getPhotoInternalAbility();
        instance.onRegister(abilityContext);
    }

    private void onRegister(AbilityContext abilityContext) {
        this.abilityContext = abilityContext;
        utils = new Utils(abilityContext);  // 得到实例
        // 注册公共事件
        subscribeCommonEvent();

        this.setInternalAbilityHandler((code, data, reply, option) -> {
            return this.onRemoteRequest(code, data, reply, option);
        });
    }

    /**
     * Internal ability 注销接口。
     */
    public static void unregister() {
        instance.onUnregister();
    }

    private void onUnregister() {
        // 取消订阅
        try {
            CommonEventManager.unsubscribeCommonEvent(subscriber);
        } catch (RemoteException e) {
            HiLog.error(LABEL, "Exception occurred during unsubscribeCommonEvent invocation.");
        }
        abilityContext = null;
        this.setInternalAbilityHandler(null);
    }

    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        switch (code) {
            case 1001: {

                latch = new CountDownLatch(1); // 需要同步传递数据

                Intent intent = new Intent();
                Operation opt = new Intent.OperationBuilder()
                        .withAction(IntentConstants.ACTION_CHOOSE)
                        .build();
                intent.setOperation(opt);
                intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                intent.setType("image/*");
                abilityContext.startAbility(intent, code);

                try {
                    HiLog.info(LABEL, "等待中");
                    System.out.println("APP LOG 等待中");
                    latch.await();
                } catch (InterruptedException e) {
                    HiLog.info(LABEL, "等待失败");
                    System.out.println("app log 等待失败");
                }
                reply.writeString(photoUriString);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }


    //实现订阅
    private void subscribeCommonEvent() {
        if (!isSubscribe) {
            HiLog.info(LABEL, "订阅开始：");
            //1.构建MatchingSkills对象
            MatchingSkills matchingSkills = new MatchingSkills();
            matchingSkills.addEvent("cn.crcrc.arkui_example.event"); //订阅自定义的公共事件

            //2.构建订阅信息对象
            CommonEventSubscribeInfo subscribeInfo = new CommonEventSubscribeInfo(matchingSkills);

            //3.构建订阅者对象
            subscriber = new CommonEventSubscriber(subscribeInfo) {
                // 回调方法
                @Override
                public void onReceiveEvent(CommonEventData commonEventData) {
                    HiLog.info(LABEL, "已接收公共事件");
                    Boolean setImaDataResult = false;
                    if (commonEventData.getData() != null) {
                        System.out.println("接收到数据：" + commonEventData.getData());
                        if (!commonEventData.getData().equals("fail")) {
                            Uri uri = Uri.parse(commonEventData.getData());
                            long l = System.currentTimeMillis();
                            String fileName = String.valueOf(l);
                            setImaDataResult = utils.setImaData(uri, fileName);
                            photoUriString = "internal://app/" + String.valueOf(l) + ".jpg";
                            HiLog.info(LABEL, "js 访问图片路径：" + photoUriString);
                        }
                        latch.countDown();
                    } else {
                        HiLog.info(LABEL, "已接收公共事件，但数据为空");
                        System.out.println("APP LOG 已接收公共事件，但数据为空");
                    }

                    // 目前 onReceiveEvent 只能在 ui 主线程上执行
                    // 耗时任务派发到子线程异步执行，保证不阻塞 ui 线程
                    final AsyncCommonEventResult result = goAsyncCommonEvent();
                    Boolean finalSetImaDataResult = setImaDataResult;
                    handler.postTask(new Runnable() {
                        @Override
                        public void run() {
                            if (finalSetImaDataResult) {
                                HiLog.info(LABEL, "进行数据库操作");
                                // 存入数据库
                            }
                            HiLog.info(LABEL, "数据库操作完成");
                            result.finishCommonEvent();//结束事件
                        }
                    });
                }
            };

            //4.订阅公共事件的核心动作
            try {
                CommonEventManager.subscribeCommonEvent(subscriber);
                isSubscribe = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            HiLog.info(LABEL, "公共事件不能重复订阅");
            System.out.println("公共事件不能重复订阅！");
        }
    }

}
