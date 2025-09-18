import { defineStore } from "pinia";
import { ref } from "vue";
import type { Book } from "@/api/Book";

export const useBookStore = defineStore("book", () => {
  // 定义为以数字为键、Book为值的对象
  const bookMap = ref<Record<number, Book>>({});
  const Top10Books = ref<Book[]>([]);
  const NewBooks = ref<Book[]>([]);

  const setTop10Books = (list: Book[]) => {
    Top10Books.value = list;
  };
  const getTop10Books = () => {
    return Top10Books.value;
  };

  const setNewBooks = (list: Book[]) => {
    NewBooks.value = list;
  };
  const getNewBooks = () => {
    return NewBooks.value;
  };

  // 从数组构建映射
  function setBookMap(list: Book[]) {
    bookMap.value = list.reduce((map, book) => {
      map[book.bookId] = book;
      return map;
    }, {} as Record<number, Book>);
  }

  // 获取整个映射
  function getBookMap() {
    return bookMap.value;
  }

  // 根据ID获取书籍
  function getBookById(id: number) {
    return bookMap.value[id];
  }

  return {
    bookMap,
    setBookMap,
    getBookMap,
    getBookById,
    Top10Books,
    setTop10Books,
    getTop10Books,
    NewBooks,
    setNewBooks,
    getNewBooks,
  };
});
