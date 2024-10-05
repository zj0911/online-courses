<template>
  <div class="main-content">
    <el-card style="width: 70%; margin: 30px auto;  border-radius: 10px;">
      <div style="text-align: center">
        <span style="font-size: 24px; font-weight: bold; color: #333333; margin-left: 20px">{{ informationData.name }}</span>
      </div>
      <el-card style="width: 85%; margin: 15px auto 15px auto; background-color: rgba(214,215,217,0.2); text-align: center; border: none; box-shadow: none">
        <span style="color: red" v-if="informationData.score > 0">下载该资源需要 {{ informationData.score }} 积分</span>
        <span style="color: #12b127" v-else>免费白嫖</span>
        <span style="color: #666666; margin-left: 50px">发布时间：{{ informationData.time }}</span>
      </el-card>
      <div style="width: 80%; margin: 15px auto 15px auto; text-align: center; display: flex; justify-content: space-between; ">
        <div style="display: flex; align-items: center;">
          <img :src="informationData.userImg" alt="" style="width: 45px; height: 45px; border-radius: 50%; margin-right: 10px;">
          <span style="font-size: 20px;">{{ informationData.userName }}</span>
        </div>
        <!-- 右侧：按钮 -->
        <el-button type="primary">更多资源</el-button>
      </div>
      <!--   资料保密区域   -->
      <div>
        <div style="font-size: 18px; margin: 10px 0; font-weight: bold">资料链接</div>
        <div v-if="informationData.score === 0 || flag">
          <div style="margin-top: 10px;">资料链接：<a :href="informationData.file" target="_blank">{{ informationData.file }}</a></div>
        </div>
        <div v-else>
          <span style="color: red; margin-right: 20px">该资料需要积分，兑换后可解锁</span>
          <el-button type="warning" size="mini" @click="exchange">兑换资料</el-button>
        </div>
      </div>
      <!--   资料介绍区域   -->
      <div style="margin-top: 20px">
        <div style="font-size: 18px; font-weight: bold">资料介绍</div>
        <div style="margin-top: 10px" v-html="informationData.content" class="w-e-text w-e-text-container"></div>
      </div>
    </el-card>
  </div>
</template>
<script>
import E from 'wangeditor'
export default {

  data() {
    let informationId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      informationId: informationId,
      informationData: {},
      flag: false,
    }
  },
  mounted() {
    this.loadInformation()
    this.check()
  },

  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadInformation() {
      this.$request.get('/information/selectById/' + this.informationId).then(res => {
        if (res.code === '200') {
          this.informationData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    check() {
      this.$request.get('/fileorder/selectAll', {
        params: {
          userId: this.user.id,
          fileId: this.informationId
        }
      }).then(res => {
        if (res.code === '200') {
          if (res.data.length > 0) {
            this.flag = true
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    exchange() {
      let data = {
        fileId: this.informationId,
        userId: this.user.id,
        score: this.informationData.score
      }
      this.$request.post('/fileorder/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('兑换成功')
          this.loadInformation()
          this.check()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>