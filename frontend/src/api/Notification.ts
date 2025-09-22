import request from "@/utils/request"

export interface NotificationVO {
    noticeId: number;
    days: number;
    type: number;
    title: string;
    bookId: number;
}

export const getAllNotifications=()=>{
    return request({
        url: `/user/notification/getAllNotification`,
        method: 'GET',
    })
}   
