import request from "@/utils/request";

export interface ChatMessage {
    messageId: number;
    userId: number | null;
    content: string;
    role: "user" | "ai";  // 对应枚举 Role
    createTime: string;  
}

export const getChatMessage = () => {
    return request({
        url: '/chatMessage/getChatMessage',
        method: 'GET',
    });
};

export const addChatMessage = (chatMessage: ChatMessage) => {
    return request({
        url: '/chatMessage/addChatMessage',
        method: 'PUT',
        data: chatMessage,
    });
};

// AI流式聊天接口
export const streamChat = (message: string) => {
    return fetch(`/api/ai/chat?message=${encodeURIComponent(message)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'text/event-stream',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
        },
    });
};


