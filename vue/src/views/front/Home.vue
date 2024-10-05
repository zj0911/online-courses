<template>
  <div class="main-content">
    <div>
    <!--轮播图-->
    <el-carousel height="350px">
      <el-carousel-item v-for="item in carouselData">
        <img :src="item" alt="" style="width: 100%; height: 400px">
      </el-carousel-item>
    </el-carousel>
  </div>

  <!--课程内容-->
    <div style="width: 80%; margin: 30px auto;">
      <!--按钮-->
      <div style="display: flex">
        <div style="flex: 1">
          <span style="color: #333333; font-size: 24px; font-weight: bold;
          margin-right: 50px">在线课程</span>
          <el-button type="primary" @click="initValue('VIDEO')">视频课程</el-button>
          <el-button type="warning" @click="initValue('TEXT')">图文课程</el-button>
          <el-button type="success" @click="initValue('SCORE')">积分专区</el-button>
        </div>
        <div style="width: 300px">
          <el-button type="info" @click="signIn()">签到</el-button>
          <span style="margin-left: 15px; color: #67c03d">上次签到:</span>
          <span style="color: #666666; margin-left: 5px">{{ signData?.time }}</span>
        </div>
      </div>
      <!--课程信息1-->
      <div style="display: flex; margin-top: 20px; height: 300px">
        <div style="flex: 1; margin-right: 10px; width: 0">
          <a :href="(type === 'SCORE' ? '/front/scoreDetail?id=' : '/front/courseDetail?id=') + recommend.id">
            <img :src="recommend.img" alt="" style="width: 100%; height: 270px; border-radius: 5px">
          </a>
          <div style="font-size: 15px; margin-top: 5px" class="overflowShow">{{ recommend.name }}</div>
        </div>
        <div style="flex: 2; margin-left: 10px">
          <el-row :gutter="20">
            <el-col :span="6" style="margin-bottom: 35px" v-for="item in rightData">
              <a :href="(type === 'SCORE' ? '/front/scoreDetail?id=' : '/front/courseDetail?id=') + item.id">
                <img :src="item.img" alt="" style="width: 100%; height: 100px; border-radius: 5px; border: 1px solid #cccccc">
              </a>
              <div style="color: #333333; margin-top: 10px" class="overflowShow">{{ item.name }}</div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!--课程信息2-->
      <div style="display: flex; margin-top: 50px">
        <div style="flex: 1">
          <span style="color: #333333; font-size: 24px; font-weight: bold;
          margin-right: 50px">在线资源</span>
        </div>
      </div>
      <div style="display: flex; margin-top: 20px">
        <div style="flex:2; margin-right: 10px;">
          <el-row :gutter="20">
            <el-col :span="6" style="margin-bottom: 35px" v-for="item in infoData">
              <a :href="'/front/informationDetail?id=' + item.id">
                <img :src="item.img" style="width: 100%; border-radius: 5px; height: 100px">
              </a>
              <div style="color: #333333; margin-top: 10px" class="overflowShow">
                {{ item.name }}
              </div>
            </el-col>
          </el-row>
        </div>
        <div style="flex:1; margin-left: 10px; width: 0">
          <a :href="'/front/informationDetail?id=' + infoRecommend.id">
            <img :src="infoRecommend.img" alt="" style="width: 100%; height: 270px; border-radius: 5px">
          </a>
          <div style="color: #333333; margin-top: 10px" class="overflowShow">{{ infoRecommend.name }}</div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      carouselData: [
        require('@/assets/imgs/lun-1.jpg'),
        require('@/assets/imgs/lun-2.jpg'),
        require('@/assets/imgs/lun-3.jpg'),
        require('@/assets/imgs/lun-4.jpg'),
      ],
      recommend: {},
      rightData: [],
      type: 'VIDEO',
      infoRecommend: {},
      infoData: [],
      signData: {},
    }
  },
  mounted() {
    this.getData()
    this.getInformation()
    this.getSign()
  },

  methods: {
    initValue(type) {
      this.type = type;
      this.getData()
    },
    getData() {
      // 积分专区这边的数据
      if ('SCORE' === this.type) {
        // 1. 获取推荐的那个积分课程
        this.getRecommend('/score/getRecommend')
        // 2. 获取推荐之外的最新的8个课程
        this.getRightData('/score/selectTop8')
      } else {
        // 1. 获取推荐的那个视频课程
        this.getRecommend('/course/getRecommend?type=' + this.type)
        // 2. 获取推荐之外的最新的8个视频课程
        this.getRightData('/course/selectTop8?type=' + this.type)
      }
    },

    getRecommend(url) {
      this.$request.get(url).then(res => {
        if (res.code === '200') {
          this.recommend = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getRightData(url) {
      this.$request.get(url).then(res => {
        if (res.code === '200') {
          this.rightData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    getInformation() {
      //推荐数据
      this.$request.get('/information/getRecommend').then(res => {
        if (res.code === '200') {
          this.infoRecommend = res.data
        } else {
          this.$message.error(res.msg)
        }
      })

      //前八个数据
      this.$request.get('/information/selectTop8').then(res => {
        if (res.code === '200') {
          this.infoData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    //签到
    signIn() {
      let data = {
        userId: this.user.id
      }
      this.$request.post('/signin/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('签到成功，恭喜你获得10个积分')
          this.getSign()
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    //获取签到数据
    getSign() {
      this.$request.get('/signin/selectByUserId?id=' + this.user.id).then(res => {
        if (res.code === '200') {
          this.signData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

  }
}
</script>
