
export default {
    data: {
        title: 'World',
        isLogin: false,
        avatarURL: './common/images/png/icon_app.png',
        userName: '我要登录',
        itemList1: [
            {
                itemIcon: '/common/images/png/AccountMange.png',
                itemName: '编辑资料',
                itemValue: ''
            },
            {
                itemIcon: '/common/images/png/UserLevel.png',
                itemName: '个人等级',
                itemValue: 'LV 0'
            },
            {
                itemIcon: '/common/images/png/Points.png',
                itemName: '个人积分',
                itemValue: ''
            }
        ],
        itemList2: [
            {
                itemIcon: '/common/images/png/post.png',
                itemName: '我的帖子',
                itemValue: ''
            },
            {
                itemIcon: '/common/images/png/Answer.png',
                itemName: '问答',
                itemValue: ''
            },
            {
                itemIcon: '/common/images/png/History.png',
                itemName: '浏览历史',
                itemValue: ''
            },
        ],
        itemList3: [
            {
                itemIcon: '/common/images/png/Setting.png',
                itemName: '设置',
                itemValue: ''
            },
            {
                itemIcon: '/common/images/png/Feedback.png',
                itemName: '意见与反馈',
                itemValue: ''
            },
        ]
    },
    props: ['isLogin'],
}
