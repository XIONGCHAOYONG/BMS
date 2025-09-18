import request from "@/utils/request";

export interface Book {
    bookId: number,
    title: string,
    author: string,
    isbn: string,
    categoryId: number,
    publisher: string,
    publishDate: string,
    totalStock: number,
    availableStock: number,
    summary: string,
    totalLend: number,
    status: number,
    createTime: string,  
}

export const  getTop10 = () => {
    return request({
        url: `/user/book/top10`,
        method: 'GET',
    })
}

export const getTop10ByCategory = (categoryId: number) => {
    return request({
        url: `/user/book/top10ByCategory/${categoryId}`,
        method: 'GET',
    })
}

export const getBookById = (bookId: number) => {
    return request({
        url: `/user/book/getBookById/${bookId}`,
        method: 'GET',
    })
}
export const getNewBooks = () => {
    return request({
        url: `/user/book/getNewBooks`,
        method: 'GET',
    })
}

export const searchBooks = (keyword: string) => {
    return request({
        url: `/user/book/searchBooks`,
        method: 'GET',
        params: {
            keyword
        }
    })
}