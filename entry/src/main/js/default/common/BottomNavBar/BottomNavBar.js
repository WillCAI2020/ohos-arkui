import router from '@system.router';

export default {
    data: {
        //定义菜单项
        menus: [{
                    "text": "首页", "img1": "./common/images/png/NavHome.png", "img2": "./common/images/png/NavHomeCS.png"
                },
                {
                    "text": "发帖", "img1": "./common/images/png/NavNews.png", "img2": "./common/images/png/NavNewsCS.png"
                },
                {
                    "text": "我的", "img1": "./common/images/png/NavUser.png", "img2": "./common/images/png/NavUserCS.png"
                }],
        //定义一个下标
        cinidex: 0,
    },
    changecontent(index) {
        //赋值
        this.cinidex = index;
        this.$emit('eventType1', {text: this.cinidex});
    }
}
