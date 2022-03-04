import validator from 'validator'
import sleep from 'sleep-promise'
import router from '@system.router';
import prompt from '@system.prompt';
import Service from '../../common/api/signService.js'; // 此处FA路径和类名对应之前的jsOutput路径以及InternalAbility的名字
import inputMethod from '@ohos.inputMethod'

export default {
    data: {
        agree: true,
        mobile: '15297810596',
        code: '',
        retryCode: '获取验证码',
        getting: false, //获取验证码中
        canLogin: false, //是否可以登录
        canGetCode: false, //可以获取验证码
        sent: false, //验证码已发送
        remainTime: 60, //倒计时剩余时间
        showRemainTime: false, //是否显示剩余时间
        timerID: 0, //计时器的序号
        inputFocusEn: true,
    },

    //倒计时算法 每秒减 1
    countDown() {
        if (this.remainTime != 0) {
            this.remainTime -= 1
        } else {
            // 倒计时结束，重新开始
            this.remainTime = 60
            clearInterval(this.timerID)
            this.retryCode = "重发验证码"
            this.sent = false
            this.canGetCode = true
            this.showRemainTime = false
        }

    },

    navTologInWithPwd() {
        router.push({
            uri: "pages/LoginWithPWD/LoginWithPWD"
        })
    },

    navToIndex() {
        router.replace({
            uri: "pages/index/index",
            params: {
                isLogin: true
            }
        })
    },

    async gettingCode() {
        this.getting = true // 等待

        // 调试用
        /*        await sleep(2000);
        this.sent = true
        this.showRemainTime = true
        this.timerID = setInterval(this.countDown, 1000) // 验证码倒计时
        prompt.showToast({
            message: '验证码已发送'
        })
        this.getting = false // 等待*/

        const echo = new Service()
        echo.getAuthCode(this.mobile)
            .then((data => {
                console.info(data["abilityResult"]);
                if (data["abilityResult"] == '1') {
                    this.sent = true
                    this.showRemainTime = true
                    this.timerID = setInterval(this.countDown, 1000) // 验证码倒计时
                    prompt.showToast({
                        message: '验证码已发送'
                    })
                } else {
                    prompt.showToast({
                        message: '验证码发送失败'
                    })
                }
                this.getting = false // 停止等待
            }))

    },

    validateInput() {
        // 不是中国的手机号，则无法获取验证码
        if (!validator.isMobilePhone(this.mobile, ['zh-CN'])) {
            this.canGetCode = false //&& this.agree
        } else {
            this.canGetCode = true //&& this.agree
        }
    },

    checkAgree() {
        this.agree = !this.agree
        this.validateInput()
    },

    changePhone(event) {
        this.mobile = event.value
        this.validateInput()
    },

    verifyCode(event) {
        this.code = event.value
        if (this.code.length == 6) {

            /*            if(this.code == '666666')
            {
                this.navToIndex('ruchan')
            }*/

            const echo = new Service();
            echo.logInWithCode(this.mobile, this.code).then((data => {
                if (data["abilityResult"] == 1) {
                    prompt.showToast({
                        message: '登录成功'
                    })
                    this.navToIndex()
                }
                else if (data["abilityResult"] == -1) {
                    prompt.showToast({
                        message: '已登录'
                    })
                }
                else {
                    prompt.showToast({
                        message: '登录失败'
                    })
                }
            }))
        }

    },
    back() {
        router.back()
    },
    hideKeyBoard() {
        var InputMethodController = inputMethod.getInputMethodController();
        InputMethodController.stopInput((error, isSuccess) => {
            if (error) {
                console.error('Failed to stop Input. Cause: ' + error.message);
                return;
            }
        });
        this.inputBlur(this.inputFocusEn)
    },
    async inputBlur(focusEnable) {
        focusEnable = false
        await sleep(500)
        focusEnable = true
    }
}
