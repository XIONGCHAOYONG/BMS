import { defineStore } from "pinia";
import { computed, ref } from "vue";
import type { User } from "@/api/User";

export const useUserStore = defineStore("user", () => {
  // state
  const token = ref<string | null>(localStorage.getItem("token"));
  const user = ref<User | null>(
    localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user") as string)
      : null
  );

  // getters
  const getToken = ()=>{
    return token.value;
  }
  const getUser =()=>{
    return user.value;

  }

  // actions
  const setToken = (newToken: string) => {
    token.value = newToken;
    localStorage.setItem("token", newToken);
  };

  const setUser = (newUser: User) => {
    user.value = newUser;
    localStorage.setItem("user", JSON.stringify(newUser));
  };

  const logout = () => {
    token.value = null;
    user.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  };

  return {
    token,
    user,
    getToken,
    getUser,
    setToken,
    setUser,
    logout,
  };
});
