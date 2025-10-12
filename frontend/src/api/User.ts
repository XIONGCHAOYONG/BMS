import request from "@/utils/request"

export interface User{
    userId: number,
    username:string,
    account:string,
    password:string,
    realName:string,
    phone:string,
    address:string,
    avatar:string,
    role:number,
    status:number,
    createTime:number[],
    points:number,
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

export interface UserUpdateDTO{
    username?:string,
    realName?:string,
    phone?:string,
    address?:string,
    avatar?:string,
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

export const getUserById=(userId:number)=>{
    return request({
    url: `/user/user/getUserById`,
    method: 'GET',
    params: { userId }
  })
}

export const updateUser=(UserUpdateDTO:UserUpdateDTO)=>{
    return request({
    url: `/user/user/updateUser`,
    method: 'POST',
    data: UserUpdateDTO
  })
}
