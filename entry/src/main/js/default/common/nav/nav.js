import router from '@system.router';

export default {
    data: {
        title: ''
    },
    props: ['title'],

    back(){
        router.back()
    }
}
