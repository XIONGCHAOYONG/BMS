import request from "@/utils/request";
export interface Lend {
    lendId?: number;
    userId: number;
    bookId: number;
    borrowDate: [number, number, number];
    dueDate: [number, number, number];
    returnDate?: [number, number, number];
    status: number; // 0:未还 1:已还 2:逾期 3:将逾期 4.续借
    renewCount: number;
}

export interface LendDTO {
    lendId?: number;
    bookId: number;
    days: number;
}

export interface ReLendDTO {
    bookId: number;
    lendId: number;
    days: number;
    dueDate: [number, number, number];
}

export interface LendVO {
    lend: Lend;
    title: string;  
    author: string;
}

export interface ReturnDTO {
    lendId: number;
    bookId: number;
}
export const getAllLend = () => {
    return request({
        url: `/user/lend/getAllLend`,
        method: 'GET',
    })
}
export const getCurrentLend = () => {
    return request({
        url: `/user/lend/getCurrentLend`,
        method: 'GET',
    })
}

export const lendBook = (lendDTO: LendDTO) => {
    return request({
        url: `/user/lend/lendBook`,
        method: 'POST',
        data: lendDTO
    })
}

export const renewBook = (reLendDTO: ReLendDTO) => {
    return request({
        url: `/user/lend/renewBook`,
        method: 'PUT',
        data: reLendDTO
    })
}

export const returnBook = (reLendDTO: ReturnDTO) => {
    return request({
        url: `/user/lend/returnBook`,
        method: 'PUT',
        data: reLendDTO
    })
}

export const getLendByBookId = (bookId: number) => {
    return request({
        url: `/user/lend/getLendByBookId/${bookId}`,
        method: 'GET',
    })
}
