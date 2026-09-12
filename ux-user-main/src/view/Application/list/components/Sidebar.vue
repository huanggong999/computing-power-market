<template>
  <div class="concent">
    <div class="line" v-for="(item, index) in info" :key="index">
      <div class="concent-title">
        <img
          class="concent-img"
          src="@/assets/images/app-blue-card.png"
          alt=""
        />
        <div class="name">{{ item.name }}</div>
      </div>
      <div
        class="children"
        v-for="(itemChildren, indexChildren) in item.childrenList"
        :key="indexChildren"
      >
        <div class="title" @click="selectLine(itemChildren.id, index)">
          <div
            :class="activeId === itemChildren.id ? 'spot-active' : 'spot'"
          ></div>
          <div :class="activeId === itemChildren.id ? 'active' : 'name'">
            {{ itemChildren.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const emit = defineEmits(['screen'])
const props = defineProps({
  info: {
    type: Array,
    default: () => {
      return []
    },
  },
})
const activeId = ref('')
const selectLine = (id, index) => {
  activeId.value = id
  emit('screen', id)
}
</script>
<style scoped lang="scss">
.concent {
  padding: 40px 78px 0 30px;

  .line:not(:first-child) {
    margin-top: 89px;
  }

  &-img {
    width: 39px;
    height: 39px;
  }

  &-title {
    display: flex;

    .name {
      height: 37px;
      margin-left: 14px;
      font-weight: 400;
      font-size: 26px;
      color: #000000;
    }
  }

  .children {
    .title {
      display: flex;
      align-items: center;
      margin-top: 26px;
    }
    &:hover {
      cursor: pointer;
      .spot {
        background: #3972fd;
      }
      .name {
        color: #3972fd;
      }
    }
    .spot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #83889d;
    }

    .name {
      height: 30px;
      margin-left: 8px;
      font-weight: 400;
      font-size: 22px;
      color: #83889d;
    }

    .spot-active {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #3972fd;
    }
    .active {
      height: 30px;
      margin-left: 8px;
      font-weight: 400;
      font-size: 22px;
      color: #3972fd;
    }
  }
}
</style>
