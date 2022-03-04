package cn.crcrc.arkui_v2;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class UserInfo {

    @JsonField
    private String isAnonymous;    // 是否匿名用户

    @JsonField
    private String Uid; // 用户 id

    @JsonField
    private String Email;   // 用户邮箱

    @JsonField
    private String Phone;   // 用户电话号码

    @JsonField
    private String Name;    // 用户名称

    @JsonField
    private String photoUrl;   // 用户头像

    @JsonField
    private String providerId; // 第三方认证平台名称

    @JsonField
    private String accessToken; // Access Token信息

    @JsonField
    private List<UserExtra> userExtras;

    public String getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(String isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<UserExtra> getUserExtras() {
        return userExtras;
    }

    public void setUserExtras(List<UserExtra> userExtras) {
        this.userExtras = userExtras;
    }

    @JsonObject
    public static class UserExtra {
        @JsonField
        private String createTime;  // 用户创建时间

        @JsonField
        private String lastSignInTime;  // 最近一次登录时间

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getLastSignInTime() {
            return lastSignInTime;
        }

        public void setLastSignInTime(String lastSignInTime) {
            this.lastSignInTime = lastSignInTime;
        }
    }
}
