<template>
  <div class="main-content">
    <div style="width: 75%; margin: 30px auto; min-height: 1000px">
      <div style="text-align: center">
        <el-button type="success">{{ courseData.type === 'VIDEO'? '视频课' : '图文课' }}</el-button>
        <span style="font-size: 24px; font-weight: bold; color: #333333; margin-left: 20px">{{ courseData.name }}</span>
      </div>
      <div style="text-align: center; margin-top: 15px">
        <span style="color: red" v-if="courseData.price > 0">￥ {{ courseData.price }} / 元</span>
        <span style="color: red" v-if="courseData.price === 0">公开课</span>
        <span style="color: #6ac22d; margin-left: 20px" v-if="courseData.discount > 0"> {{courseData.discount}} 折</span>
        <span style="color: #666666; margin-left: 50px">发布时间：{{ courseData.time }}</span>
      </div>

      <!--   课程保密区域   -->
      <div>
        <div style="font-size: 20px; font-weight: bold;">课程资料</div>
        <div v-if="courseData.price === 0 || ordered">

          <el-row :gutter="20" v-if="courseData.type==='VIDEO'">
            <div style="margin-top: 20px">
              <!-- 左侧：视频卡片 -->
              <el-col :span="18">
                <el-card class="box-card" style="height: 480px;border-radius: 10px;">
                  <div style="display: flex; align-items: center;">
                    <div v-if="courseData.type==='VIDEO'" style="height: 50px; width: 50px; background-color: rgba(55,154,222,0.5); border-radius: 10px; display: flex; justify-content: center; align-items: center">
                      <i class="el-icon-video-camera" style="font-size: 40px;"></i>
                    </div>
                    <span style="margin-left: 10px; font-size: 22px; font-weight: bold; width: 40%" class="overflowShow">{{courseData.name}}</span>
                    <div style="margin-left: auto">
                      <el-button class="el-icon-share" style="font-size: 23px; border: none;" @click="shareWeb()"></el-button>
                      <el-button class="el-icon-download"  style="font-size: 23px; border: none;" @click="downloadFile(courseData.video)"></el-button>
                    </div>
                  </div>
                  <video :src="courseData.video" v-if="courseData.type === 'VIDEO'" controls style="width: 100%; height: 380px; border-radius: 5px; margin-top: 20px"></video>
                </el-card>
              </el-col>

              <!-- 右侧：课程缩略图 -->
              <el-col :span="6">
                <el-card class="box-card" style="border-radius: 10px; height: 480px; overflow-y: auto">
                  <el-row :gutter="20">
                    <el-col :span="24" v-for="(item, index) in rightData" :key="index" style="margin-bottom: 20px;">
                      <a :href="'/front/courseDetail?id='+item.id">
                        <img :src="item.img" alt="" style="width: 100%; height: 100px; border-radius: 5px; border: 1px solid #cccccc">
                      </a>
                      <div style="color: #333333; margin-top: 10px;" class="overflowShow">{{ item.name }}</div>
                    </el-col>
                  </el-row>
                </el-card>
              </el-col>
            </div>
          </el-row>


          <div style="margin-top: 20px" v-if="courseData.file && courseData.file.trim() !== ''">
            资料链接：<a :href="courseData.file" target="_blank">{{ courseData.file }}</a>
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
        <div style="margin-top: 10px; font-size: 14px; " v-html="courseData.content" class="w-e-text w-e-text-container"></div>
      </div>

      <div style="margin-top: 50px; font-size: 18px">欢迎发表您宝贵的意见</div>
      <div style="margin-top: 20px">
        <el-input type="textarea" :rows="5" v-model="content"></el-input>
      </div>
      <div style="margin-top: 10px; text-align: right">
        <el-button type="primary" @click="submit(content, 0)">提交</el-button>
      </div>
      <div style="margin-top: 30px; margin-bottom: 500px">
        <el-row v-for="item in commentData" style="margin-bottom: 30px">
          <el-col :span="4">
            <div style="display: flex; align-items: center;">
              <img :src="item.userAvatar" alt="" style="width: 50px; height: 50px; border-radius: 50%">
              <div style="flex: 1; margin-left: 10px">{{item.userName}}</div>
            </div>
          </el-col>
          <el-col :span="20">
            <div style="height: 50px; line-height: 50px">
              <el-row>
                <el-col :span="18" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{item.content}}</el-col>
                <el-col :span="6" style="text-align: right">{{item.time}}</el-col>
              </el-row>
            </div>
            <div v-for="child in item.children" style="margin-bottom: 5px">
              <el-row>
                <el-col :span="5">
                  <div style="display: flex; align-items: center;">
                    <img :src="child.userAvatar" alt="" style="width: 50px; height: 50px; border-radius: 50%">
                    <div style="flex: 1; margin-left: 10px">{{ child.userName }} 回复：</div>
                  </div>
                </el-col>
                <el-col :span="13" style="height: 50px; line-height: 50px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{child.content}}</el-col>
                <el-col :span="6" style="height: 50px; line-height: 50px; text-align: right">{{child.time}}</el-col>
              </el-row>
            </div>
            <div style="margin-top: 20px">
              <el-input style="width: 400px" v-model="item.tmp"></el-input>
              <el-button type="primary" style="margin-left: 5px" @click="submit(item.tmp, item.id)">回复</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>


    <el-dialog title="课程分享" :visible.sync="courseShare" width="50%" :close-on-click-modal="false" destroy-on-close>
      <div class="w-e-text w-e-text-container"></div>
      <el-input v-model="input"></el-input>
      <el-button type="primary" style="justify-content: center; margin-top: 10px" @click="copyAndRedirect">复制链接并跳转微信</el-button>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      courseId: this.$route.query.id,
      courseData: {},
      rightData:[],
      content: null,
      commentData: [],
      courseShare: false,
      ordered: false,
      input: window.location.href
    }
  },

  created() {
    this.loadCourse()
    this.getRightData()
    this.checkCourse()
    this.loadComment()
  },

  methods: {
    //加载课程数据
    loadCourse(){
      this.$request.get('/course/selectById/' + this.courseId).then(res=>{
        if(res.code === '200'){
          this.courseData = res.data
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    getRightData() {
      this.$request.get('/course/selectAll').then(res => {
        if (res.code === '200') {
          this.rightData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    shareWeb(){
      this.courseShare = true
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
        this.courseShare = false; // 关闭对话框
      }
    },

    //购买课程
    buy(){
      let data = {
        courseId: this.courseId,
        userId: this.user.id
      }
      this.$request.post('/orders/add', data).then(res=>{
        if(res.code === '200'){
          this.$message.success('购买成功')
          this.loadCourse()
          this.checkCourse()
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    //判断订单购买状态
    checkCourse(){
      this.$request.get('orders/selectAll',{
        params: {
          userId: this.user.id,
          courseId: this.courseId
        }
      }).then(res=>{
        if(res.code === '200'){
          if(res.data.length > 0){
            this.ordered = true
          }
        }else{
          this.$message.error(res.msg)
        }
      })
    },

    loadComment() {
      this.$request.get('/comment/selectAll').then(res => {
        if (res.code === '200') {
          this.commentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    submit(content, parentId) {
      let data = {
        userId: this.user.id,
        courseId: this.courseId,
        content: content,
        parentId: parentId,
      }
      this.$request.post('/comment/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('评论成功')
          this.content = null
          this.loadComment()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>

<style scoped>
</style>