<template>
  <div class="main-content">
    <el-card style="width: 80%; margin: 30px auto; border-radius: 10px">
      <div style="margin: 20px 0">我兑换过的课程（{{total}}）</div>
      <div class="table">
        <el-table :data="tableData" stripe>
          <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
          <el-table-column prop="scoreImg" label="课程封面" width="100">
            <template v-slot="scope">
              <div style="display: flex; align-items: center">
                <el-image style="width: 60px; height: 40px; border-radius: 10px" v-if="scope.row.scoreImg"
                          :src="scope.row.scoreImg" :preview-src-list="[scope.row.scoreImg]"></el-image>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="300" show-overflow-tooltip>
            <template v-slot="scope">
              <a :href="'/front/scoreDetail?id=' + scope.row.scoreId">{{ scope.row.scoreName }}</a>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="兑换积分" width="100"></el-table-column>
          <el-table-column prop="orderId" label="订单编号" width="200"></el-table-column>
          <el-table-column prop="time" label="兑换时间" width="200"></el-table-column>
        </el-table>

        <div class="pagination" style="margin-top: 20px">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="total">
          </el-pagination>
        </div>
      </div>
    </el-card>

    <div style="position: fixed; bottom: 20px; width: 30%; left: 50%; transform: translateX(-50%); display: flex; justify-content: center; margin: 0 auto;">
      <el-row :gutter="1" style="width: 100%; display: flex; justify-content: space-around;">
        <el-col :span="5" style="text-align: center; margin-left:15px; border-right:2px solid #606266;">
          <a href="/front/orders" style="color: #606266;">已购课程</a>
        </el-col>
        <el-col :span="5" style="text-align: center;  border-right:2px solid #606266;">
          <a href="/front/scoreOrder" style="color: #606266">积分课程</a>
        </el-col>
        <el-col :span="5" style="text-align: center;   border-right:2px solid #606266;">
          <a href="/front/fileOrder" style="color: #606266">资料兑换</a>
        </el-col>
        <el-col :span="5" style="text-align: center; margin-right:15px;">
          <a href="/front/myInfo" style="color: #606266">我的资料</a>
        </el-col>
      </el-row>
    </div>

  </div>
</template>
<script>

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 6,  // 每页显示的个数
      total: 0,
      name: null,
    }
  },
  mounted() {
    this.load(1)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/scoreorder/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          userId: this.user.id,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>