package com.rhcsoft.spring.task.pool.taskpooldemo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventDoc;
import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventState;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventData;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventNotification;
import com.rhcsoft.spring.task.pool.taskpooldemo.model.EventUser;
import com.rhcsoft.spring.task.pool.taskpooldemo.service.EventDocService;

@SpringBootTest
class TaskPoolDemoApplicationTests {

	@Autowired
	private WebApplicationContext wac;

	@MockitoBean
	private EventDocService eventDocService;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private EventNotification event = new EventNotification() {
		{
			setEventId("123");
			setUser(new EventUser() {
				{
					setId("1");
					setName("John Doe");
				}
			});
			setData(new EventData() {
				{
					setField1("value1");
					setField2("value2");
				}
			});
		}
	};

	private EventDoc eventDoc = new EventDoc() {
		{
			setId("123");
			setUserId("1");
			setField1("value1");
			setField2("value2");
			setState(EventState.NEW);
			setReceivedAt(LocalDateTime.now());
			setStartedAt(null);
			setCompletedAt(null);
		}
	};

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

		Mockito.when(eventDocService.saveEvent(Mockito.any(EventNotification.class))).thenReturn(Optional.of(eventDoc));
		Mockito.doNothing().when(eventDocService).startProcessEvent(Mockito.anyString());
		Mockito.doNothing().when(eventDocService).completeProcessEvent(Mockito.anyString());
		Mockito.doNothing().when(eventDocService).failProcessEvent(Mockito.anyString());
		Mockito.when(eventDocService.getEvent(Mockito.anyString())).thenReturn(Optional.of(eventDoc));
	}

	@Test
	public void testPostSuccess() throws Exception {

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post("/api/data").content(objectMapper.writeValueAsString(event))
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventDoc.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(eventDoc.getUserId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.field1").value(eventDoc.getField1()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.field2").value(eventDoc.getField2()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.state").value(eventDoc.getState().name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.receivedAt").isString())
				.andExpect(MockMvcResultMatchers.jsonPath("$.startedAt").isEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.completedAt").isEmpty());
	}

	@Test
	public void testPostValidation() throws Exception {

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post("/api/data")
								.content(objectMapper.writeValueAsString(new EventNotification()))
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testPostError() throws Exception {

		Mockito.when(eventDocService.saveEvent(Mockito.any(EventNotification.class)))
				.thenReturn(Optional.ofNullable(null));

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post("/api/data").content(objectMapper.writeValueAsString(event))
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetSuccess() throws Exception {

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.get("/api/data/" + event.getEventId())
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(eventDoc.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(eventDoc.getUserId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.field1").value(eventDoc.getField1()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.field2").value(eventDoc.getField2()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.state").value(eventDoc.getState().name()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.receivedAt").isString())
				.andExpect(MockMvcResultMatchers.jsonPath("$.startedAt").isEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.completedAt").isEmpty());
	}

	@Test
	public void testGetError() throws Exception {

		Mockito.when(eventDocService.saveEvent(Mockito.any(EventNotification.class)))
				.thenReturn(Optional.ofNullable(null));

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.get("/api/data" + event.getEventId())
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testReprocess() throws Exception {

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.patch("/api/reprocess/" + event.getEventId())
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
