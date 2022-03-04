import wantConstant from '@ohos.ability.wantConstant';
import featureAbility from '@ohos.ability.featureAbility';
import dataStorage from '@ohos.data.storage'

// abilityType: 0-Ability; 1-Internal Ability
const ABILITY_TYPE_EXTERNAL = 0;
const ABILITY_TYPE_INTERNAL = 1;
// syncOption(Optional, default sync): 0-Sync; 1-Async
const ACTION_SYNC = 0;
const ACTION_ASYNC = 1;

export default {
    data: {
        title: 'World',
        avatarURL: './common/images/icon_app.png',
        avatarURLEx: './common/images/camera.png',
        tips: '点击更换头像',
    },
makeAction(bundleName, abilityName, code, abilityType, data) {
    const action = {};
    action.bundleName = bundleName;
    action.abilityName = abilityName;
    action.messageCode = code;
    action.abilityType = abilityType;
    action.data = data;
    action.syncOption = 0;
    return action;
},
async testGetPhotoInternalAbility() {
    const action = this.makeAction('cn.crcrc.arkui_example',
        'cn.crcrc.arkui_example.ability.getPhotoInternalAbility', 1001, 1, {});
    const result = await FeatureAbility.callAbility(action);
    console.info(result)
    if(result != null){
        this.avatarURL = result
    }
},
testGetPhotoLocalParticleAbility(){
    this.javaInterface = createLocalParticleAbility('cn.crcrc.arkui_example.ability.getPhotoLocalParticleAbility');
    this.javaInterface.getPhotoUri().then(result => {
        console.info(result);
        if(result != null){
            this.avatarURL = result
        }
    }, error => {
        console.error('testGetPhotoLocalParticleAbility 出错');
    });
}
}
