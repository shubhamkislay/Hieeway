package com.hieeway.hieeway.Model;

public class ChatMessageCompound {

    private ChatMessage ChatMessageSenderCopy;
    private ChatMessage chatMessageReceiverCopy;

    public ChatMessageCompound(ChatMessage chatMessageSenderCopy, ChatMessage chatMessageReceiverCopy) {
        ChatMessageSenderCopy = chatMessageSenderCopy;
        this.chatMessageReceiverCopy = chatMessageReceiverCopy;
    }

    public ChatMessageCompound() {
    }

    public ChatMessage getChatMessageSenderCopy() {
        return ChatMessageSenderCopy;
    }

    public void setChatMessageSenderCopy(ChatMessage chatMessageSenderCopy) {
        ChatMessageSenderCopy = chatMessageSenderCopy;
    }

    public ChatMessage getChatMessageReceiverCopy() {
        return chatMessageReceiverCopy;
    }

    public void setChatMessageReceiverCopy(ChatMessage chatMessageReceiverCopy) {
        this.chatMessageReceiverCopy = chatMessageReceiverCopy;
    }
}
