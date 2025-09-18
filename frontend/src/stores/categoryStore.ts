import { defineStore } from "pinia";
import { ref } from "vue";
import type { Category } from "@/api/Category";

export const useCategoryStore = defineStore("category", () => {
    const categories = ref<Category[]>([]);

    function setCategories(newCategories: Category[]) {
        categories.value = newCategories;
    }
    function getCategories() {
        return categories.value;
    }

    return {
        categories,
        setCategories,
        getCategories
    };
});