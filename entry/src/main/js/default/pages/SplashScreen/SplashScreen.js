import router from '@system.router';
import Service from '../../common/api/signService.js';

var timeoutID;
var intervalID;
var skipTime = 1;

export default {
    data: {
        "skip": "跳过 1",
        "ads": "广告",
        "app_name": "视频",
        "copyright": "您可以在这里填写版权及其他信息"
    },
    onInit() {
        this.timeout();
    },
    async timeout() {
        let that = this;
        intervalID = setInterval(function () {
            // -1 代表最后一个 从开始到结束，不包括结束
            that.skip = that.skip.slice(0, -1) + skipTime;
            skipTime -= 1;
        }, 1000);
        timeoutID = setTimeout(function () {
            clearTimeout(timeoutID);
            clearInterval(intervalID);
            that.navigate();
        }, 2000);
    },
    async skipButtonClicked() {
        clearTimeout(timeoutID);
        clearInterval(intervalID);
        this.navigate();
    },
    async navigate() {
        const echo = new Service()
        echo.verifyLoginState().then((data =>{
            console.info('初始化获取登录状态：'+data["abilityResult"]);
            if(data["abilityResult"] == -1)
            {
                console.info('已登录')
                this.navToIndex()
            }else if(data["abilityResult"] == 1)
            {
                console.info('未登录')
                this.navTologInWithPwd()
            }
        }))
    },
    navToIndex() {
        router.replace({
            uri: "pages/index/index",
            params: {
                isLogin: true
            }
        })
    },
    navTologInWithPwd() {
        router.replace({
            uri: "pages/LoginWithPWD/LoginWithPWD"
        })
    },
}
