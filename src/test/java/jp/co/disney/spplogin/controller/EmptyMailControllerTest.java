package jp.co.disney.spplogin.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.web.EmptyMailController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EmptyMailControllerTest {
	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	private MockMvc mockMvc;
	
	@InjectMocks
	@Autowired
	private EmptyMailController controller;
	
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller).build();
    }
    
    @Test
    public void genToAddressメソッド() throws Exception {
    	this.mockMvc.perform(MockMvcRequestBuilders.get("/EmptyMail/genToAddress"))
    	.andExpect(status().isOk());
    }
}
