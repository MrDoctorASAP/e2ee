package com.e2ee.api.service;

import com.e2ee.api.controller.BatchController;
import com.e2ee.api.controller.dto.UnseenChatDto;
import com.e2ee.api.controller.dto.UserDto;
import com.e2ee.api.repository.*;
import com.e2ee.api.repository.batch.BatchRepository;
import com.e2ee.api.repository.batch.IBatchChat;
import com.e2ee.api.repository.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BatchService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final GroupChatInfoRepository groupInfoRepository;

    private final ChatService chatService;
    private final UserProfileService profileService;
    private final UnseenMessagesService unseenService;

    private final BatchRepository batchRepository;

    public List<BatchController.BatchMessage> getMessages(User user, Long chatId) {
        Long userId = user.getId();
        if (!chatRepository.existsById(chatId)) {
            throw new RuntimeException("ChatNotFound");
        }
        if (!memberRepository.existsByChatIdAndUserId(chatId, userId)) {
            throw new RuntimeException("NotChatMember");
        }
        List<Message> messages = messageRepository.findAllByChatId(chatId);
        List<Long> userIds = messages.stream().map(Message::getUserId).distinct().toList();
        List<UserDto> userProfiles = profileService.getUserProfiles(userIds);
        return messages.stream()
                .map(message -> new BatchController.BatchMessage(userProfiles.stream()
                        .filter(profile -> profile.getUserId().equals(message.getUserId()))
                        .findFirst()
                        .get(), message))
                .toList();
    }

    // method returns chats
    public List<BatchController.BatchChat> getChats(User user) {
                List<IBatchChat> batchChats = batchRepository.findAllByUserId(user.getId());
        return batchChats.stream()
                .map(chat -> new BatchController.BatchChat(
                        new Chat(chat.getChatId(), chat.getPersonal(), null),
                        new GroupChatInfo(null, chat.getChatId(), chat.getGroupName()),
                        new UserDto(chat.getPersonalId(), chat.getPersonalUsername(), chat.getPersonalFirstName(), chat.getPersonalLastName()),
                        new UnseenChatDto(chat.getChatId(), chat.getUnseen()),
                        new UserDto(chat.getSenderId(), chat.getSenderUsername(), chat.getSenderFirstName(), chat.getSenderLastName()),
                        new Message(chat.getMessageId(), chat.getChatId(), chat.getSenderId(), chat.getMessageDate(), chat.getMessageText())
                        ))
                .collect(Collectors.toList());
//        Map<Long, BatchController.BatchChat> batchChats = memberRepository.findAllByUserId(user.getId())
//                .stream()
//                .collect(Collectors.toMap(ChatMember::getChatId, member -> new BatchController.BatchChat()));
//        Set<Long> ids = batchChats.keySet();
//        chatRepository.findAllById(ids)
//                .forEach(chat -> batchChats.get(chat.getId()).setChat(chat));
//        groupInfoRepository.findAllById(ids)
//                .stream()
//                .filter(Objects::nonNull)
//                .forEach(info -> batchChats.get(info.getChatId()).setGroup(info));
////        List<LastMessageDto> lastMessages = chatService.getLastMessages(user, 0, Integer.MAX_VALUE);
////        lastMessages.forEach(lastMessage -> batchChats.get(lastMessage.getChatId())
////                .setLastMessage(lastMessage.getMessage()));
////        profileService.getUserProfiles(lastMessages.stream()
////                        .map(LastMessageDto::getMessage)
////                        .map(Message::getUserId)
////                        .distinct().toList())
////                .forEach(userDto -> lastMessages.stream()
////                        .filter(lastMessage -> lastMessage.getMessage().getUserId().equals(userDto.getUserId()))
////                        .forEach(lastMessage -> batchChats.get(lastMessage.getChatId()).setSender(userDto)));
//        unseenService.getUnseenChats(user)
//                .forEach(unseen -> batchChats.get(unseen.getChatId()).setUnseen(unseen));
//        batchChats.forEach((chatId, batchChat) -> {
//            if (batchChat.getUnseen() == null) {
//                batchChat.setUnseen(new UnseenChatDto(chatId, 0L));
//            }
//            if (batchChat.getChat().isPersonal()) {
//                ChatMember chatMember = memberRepository.findAllByChatId(chatId)
//                        .stream()
//                        .filter(member -> !Objects.equals(member.getUserId(), user.getId()))
//                        .findFirst().get();
//                batchChat.setPersonal(profileService.getUserProfiles(List.of(chatMember.getUserId())).get(0));
//            }
//        });
//        return batchChats.values().stream().toList();
    }
}
