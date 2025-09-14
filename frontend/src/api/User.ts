import request from "@/utils/request"

export interface User{
    userId: number,
    username:string,
    account:string,
    password:string,
    realName:string,
    phone:string,
    adderss:string,
    avatar:string,
    role:number,
    status:number,
    createTime:string,
}

export interface UserLoginDTO{
    identifier:string,
    password:string,
}
export interface UserLoginVO{
    token:string,
    user:User,
}
export interface UserRegisterDTO{
    username:string,
    password:string,
    phone:string,
}

export const login=(UserLoginDTO:UserLoginDTO)=>{
    return request({
    url: `/user/user/login`,
    method: 'POST',
    data: UserLoginDTO
  })
}

export const register=(UserRegisterDTO:UserRegisterDTO)=>{
    return request({
    url: `/user/user/register`,
    method: 'POST',
    data: UserRegisterDTO
  })
}