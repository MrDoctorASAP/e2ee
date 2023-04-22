package com.e2ee.api;

import com.e2ee.api.controller.dto.*;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.ChatService;
import com.e2ee.api.service.MessageService;
import com.e2ee.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.e2ee.api.controller.dto.GroupChatDto.groupChat;
import static com.e2ee.api.controller.dto.MessageDto.message;
import static com.e2ee.api.controller.dto.PersonalChatDto.with;
import static com.e2ee.api.controller.dto.UserRegistrationDto.sampleUser;


@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentInit implements ApplicationRunner {

    private final UserService userService;
    private final MessageService messageService;
    private final ChatService chatService;

    private final AtomicLong messageNumber = new AtomicLong(1L);
    private volatile boolean sentMessages = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean messageSender = args.containsOption("dev-message-sender");
        boolean defaultData = args.containsOption("dev-default-data") || true;
        boolean generateData = args.containsOption("dev-generate-data");
        if (defaultData || (messageSender && !generateData)) {
            createDefault();
        }
        if (generateData) {
            List<String> optionValues = args.getOptionValues("dev-generate-data");
            int messageCount = getIntArg(optionValues, 0).orElse(100);
            int chatCount = getIntArg(optionValues, 1).orElse(20);
            int userCount = getIntArg(optionValues, 2).orElse(5);
            generateData(messageCount, chatCount, userCount);
        }
        if (messageSender) {
            sentMessages = true;
        }
    }

    @Scheduled(initialDelay = 30, fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() {
        if (sentMessages) {
            messageService.sendMessage(new User(1L, null, null), message(new Chat(1L, false, null),
                    "Hello"+ messageNumber.incrementAndGet()));
        }
    }

    public void createDefault() {

        User admin = userService.createUser(sampleUser("admin", "admin"));
        User user1 = userService.createUser(sampleUser("user1", "password1"));
        User user2 = userService.createUser(sampleUser("user2", "password2"));
        User user3 = userService.createUser(sampleUser("user3", "password3"));

        Chat chat1 = chatService.createGroupChat(admin, groupChat("All", user1, user2, user3));
        messageService.sendMessage(admin, message(chat1, "Hello!!!"));
        messageService.sendMessage(user1, message(chat1, "Hi"));
        messageService.sendMessage(user2, message(chat1, "Bonjour"));
        messageService.sendMessage(user3, message(chat1, "42"));

        Chat chat2 = chatService.createGroupChat(admin, groupChat("Example", user1));
        messageService.sendMessage(admin, message(chat2, "Foo"));
        messageService.sendMessage(user1, message(chat2, "Bar"));
        messageService.sendMessage(admin, message(chat2, "Buzz"));

        Chat chat3 = chatService.createGroupChat(admin, groupChat("Test Chat", user2, user3));
        messageService.sendMessage(admin, message(chat3, "Log"));
        messageService.sendMessage(user2, message(chat3, "0101010101010101001101010"));
        messageService.sendMessage(user3, message(chat3, "Ok"));

        Chat chat4 = chatService.createPersonalChat(admin, with(user1));
        messageService.sendMessage(admin, message(chat4, "1"));
        messageService.sendMessage(user1, message(chat4, "2"));
        messageService.sendMessage(admin, message(chat4, "3"));
        messageService.sendMessage(user1, message(chat4, "4"));

        Chat chat5 = chatService.createPersonalChat(admin, with(user2));
        messageService.sendMessage(admin, message(chat5, "Hi"));

        Chat chat6 = chatService.createPersonalChat(admin, with(user3));
    }

    public void generateData(int messageCount, int chatCount, int userCount) {
        Random random = new Random();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            String username = "_user" + (i + 1);
            User user = userService.createUser(new UserRegistrationDto(new UserCredentialsDto(username, username),
                    username, username, username + "@mail.ru"));
            users.add(user);
        }
        Map<Chat, List<Long>> chats = new HashMap<>();
        for (int i = 0; i < chatCount; i++) {
            User creator = users.get(random.nextInt(users.size()));
            if (random.nextBoolean()) {
                List<Long> members = Stream.concat(Stream.of(creator.getId()),
                                random.ints(random.nextInt(1, 10), 1, userCount)
                                        .mapToObj(x -> (Long) (long) x))
                        .distinct()
                        .toList();
                Chat chat = chatService.createGroupChat(creator, new GroupChatDto("Group chat", members));
                chats.put(chat, members);
            } else {
                Long userId = (long) random.nextInt(userCount) + 1;
                if (userId.equals(creator.getId())) {
                    if (userId == 1L) {
                        userId = 2L;
                    } else {
                        userId = 1L;
                    }
                }
                Chat chat = chatService.createPersonalChat(creator, new PersonalChatDto(userId));
                chats.put(chat, List.of(creator.getId(), userId));
            }
        }
        List<Map.Entry<Chat, List<Long>>> entries = chats.entrySet().stream().toList();
        for (int i = 0; i < messageCount; i++) {
            Map.Entry<Chat, List<Long>> chatEntry = entries.get(random.nextInt(entries.size()));
            Long userId = chatEntry.getValue().get(random.nextInt(chatEntry.getValue().size()));
            Long chatId = chatEntry.getKey().getId();
            messageService.sendMessage(new User(userId, null, null), new MessageDto(chatId,
                    "FooBarBuzz" + i));
        }
    }

    private Optional<Integer> getIntArg(List<String> optionValues, int index) {
        return Optional.of(index)
                .filter(i -> optionValues.size() > i)
                .map(optionValues::get)
                .flatMap(this::tryParseInteger);
    }

    private Optional<Integer> tryParseInteger(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
