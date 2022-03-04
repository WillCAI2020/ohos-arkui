export default {
    data: {
        title: "",
        cindex: 0,
        isLogin: false
    },
    onInit() {
        this.title = this.$t('strings.world');
    },
    textClicked (e) {
        this.cindex = e.detail.text;
    },
}
