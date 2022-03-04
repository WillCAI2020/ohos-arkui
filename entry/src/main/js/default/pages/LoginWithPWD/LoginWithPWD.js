import Service from '../../common/api/signService.js'; // 此处FA路径和类名对应之前的jsOutput路径以及InternalAbility的名字
import validator from 'validator'
import sleep from 'sleep-promise'
import router from '@system.router';
import prompt from '@system.prompt';

export default {
    data: {
        title: 'World',
        agree: false,
        mobile: '15297810596',
        password: '',
        logining: false, //登录中
        canLogin: false, //是否可以登录
        result: '结果：'
    },
    onInit(){
        // 测试
        /*        echo.test().then((data => {
            var ret = JSON.parse(data["abilityResult"]); // 反序列化
            console.info('1' + ret.error_code)
            console.info('2' + ret.reason)
            console.info('3'+ ret.result.temp)
            console.info('4'+JSON.stringify(ret.result))
            console.info('5'+ret.result[0].temp)
        }))*/
    },
    async login() {
        this.logining = true
        await sleep(2000)
        this.logining = false
        this.navToIndex()
        /*        const echo = new Service();
        echo.getAuthCode()
            .then((data) => {
                this.result += data["abilityResult"]; // 此处取到运算结果，并加到title之后
                console.info(this.result);
            });*/
    },
    validateInput() {
        // 不是中国的手机号，则无法获取验证码
        if (!validator.isMobilePhone(this.mobile, ['zh-CN']) || this.password.length < 3) {
            this.canLogin = false
        } else {
            this.canLogin = true
        }
    },
    changePhone(event) {
        this.mobile = event.value
        this.validateInput()
    },
    changePwd(event) {
        this.password = event.value
        this.validateInput()
    },
    navToIndex() {
        router.replace({
            uri: "pages/index/index",
            params: {
                isLogin: true
            }
        })
    },
    navToLogInWithCode() {
        router.push({
            uri: "pages/LoginWithCode/LoginWithCode"
        })
    },
}
