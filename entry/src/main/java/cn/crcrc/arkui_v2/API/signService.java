package cn.crcrc.arkui_v2.API;

import cn.crcrc.arkui_v2.UserInfo;
import com.bluelinelabs.logansquare.LoganSquare;
import com.huawei.agconnect.auth.*;
import com.huawei.hmf.tasks.HarmonyTask;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.TaskExecutors;
import ohos.annotation.f2pautogen.InternalAbility;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

@InternalAbility(registerTo = "cn.crcrc.arkui_v2.MainAbility") // 此处registerTo的参数为项目中MainAbility类的全称
public class signService {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x000101, "app log");

    public int getAuthCode(String phoneNumberStr) {
        final int[] successCode = {0};
        final CountDownLatch latch = new CountDownLatch(1);
        System.out.println("app log 测试");

        String countryCodeStr = "86";
//        String phoneNumberStr = "15297810596";

        System.out.println("app log 申请验证码");
        HiLog.info(LABEL, "申请验证码");

        VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.CHINA)
                .build();
        HarmonyTask<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(countryCodeStr, phoneNumberStr, settings);
        task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
            @Override
            public void onSuccess(VerifyCodeResult verifyCodeResult) {

                //验证码申请成功
                HiLog.info(LABEL, "验证码申请成功");
                System.out.println("app log 验证码申请成功");
                successCode[0] = 1;
                System.out.println("app log 类内部：" + successCode[0]);
                latch.countDown();
            }
        }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                HiLog.info(LABEL, "验证码申请失败");
                System.out.println("app log 验证码申请失败" + e.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("app log 等待失败");
        }
        System.out.println("app log 类外部：" + successCode[0]);
        return successCode[0];
    }

    public int createUser(String phoneNumberStr, String verifyCode) {
        final int[] successCode = {0};
        final CountDownLatch latch = new CountDownLatch(1);
        String countryCodeStr = "86";
//        String phoneNumberStr = "15297810596";

        PhoneUser phoneUser = new PhoneUser.Builder()
                .setCountryCode(countryCodeStr)
                .setPhoneNumber(phoneNumberStr)
                .setVerifyCode(verifyCode)
//                .setPassword("your password")  //optional
                .build();
        AGConnectAuth.getInstance().createUser(phoneUser)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {

                        //创建帐号成功后，默认已登录
                        System.out.println("app log 创建账号成功");
                        successCode[0] = 1;
                        latch.countDown();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        System.out.println("app log 创建账号失败" + e.getMessage());
                        latch.countDown();
                    }
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("app log 等待失败");
        }
        return successCode[0];
    }

    public int logInWithCode(String phoneNumberStr, String verifyCode) {
        final int[] successCode = {0};
        final CountDownLatch latch = new CountDownLatch(1);
        String countryCodeStr = "86";
//        String phoneNumberStr = "15297810596";

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {  // 不为空，即已登录
            String id = user.getUid();
            System.out.println("app log 已登录用户id：" + id);
            successCode[0] = -1;
        } else {

            /* 采用验证码方式创建手机帐号凭证 */
            AGConnectAuthCredential credential = PhoneAuthProvider.credentialWithVerifyCode(countryCodeStr, phoneNumberStr, "", verifyCode);

            /* 登录 */
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                        @Override
                        public void onSuccess(SignInResult signInResult) {

                            //获取登录信息
                            AGConnectUser userTemp = AGConnectAuth.getInstance().getCurrentUser();
                            String id = userTemp.getUid();
                            System.out.println("app log 登陆成功：" + id);
                            successCode[0] = 1;
                            latch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {

                            System.out.println("app log 登录失败" + e.getMessage());
                            latch.countDown();
                        }
                    });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("app log 等待失败");
        }
        return successCode[0];
    }

    public int verifyLoginState() {
        int successCode = 0;
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {  // 不为空，即已登录
            String id = user.getUid();
            System.out.println("app log 已登录用户id：" + id);
            successCode = -1;
        } else {
            successCode = 1;
        }
        return successCode;
    }

    public String getUserInfo() {

        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        // 构造 JSON Bean
        UserInfo userInfo = new UserInfo();
        UserInfo.UserExtra userExtra = new UserInfo.UserExtra();
        List<UserInfo.UserExtra> userExtras = new ArrayList<>();

        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {  // 不为空，即已登录
            boolean isAnonymous = user.isAnonymous();
            String id = user.getUid();
            String Email = user.getEmail();
            String Phone = user.getPhone();
            String Name = user.getDisplayName();
            String Photo = user.getPhotoUrl();
            String providerID = user.getProviderId();
            final String[] token = new String[1];
            // 化异步为同步
            user.getToken(false).addOnSuccessListener(new OnSuccessListener<TokenResult>() {
                @Override
                public void onSuccess(TokenResult tokenResult) {
                    token[0] = tokenResult.getToken();
                    latch1.countDown();
                }
            });
            try {
                latch1.await();
            } catch (InterruptedException e) {
                System.out.println("app log 等待失败 getToken");
            }

            user.getUserExtra().addOnSuccessListener(new OnSuccessListener<AGConnectUserExtra>() {
                @Override
                public void onSuccess(AGConnectUserExtra agConnectUserExtra) {
                    userExtra.setCreateTime(agConnectUserExtra.getCreateTime());
                    userExtra.setLastSignInTime(agConnectUserExtra.getLastSignInTime());
                    latch2.countDown();
                }
            });
            try {
                latch2.await();
            } catch (InterruptedException e) {
                System.out.println("app log 等待失败 getUserExtra");
            }

            userExtras.add(userExtra);
            // 设值
            userInfo.setIsAnonymous(String.valueOf(isAnonymous));
            userInfo.setUid(id);
            userInfo.setEmail("crc681225@163.com");
            userInfo.setPhone(Phone);
            userInfo.setName(Name);
            userInfo.setPhotoUrl(Photo);
            userInfo.setProviderId(providerID);
            //userInfo.setAccessToken(token[0]);
            userInfo.setUserExtras(userExtras);

            // JSON 序列化
            try {
                String jsonString  = LoganSquare.serialize(userInfo);
                System.out.println("app log " + jsonString);
                return jsonString;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("app log 转化为json失败");
            }
        } else {
            System.out.println("app log 当前无用户登录");
        }
        return "failure";
    }

}
