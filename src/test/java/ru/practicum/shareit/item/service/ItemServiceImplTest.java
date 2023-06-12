package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.utils.CommentMapper;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ItemServiceImpl.class})
@ExtendWith(SpringExtension.class)
@SuppressWarnings("unused")
class ItemServiceImplTest {
    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private UserRepository userRepository;

    /**
     * Method under test: {@link ItemServiceImpl#createItem(ItemRequestDto, long)}
     */
    @Test
    void testCreateItem() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        when(itemRepository.save(Mockito.any())).thenReturn(item);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ItemDto actualCreateItemResult = itemServiceImpl.createItem(new ItemRequestDto(), 1L);
        assertTrue(actualCreateItemResult.getAvailable());
        assertEquals(1L, actualCreateItemResult.getRequestId().longValue());
        assertNull(actualCreateItemResult.getNextBooking());
        assertNull(actualCreateItemResult.getComments());
        assertEquals(1L, actualCreateItemResult.getId());
        assertNull(actualCreateItemResult.getLastBooking());
        assertEquals("Name", actualCreateItemResult.getName());
        assertEquals("The characteristics of someone or something", actualCreateItemResult.getDescription());
        UserDto owner2 = actualCreateItemResult.getOwner();
        assertEquals("Name", owner2.getName());
        assertEquals(1L, owner2.getId());
        assertEquals("jane.doe@example.org", owner2.getEmail());
        verify(itemRepository).save(Mockito.any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#createItem(ItemRequestDto, long)}
     */
    @Test
    void testCreateItem2() {
        when(itemRepository.save(Mockito.any())).thenThrow(new EntityNotValidException("Entity", "Field"));

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        assertThrows(EntityNotValidException.class, () -> itemServiceImpl.createItem(new ItemRequestDto(), 1L));
        verify(itemRepository).save(Mockito.any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#readItem(long, long)}
     */
    @Test
    void testReadItem() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(bookingRepository.findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItemIn(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        ItemDto actualReadItemResult = itemServiceImpl.readItem(1L, 1L);
        assertTrue(actualReadItemResult.getAvailable());
        assertEquals(1L, actualReadItemResult.getRequestId().longValue());
        assertNull(actualReadItemResult.getNextBooking());
        assertTrue(actualReadItemResult.getComments().isEmpty());
        assertEquals(1L, actualReadItemResult.getId());
        assertNull(actualReadItemResult.getLastBooking());
        assertEquals("Name", actualReadItemResult.getName());
        assertEquals("The characteristics of someone or something", actualReadItemResult.getDescription());
        UserDto owner2 = actualReadItemResult.getOwner();
        assertEquals("Name", owner2.getName());
        assertEquals(1L, owner2.getId());
        assertEquals("jane.doe@example.org", owner2.getEmail());
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(bookingRepository).findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(commentRepository).findByItemIn(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#readItem(long, long)}
     */
    @Test
    void testReadItem2() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(bookingRepository.findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItemIn(Mockito.any(), Mockito.any()))
                .thenThrow(new EntityNotValidException("start", "start"));
        assertThrows(EntityNotValidException.class, () -> itemServiceImpl.readItem(1L, 1L));
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(bookingRepository).findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(commentRepository).findByItemIn(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#updateItem(long, ItemDto, long)}
     */
    @Test
    void testUpdateItem() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);

        User owner2 = new User();
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Name");

        User requester2 = new User();
        requester2.setEmail("jane.doe@example.org");
        requester2.setId(1L);
        requester2.setName("Name");

        Request request2 = new Request();
        request2.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request2.setDescription("The characteristics of someone or something");
        request2.setId(1L);
        request2.setRequester(requester2);

        Item item2 = new Item();
        item2.setAvailable(true);
        item2.setDescription("The characteristics of someone or something");
        item2.setId(1L);
        item2.setName("Name");
        item2.setOwner(owner2);
        item2.setRequest(request2);
        when(itemRepository.save(Mockito.any())).thenReturn(item2);
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        ItemDto actualUpdateItemResult = itemServiceImpl.updateItem(1L, new ItemDto(), 1L);
        assertTrue(actualUpdateItemResult.getAvailable());
        assertEquals(1L, actualUpdateItemResult.getRequestId().longValue());
        assertNull(actualUpdateItemResult.getNextBooking());
        assertNull(actualUpdateItemResult.getComments());
        assertEquals(1L, actualUpdateItemResult.getId());
        assertNull(actualUpdateItemResult.getLastBooking());
        assertEquals("Name", actualUpdateItemResult.getName());
        assertEquals("The characteristics of someone or something", actualUpdateItemResult.getDescription());
        UserDto owner3 = actualUpdateItemResult.getOwner();
        assertEquals("Name", owner3.getName());
        assertEquals(1L, owner3.getId());
        assertEquals("jane.doe@example.org", owner3.getEmail());
        verify(itemRepository).save(Mockito.any());
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#updateItem(long, ItemDto, long)}
     */
    @Test
    void testUpdateItem2() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        when(itemRepository.save(Mockito.any())).thenThrow(new EntityNotValidException("Entity", "Field"));
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        assertThrows(EntityNotValidException.class, () -> itemServiceImpl.updateItem(1L, new ItemDto(), 1L));
        verify(itemRepository).save(Mockito.any());
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#deleteItem(long, long)}
     */
    @Test
    void testDeleteItem() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        doNothing().when(itemRepository).deleteById(Mockito.<Long>any());
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        itemServiceImpl.deleteItem(1L, 1L);
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(itemRepository).deleteById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#deleteItem(long, long)}
     */
    @Test
    void testDeleteItem2() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        doThrow(new EntityNotValidException("Entity", "Field")).when(itemRepository).deleteById(Mockito.<Long>any());
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        assertThrows(EntityNotValidException.class, () -> itemServiceImpl.deleteItem(1L, 1L));
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(itemRepository).deleteById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#getAllItems(long)}
     */
    @Test
    void testGetAllItems() {
        when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong())).thenReturn(new ArrayList<>());

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(bookingRepository.findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItemIn(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.getAllItems(1L).isEmpty());
        verify(itemRepository).findByOwnerIdOrderByIdAsc(anyLong());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(bookingRepository).findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(commentRepository).findByItemIn(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#getAllItems(long)}
     */
    @Test
    void testGetAllItems2() {
        when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong())).thenReturn(new ArrayList<>());

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(bookingRepository.findByItemInAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(new ArrayList<>());
        when(commentRepository.findByItemIn(Mockito.any(), Mockito.any()))
                .thenThrow(new EntityNotValidException("created", "created"));
        assertThrows(EntityNotValidException.class, () -> itemServiceImpl.getAllItems(1L));
        verify(itemRepository).findByOwnerIdOrderByIdAsc(anyLong());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(commentRepository).findByItemIn(Mockito.any(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#getAvailableItemsByText(String, long, PageRequest)}
     */
    @Test
    void testGetAvailableItemsByText() {
        when(itemRepository.findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCaseAndAvailableOrderByIdDesc(
                Mockito.any(), anyBoolean(), Mockito.any())).thenReturn(new ArrayList<>());
        assertTrue(itemServiceImpl.getAvailableItemsByText("Text", 1L, null).isEmpty());
        verify(itemRepository).findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCaseAndAvailableOrderByIdDesc(
                Mockito.any(), anyBoolean(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#getAvailableItemsByText(String, long, PageRequest)}
     */
    @Test
    void testGetAvailableItemsByText2() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemRepository.findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCaseAndAvailableOrderByIdDesc(
                Mockito.any(), anyBoolean(), Mockito.any())).thenReturn(itemList);
        assertEquals(1, itemServiceImpl.getAvailableItemsByText("Text", 1L, null).size());
        verify(itemRepository).findByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCaseAndAvailableOrderByIdDesc(
                Mockito.any(), anyBoolean(), Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#addComment(Long, Long, CommentRequestDto)}
     */
    @Test
    void testAddComment() {
        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);
        Optional<Item> ofResult = Optional.of(item);
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndLessThanAndStatus(Mockito.<Long>any(), Mockito.<Long>any(),
                Mockito.any(), Mockito.any())).thenReturn(true);

        User author = new User();
        author.setEmail("jane.doe@example.org");
        author.setId(1L);
        author.setName("Name");

        User owner2 = new User();
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Name");

        User requester2 = new User();
        requester2.setEmail("jane.doe@example.org");
        requester2.setId(1L);
        requester2.setName("Name");

        Request request2 = new Request();
        request2.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request2.setDescription("The characteristics of someone or something");
        request2.setId(1L);
        request2.setRequester(requester2);

        Item item2 = new Item();
        item2.setAvailable(true);
        item2.setDescription("The characteristics of someone or something");
        item2.setId(1L);
        item2.setName("Name");
        item2.setOwner(owner2);
        item2.setRequest(request2);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        comment.setId(1);
        comment.setItem(item2);
        comment.setText("Text");
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        User author2 = new User();
        author2.setEmail("jane.doe@example.org");
        author2.setId(1L);
        author2.setName("Name");

        User owner3 = new User();
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setName("Name");

        User requester3 = new User();
        requester3.setEmail("jane.doe@example.org");
        requester3.setId(1L);
        requester3.setName("Name");

        Request request3 = new Request();
        request3.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request3.setDescription("The characteristics of someone or something");
        request3.setId(1L);
        request3.setRequester(requester3);

        Item item3 = new Item();
        item3.setAvailable(true);
        item3.setDescription("The characteristics of someone or something");
        item3.setId(1L);
        item3.setName("Name");
        item3.setOwner(owner3);
        item3.setRequest(request3);

        Comment comment2 = new Comment();
        comment2.setAuthor(author2);
        comment2.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        comment2.setId(1);
        comment2.setItem(item3);
        comment2.setText("Text");
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        when(commentMapper.toResponseDto(Mockito.any(), Mockito.any())).thenReturn(commentResponseDto);
        when(commentMapper.toComment(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(comment2);
        assertSame(commentResponseDto, itemServiceImpl.addComment(1L, 1L, new CommentRequestDto("Text")));
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(bookingRepository).existsByItemIdAndBookerIdAndEndLessThanAndStatus(Mockito.<Long>any(), Mockito.<Long>any(),
                Mockito.any(), Mockito.any());
        verify(commentRepository).save(Mockito.any());
        verify(commentMapper).toResponseDto(Mockito.any(), Mockito.any());
        verify(commentMapper).toComment(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
    }

    /**
     * Method under test: {@link ItemServiceImpl#addComment(Long, Long, CommentRequestDto)}
     */
    @Test
    void testAddComment2() {
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(Optional.empty());

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(1L);
        user.setName("Name");
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndLessThanAndStatus(Mockito.<Long>any(), Mockito.<Long>any(),
                Mockito.any(), Mockito.any())).thenReturn(true);

        User author = new User();
        author.setEmail("jane.doe@example.org");
        author.setId(1L);
        author.setName("Name");

        User owner = new User();
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setName("Name");

        User requester = new User();
        requester.setEmail("jane.doe@example.org");
        requester.setId(1L);
        requester.setName("Name");

        Request request = new Request();
        request.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setRequester(requester);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("The characteristics of someone or something");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        comment.setId(1);
        comment.setItem(item);
        comment.setText("Text");
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        User author2 = new User();
        author2.setEmail("jane.doe@example.org");
        author2.setId(1L);
        author2.setName("Name");

        User owner2 = new User();
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setName("Name");

        User requester2 = new User();
        requester2.setEmail("jane.doe@example.org");
        requester2.setId(1L);
        requester2.setName("Name");

        Request request2 = new Request();
        request2.setCreatedTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        request2.setDescription("The characteristics of someone or something");
        request2.setId(1L);
        request2.setRequester(requester2);

        Item item2 = new Item();
        item2.setAvailable(true);
        item2.setDescription("The characteristics of someone or something");
        item2.setId(1L);
        item2.setName("Name");
        item2.setOwner(owner2);
        item2.setRequest(request2);

        Comment comment2 = new Comment();
        comment2.setAuthor(author2);
        comment2.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        comment2.setId(1);
        comment2.setItem(item2);
        comment2.setText("Text");
        when(commentMapper.toResponseDto(Mockito.any(), Mockito.any()))
                .thenReturn(new CommentResponseDto());
        when(commentMapper.toComment(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(comment2);
        assertThrows(EntityNotFoundException.class,
                () -> itemServiceImpl.addComment(1L, 1L, new CommentRequestDto("Text")));
        verify(itemRepository).findById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }
}
