<template>
  <div class="lend">
  <div class="lendList">

  </div>

  <div class="right">
     <div class="myNotification">
    借阅、还书、将逾期
  </div>
  <div class="rightBottom">
<div class="outTime">
    逾期未还
  </div>
  <div class="extend">
    续借
  </div>
  </div>
  
  </div>
   </div>

</template>

<style scoped>
.lend {
  margin-top: 60px;
  background-color: pink;
  display: flex;
}
.lendList{
  min-height: calc(100vh - 60px);
  width: 60%;
  background-color: lightblue;
}
.right{
  height: calc(100vh - 60px);
  width: 40%;
  background-color: red;
}

.myNotification{
  height: 60%;
  background-color: lightgreen;
}

.rightBottom{
  display: flex;
  height: 40%;
}
.outTime{
  width: 50%;
  background-color: pink;
}
.extend{
  width: 50%;
  background-color: lightcoral;
}
</style>

