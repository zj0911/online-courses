<template>
  <div class="main-content">
    <div style="width: 75%; margin: 30px auto; min-height: 1000px">
      <div style="text-align: center">
        <el-button type="success">{{ scoreData.type === 'VIDEO'? '视频课' : '图文课' }}</el-button>
        <span style="font-size: 24px; font-weight: bold; color: #333333; margin-left: 20px">{{ scoreData.name }}</span>
      </div>
      <div style="text-align: center; margin-top: 15px">
<!--        <span style="color: red" v-if="scoreData.price > 0">￥ {{ scoreData.price }} / 元</span>-->
        <span style="color: red" v-if="scoreData.price === 0">公开课</span>
        <span style="color: red; margin-left: 20px" v-if="scoreData.discount > 0">折后价: {{(scoreData.discount * 0.1 * scoreData.price).toPrecision(2) }}/积分</span>
        <span style="color: #666666; margin-left: 50px">发布时间：{{ scoreData.time }}</span>
      </div>

      <!--   课程保密区域   -->
      <div>
        <div style="font-size: 20px; font-weight: bold;">课程资料</div>
        <div v-if="scoreData.price === 0 || ordered">

          <div style="margin-top: 20px">
            <el-row :gutter="20" v-if="scoreData.type==='VIDEO'">
              <!-- 左侧：视频卡片 -->
              <el-col :span="18">
                <el-card class="box-card" style="height: 500px;border-radius: 10px;">
                  <div style="display: flex; align-items: center;">
                    <div v-if="scoreData.type==='VIDEO'" style="height: 50px; width: 50px; background-color: rgba(55,154,222,0.5); border-radius: 10px; display: flex; justify-content: center; align-items: center">
                      <i class="el-icon-video-camera" style="font-size: 40px;"></i>
                    </div>
                    <span style="margin-left: 10px; font-size: 22px; font-weight: bold; width: 40%" class="overflowShow">{{scoreData.name}}</span>
                    <div style="margin-left: auto">
                      <el-button class="el-icon-share" style="font-size: 23px; border: none;" @click="shareWeb()"></el-button>
                      <el-button class="el-icon-download"  style="font-size: 23px; border: none;" @click="downloadFile(scoreData.video)"></el-button>
                    </div>
                  </div>
                  <video :src="scoreData.video" v-if="scoreData.type === 'VIDEO'" controls style="width: 100%; height: 380px; border-radius: 5px; margin-top: 20px"></video>
                </el-card>
              </el-col>

              <!-- 右侧：课程缩略图 -->
              <el-col :span="6">
                <el-card class="box-card" style="border-radius: 10px; height: 500px; overflow-y: auto">
                  <el-row :gutter="20">
                    <el-col :span="24" v-for="(item, index) in rightData" :key="index" style="margin-bottom: 20px;">
                      <a :href="'/front/scoreDetail?id='+item.id">
                        <img :src="item.img" alt="" style="width: 100%; height: 100px; border-radius: 5px; border: 1px solid #cccccc">
                      </a>
                      <div style="color: #333333; margin-top: 10px;" class="overflowShow">{{ item.name }}</div>
                    </el-col>
                  </el-row>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <div style="margin-top: 20px" v-if="scoreData.file && scoreData.file.trim() !== ''">
            资料链接：<a :href="scoreData.file" target="_blank">{{ scoreData.file }}</a>
          </div>
          <div style="margin-top: 20px" v-else>暂无其他资料提供</div>
        </div>
        <div v-else>
          <span style="color: red; margin-right: 20px">该课程属于付费课程，购买后可解锁</span>
          <el-button type="warning" size="mini" @click="buy">购买课程</el-button>
        </div>
      </div>
      <!--   课程介绍区域   -->
      <div style="margin-top: 50px">
        <div style="font-size: 20px; font-weight: bold">课程介绍</div>
        <div style="margin-top: 10px; font-size: 14px; " v-html="scoreData.content" class="w-e-text w-e-text-container"></div>
      </div>
    </div>

    <el-dialog title="课程分享" :visible.sync="scoreShare" width="50%" :close-on-click-modal="false" destroy-on-close>
      <div class="w-e-text w-e-text-container"></div>
      <el-input v-model="input"></el-input>
      <el-button type="primary" style="justify-content: center; margin-top: 10px" @click="copyAndRedirect">复制链接并跳转微信</el-button>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    let scoreId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      scoreId: scoreId,
      scoreData: {},
      rightData:[],
      scoreShare: false,
      ordered: false,
      input: window.location.href
    }
  },
  created() {
    this.loadScore()
    this.checkCourse()
    this.getRightData()
  },

  methods: {
    loadScore(){
      this.$request.get('/score/selectById/' + this.scoreId).then(res=>{
        if(res.code === '200'){
          this.scoreData = res.data
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    getRightData() {
      this.$request.get('/score/selectAll').then(res => {
        if (res.code === '200') {
          this.rightData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    shareWeb(){
      this.scoreShare = true
    },

    downloadFile(url){
      location.href = url
    },

    async copyAndRedirect() {
      let url = this.input; // 获取当前网页的 URL
      try {
        // 复制 URL 到剪贴板
        await navigator.clipboard.writeText(url);
        console.log('当前网页的 URL 已复制到剪贴板');

        setTimeout(() => {
          // 延迟一会儿跳转到微信
          window.location.href = `weixin://`;
        }, 500); // 延迟 500 毫秒确保复制操作完成

      } catch (err) {
        console.error('复制失败:', err);
      } finally {
        this.scoreShare = false; // 关闭对话框
      }
    },

    //购买课程
    buy(){
      let data = {
        scoreId: this.scoreId,
        userId: this.user.id,
        score: this.scoreData.price
      }
      this.$request.post('/scoreorder/add', data).then(res=>{
        if(res.code === '200'){
          this.$message.success('购买成功')
          this.loadScore()
          this.checkCourse()
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    //判断订单购买状态
    checkCourse(){
      this.$request.get('scoreorder/selectAll',{
        params: {
          userId: this.user.id,
          scoreId: this.scoreId
        }
      }).then(res=>{
        if(res.code === '200'){
          if (res.data.length > 0) {
            this.ordered = true
          }
        }else{
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>

<style scoped>
</style>