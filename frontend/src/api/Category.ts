import request from "@/utils/request";

export interface Category {
    categoryId: number,
    name: string,
}

export const getAllCategories = () => {
    return request({
        url: `/user/category/getAll`,
        method: 'GET',
    })
}