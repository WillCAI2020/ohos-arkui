package cn.crcrc.arkui_example;

import cn.crcrc.arkui_example.ability.getPhotoInternalAbility;
import cn.crcrc.arkui_example.ability.getPhotoLocalParticleAbility;
import ohos.aafwk.content.Operation;
import ohos.ace.ability.AceAbility;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.event.commonevent.CommonEventPublishInfo;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.RemoteException;
import ohos.security.SystemPermission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainAbility extends AceAbility {

    // 定义日志标签
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0xF0303, "APP LOG");

@Override
public void onStart(Intent intent) {
    getPhotoInternalAbility.register(this);
    super.onStart(intent);
    //requestPermissions();
    //getPhotoLocalParticleAbility.getInstance().register(this);
}

    @Override
    public void onStop() {
        // 注销
        getPhotoInternalAbility.unregister();
        //getPhotoLocalParticleAbility.getInstance().deregister(this);
        super.onStop();
    }

    // 动态申请权限
    private void requestPermissions() {
        String[] permissions =
                {
                        SystemPermission.MEDIA_LOCATION,
                        SystemPermission.WRITE_MEDIA,
                        SystemPermission.READ_MEDIA,
                };
        List<String> permissionFiltered =
                Arrays.stream(permissions)
                        .filter(permission -> verifySelfPermission(permission)
                                != IBundleManager.PERMISSION_GRANTED).collect(Collectors.toList());
        requestPermissionsFromUser(permissionFiltered.toArray(new String[permissionFiltered.size()]), 0);
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions
            , int[] grantResults) {
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }
        for (int grantResult : grantResults) {
            if (grantResult != IBundleManager.PERMISSION_GRANTED) {
                terminateAbility();
                break;
            }
        }
    }

/**
 * 跳转回调
 */
@Override
protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
    super.onAbilityResult(requestCode, resultCode, resultData);
    try {
        System.out.println("APP LOG resultData：" + resultData.getUriString());
        // 通过发布有序公共事件传递信息
        orderlyEventPublish(resultData.getUriString());
    } catch (Exception e) {
        orderlyEventPublish("fail");
        e.printStackTrace();
    }
}

/*
 * 发布有序的公共事件
 */
private void orderlyEventPublish(String data) {
    HiLog.info(LABEL, "发布有序公共事件开始");
    //1.构建一个Intent对象，包含了自定义的事件的标识符
    Intent intent = new Intent();
    Operation oper = new Intent.OperationBuilder().withAction("cn.crcrc.arkui_example.event") //就是自定义的公共事件的标识
            .build();
    intent.setOperation(oper);
    //2.构建CommonEventData对象
    CommonEventData commonEventData = new CommonEventData(intent);

    //仅仅只有有序的公共事件，才能携带的两个专用属性,可选的参数，不是必须的
    commonEventData.setData(data);
    commonEventData.setCode(1001);
    //配置公共事件的对应权限
    CommonEventPublishInfo publishInfo = new CommonEventPublishInfo();
    publishInfo.setOrdered(true);

    //3.核心的发布事件的动作,发布的公共事件，有序的公共事件
    try {
        CommonEventManager.publishCommonEvent(commonEventData, publishInfo);
        HiLog.info(LABEL, "发布有序公共事件完成");
    } catch (RemoteException e) {
        e.printStackTrace();
    }
}
}
