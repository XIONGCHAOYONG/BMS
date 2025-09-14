import axios from 'axios'
import { ElMessage } from 'element-plus'


//创建axios实例
const request = axios.create({
  baseURL: `${import.meta.env.VITE_SERVER_URL}`,
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

//请求拦截器
request.interceptors.request.use(
  config => {
    return config
  },
  
  error => {
    return Promise.reject(error)
  }
)


//响应拦截器
request.interceptors.response.use(

   response => {
    return response
  },

  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未登录')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.msg || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)
//请求拦截器
request.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    // 添加token
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.token = `${token}`;
    }
    return config;
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
)
export default request