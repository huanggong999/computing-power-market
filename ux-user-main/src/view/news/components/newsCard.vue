<template>
    <div class="news-card">
        <img class="image" :src="data.image" />
        <div class="info">
            <div class="name">{{ data.name }}</div>
            <div class="createTime">发布时间:{{ data.createTime }}</div>
            <div class="memo">{{ data.memo }}</div>
        </div>
    </div>
</template>

<script setup lang="ts" name="TaskCenter">
const props = defineProps({
    data: {
        type: Object,
        default: () => {
            return {}
        },
    },
})
</script>
<style lang="scss" scoped>
.news-card {
    padding: 29px 0px;
    border-bottom: #d5d6db 1px solid;
    display: flex;
    width: 1320px;

    &:hover {
        cursor: pointer;
    }

    .image {
        width: 210px;
        height: 157px;
        border-radius: 8px 8px 8px 8px;
        margin-right: 28px;
    }

    .info {
        display: flex;
        flex-direction: column;


        .name {
            font-weight: bold;
            font-size: 20px;
            margin-bottom: 20px;
        }

        .createTime {
            font-weight: 400;
            font-size: 14px;
            color: #83889D;
            margin-bottom: 20px;
        }

        .memo {
            max-width: 1082px;
            font-weight: 400;
            font-size: 16px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            /* 限制显示的行数 */
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    }
}
</style>