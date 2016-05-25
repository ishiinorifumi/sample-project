package jp.co.disney.spplogin.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.helper.RandomHelper;
import jp.co.disney.spplogin.web.EmptyMailController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EmptyMailControllerTest {
	
	@Value("${spplogin.emptymail.account-prefix}")
	private String accountPrefix;
	
	@Value("${spplogin.emptymail.account-separator}")
	private String accountSeparator;
	
	
	@Value("${spplogin.emptymail.session-coop-key.prefix}")
	private String coopKeyPrefix;
	
	@Value("${spplogin.emptymail.domain}")
	private String emptyMailDomain;
	
	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
    @Autowired
    private ObjectMapper mapper;
    
	private MockMvc mockMvc;
	
	@Mock
	private RandomHelper randomHelper;
	
	@InjectMocks
	@Autowired
	private EmptyMailController controller;
	
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller).build();
    }
    
    @Test
    public void genToAddressメソッド() throws Exception {
    	Mockito.when(randomHelper.randomID()).thenReturn("abcdefg123456");
    	
    	Map<String, String> toAddress = new HashMap<>();
    	
    	toAddress.put("to_address", accountPrefix + accountSeparator + coopKeyPrefix + "abcdefg123456" + "@" + emptyMailDomain);
    	
    	this.mockMvc.perform(MockMvcRequestBuilders.get("/EmptyMail/genToAddress"))
    	.andExpect(status().isOk())
    	.andExpect(content().string(mapper.writeValueAsString(toAddress)));
    }
}
