package com.e2ee.api;

import com.e2ee.api.controller.dto.*;
import com.e2ee.api.repository.entities.Chat;
import com.e2ee.api.repository.entities.User;
import com.e2ee.api.service.ChatService;
import com.e2ee.api.service.MessageService;
import com.e2ee.api.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

// TODO: Fix this shit
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInit implements ApplicationRunner {

    private final UserService userService;
    private final MessageService messageService;
    private final ChatService chatService;

    private final ApplicationContext context;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var configs = context.getBeansOfType(AbstractSecurityWebSocketMessageBrokerConfigurer.class);
        configs.forEach((k, v) -> System.out.println(k + ": " + v.getClass()));
        if (args.containsOption("dev-default-data") || true) {
            createDefault();
        }
        if (args.containsOption("dev-generate-data")) {
            List<String> optionValues = args.getOptionValues("dev-generate-data");
            int messageCount = getIntArg(optionValues, 0).orElse(100);
            int chatCount = getIntArg(optionValues, 1).orElse(20);
            int userCount = getIntArg(optionValues, 2).orElse(5);
            generateData(messageCount, chatCount, userCount);
        }
    }

    private static final AtomicLong atomic = new AtomicLong(1L);

    @Scheduled(initialDelay = 10, fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() {
        messageService.sendMessage(new User(1L, null, null), MessageDto.create(new Chat(1L, false, null),
                "Hello"+atomic.getAndIncrement()));
    }

    public void createDefault() {

        User admin = userService.createUser(UserRegistrationDto.create("admin", "admin"));
        User user1 = userService.createUser(new UserRegistrationDto(new UserCredentialsDto("user1", "password1"),
                "user1", "one", "1@"));
        User user2 = userService.createUser(new UserRegistrationDto(new UserCredentialsDto("user2", "password2"),
                "user2", "two", "2@"));
        User user3 = userService.createUser(new UserRegistrationDto(new UserCredentialsDto("user3", "password3"),
                "user3", "three", "3@"));

        Chat chat1 = chatService.createGroupChat(admin, GroupChatDto.create("All", user1, user2, user3));
        messageService.sendMessage(admin, MessageDto.create(chat1, "Hello!!!"));
        messageService.sendMessage(user1, new MessageDto(chat1.getId(), "Hi"));
        messageService.sendMessage(user2, new MessageDto(chat1.getId(), "Bonjour"));
        messageService.sendMessage(user3, new MessageDto(chat1.getId(), "42"));

        Chat chat2 = chatService.createGroupChat(admin, new GroupChatDto("HW", List.of(
                admin.getId(), user1.getId())));
        messageService.sendMessage(admin, new MessageDto(chat2.getId(), "Foo"));
        messageService.sendMessage(user1, new MessageDto(chat2.getId(), "Bar"));
        messageService.sendMessage(admin, new MessageDto(chat2.getId(), "Buzz"));

        Chat chat3 = chatService.createGroupChat(admin, new GroupChatDto("a23", List.of(
                admin.getId(), user2.getId(), user3.getId())));
        messageService.sendMessage(admin, new MessageDto(chat3.getId(), "Log"));
        messageService.sendMessage(user2, new MessageDto(chat3.getId(), "0101010101010101001101010"));
        messageService.sendMessage(user3, new MessageDto(chat3.getId(), "Ok"));

        Chat chat4 = chatService.createPersonalChat(admin, new PersonalChatDto(user1.getId()));
        messageService.sendMessage(admin, new MessageDto(chat4.getId(), "1"));
        messageService.sendMessage(user1, new MessageDto(chat4.getId(), "2"));
        messageService.sendMessage(admin, new MessageDto(chat4.getId(), "3"));
        messageService.sendMessage(user1, new MessageDto(chat4.getId(), "4"));

        Chat chat5 = chatService.createPersonalChat(admin, PersonalChatDto.with(user2));
        messageService.sendMessage(admin, new MessageDto(chat5.getId(), "Hi"));
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
