<template>
    <footer class="footer" aria-label="网站底部">
        <div class="footer-content">
            
            <span class="footer-item">@花椒图书</span>
<span class="footer-item">
                <el-icon>
                    <Location />
                </el-icon> 江西省南昌市经济技术开发区双港东大街808号
            </span>
            <span class="footer-item">
                <el-icon>
                    <Message />
                </el-icon> 1846326845@qq.com
            </span>
        </div>
    </footer>
</template>

<script setup lang="ts">
import { Location, Message } from '@element-plus/icons-vue';
</script>

<style scoped>
.footer {
    background-color: white;
    color: grey;
    padding: 10px 20px;
    border-top: 1px solid #e0e0e0;
}

.footer-content {
    width: 100%;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    font-size: 15px;
    gap: 10px;
}

.footer-item {
    display: flex;
    align-items: center;
    gap: 6px;
    white-space: nowrap;
}

@media (max-width: 768px) {
    .footer-content {
        flex-direction: column;
        text-align: center;
        gap: 8px;
    }
}
</style>
