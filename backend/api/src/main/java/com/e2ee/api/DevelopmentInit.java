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
        boolean defaultData = args.containsOption("dev-default-data");
        boolean charactersData = args.containsOption("dev-characters-data") || true;
        boolean generateData = args.containsOption("dev-generate-data");
        if (charactersData && !defaultData) {
            createCharacters();
        }
        if (defaultData || (messageSender && !generateData && !charactersData)) {
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

    public void createCharacters() {

        User admin = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto("admin", "admin"), "Admin", "42", ""));
        User user1 = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto("user1", "password1"), "Yunston", "Cherchil", ""));
        User user2 = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto("user2", "password5"), "Oxxxy", "miron", ""));
        User user3 = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto("user3", "password3"), "Bear", "Polar", ""));
        User user4 = userService.createUser(new UserRegistrationDto(
                new UserCredentialsDto("user4", "password4"), "Shtirlitz", "", ""));

        Chat chat1 = chatService.createPersonalChat(admin, with(user4));
        messageService.sendMessage(admin, message(chat1, "Вместо привычного голубя в окно залетела голая сова."));
        messageService.sendMessage(admin, message(chat1, "\"Голосовуха\" - подумал Штирлиц."));
        messageService.sendMessage(user4, message(chat1, "Штирлиц пришел на встречу со связным в знакомый бар и заказал 100 грамм водки."));
        messageService.sendMessage(user4, message(chat1, "- Водка у нас закончилась еще два дня назад, - извинился бармен."));
        messageService.sendMessage(user4, message(chat1, "- Ну, тогда 100 грамм коньячку."));
        messageService.sendMessage(user4, message(chat1, "- Коньячок у нас закончился вчера, - огорченно сказал бармен."));
        messageService.sendMessage(user4, message(chat1, "- Ну, а пиво-то есть?"));
        messageService.sendMessage(user4, message(chat1, "- Увы, закончилось сегодня..."));
        messageService.sendMessage(user4, message(chat1, "\"Значит, связной уже здесь.\" - подумал Штирлиц"));

        Chat chat2 = chatService.createPersonalChat(admin, with(user1));
        messageService.sendMessage(user1, message(chat2, "Underidoderidoderidoderido"));
        messageService.sendMessage(admin, message(chat2, "London is the capital of Great Britain!"));
        messageService.sendMessage(user1, message(chat2, "Дело не в том, что жить с деньгами очень уж хорошо.\nА в том что без денег не хватает денег."));
        messageService.sendMessage(admin, message(chat2, "Хорошо сказал"));

        Chat chat3 = chatService.createPersonalChat(admin, with(user3));
        messageService.sendMessage(user3, message(chat3, "Приходит как-то мужик к практологу:"));
        messageService.sendMessage(user3, message(chat3, "- Здавствуйте, Василий Иванович. Я к Вам на приём."));
        messageService.sendMessage(user3, message(chat3, "- Проходи на кушетку."));
        messageService.sendMessage(user3, message(chat3, "Ну мужик разделся и занял позу."));
        messageService.sendMessage(user3, message(chat3, "Практолог начал его остамривать."));
        messageService.sendMessage(user3, message(chat3, "- Ай, почему так больно? Это какой-то медицинский инструмен?"));
        messageService.sendMessage(user3, message(chat3, "- Это, Петр Иванович, нюанс."));
        messageService.sendMessage(admin, message(chat3, "\uD83D\uDE02"));

        Chat chat4 = chatService.createGroupChat(admin, groupChat("All", user1, user3, user4, user2));
        messageService.sendMessage(admin, message(chat4, "Hello!!!"));
        messageService.sendMessage(user1, message(chat4, "Hi"));
        messageService.sendMessage(user3, message(chat4, "42"));
        messageService.sendMessage(user4, message(chat4, "Гутен таг"));
        messageService.sendMessage(user2, message(chat4, "Гээнг гэнг"));

        Chat chat5 = chatService.createPersonalChat(admin, with(user2));
        messageService.sendMessage(user2, message(chat5, """
                Я не просто баламут, хам,
                Я свой собственный Плутарх:
                Это летопись, нужны разные флоу?
                У меня девять есть, зови меня Wu-Tang.
                ..."""));
        messageService.sendMessage(admin, message(chat5, "Опять ты за своё..."));
        messageService.sendMessage(admin, message(chat5, "Дед, прими таблетки."));
        messageService.sendMessage(user2, message(chat5, """
                Если тут кто-то курд, то он не Воннегут, дам
                Слово, что я не расист, но тут каждый второй орангутан.
                Всюду блуд, Болливуд, хлам.
                Это Е16, вперемежку нации Бангладеш, вьетнамцы,
                ..."""));

        Chat chat6 = chatService.createGroupChat(admin, groupChat("Англия", admin, user1, user2));
        messageService.sendMessage(admin, message(chat6, "\uD83D\uDE00"));
        messageService.sendMessage(user1, message(chat6, "\uD83D\uDE01\uD83D\uDE01\uD83D\uDE01\uD83D\uDE01"));
        messageService.sendMessage(user2, message(chat6, "\uD83D\uDE04\uD83D\uDE04"));
        messageService.sendMessage(admin, message(chat6, "\uD83D\uDDFC"));
        messageService.sendMessage(user1, message(chat6, "\uD83D\uDE02"));

        Chat chat7 = chatService.createGroupChat(admin, groupChat("Трио", admin, user3, user4));
        messageService.sendMessage(user4, message(chat7, "Медведь, прокатимся на вертолёте?"));
        messageService.sendMessage(user3, message(chat7, "Ну хоть ты то не начинай, Штирлиц."));
        messageService.sendMessage(user3, message(chat7, "И так все придумывают про нас шутки."));
        messageService.sendMessage(user4, message(chat7, "Да, и со мной самые смешные."));

        Chat chat8 = chatService.createGroupChat(user1, groupChat("Empty test", user2));
        Chat chat9 = chatService.createPersonalChat(user1, with(user2));

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
