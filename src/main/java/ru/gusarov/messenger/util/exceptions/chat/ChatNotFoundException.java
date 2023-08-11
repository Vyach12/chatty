<<<<<<<< HEAD:chat-support/src/main/java/com/chatty/chatsupport/util/exceptions/message/MessageNotFoundException.java
package com.chatty.chatsupport.util.exceptions.message;
========
package ru.gusarov.messenger.util.exceptions.chat;
>>>>>>>> origin/mongoDB:src/main/java/ru/gusarov/messenger/util/exceptions/chat/ChatNotFoundException.java

import lombok.Builder;
import lombok.Getter;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.BaseException;

import java.time.LocalDateTime;

@Getter
public class ChatNotFoundException extends BaseException {
    @Builder
    public ChatNotFoundException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
