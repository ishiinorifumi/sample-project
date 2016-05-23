package jp.co.disney.spplogin.helper;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;

import jp.co.disney.spplogin.Application;
import jp.co.disney.spplogin.exception.ApplicationErrors;
import jp.co.disney.spplogin.exception.ApplicationException;
import jp.co.disney.spplogin.exception.SppMemberRegisterException;
import jp.co.disney.spplogin.service.CoreWebApiService;

@RunWith(Theories.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EmailAddressValidatorTest {
	
	@Rule
    public ExpectedException expectedException = ExpectedException.none();

	@Rule
    public final MockitoRule rule = MockitoJUnit.rule();
	
	@Mock
	private CoreWebApiService coreWebApiService;
	
	@InjectMocks
	@Autowired
	private EmailAddressValidator emailAddressValidator;
	
    @Before
    public void before() throws Exception {
    	new TestContextManager(this.getClass()).prepareTestInstance(this);
    	MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void メールアドレスフォーマット不正() throws Exception {
    	final String testAddress = "user.@docomo.ne.jp.com";
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(MessageFormat.format(ApplicationErrors.INVALID_FORMAT_MAIL_ADDRESS.getMessage(), testAddress));
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "010931");
        
        when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString()))
        												.thenThrow(new SppMemberRegisterException(error));
        
        emailAddressValidator.validate(testAddress);
    }
    
    @Test
    public void メールアドレス重複() throws Exception {
    	final String testAddress = "user@docomo.ne.jp.com";
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(MessageFormat.format(ApplicationErrors.DUPLICATE_MAIL_ADDRESS.getMessage(), testAddress));
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "010776");
        
        when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString()))
        												.thenThrow(new SppMemberRegisterException(error));
        
        emailAddressValidator.validate(testAddress);
    }
    
    @Test
    public void その他の想定外エラー() throws Exception {
    	final String testAddress = "user@docomo.ne.jp.com";
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("想定外のエラー");
        
        Map<String, String> error = new HashMap<>();
        error.put("code", "999999");
        error.put("spp_message", "想定外のエラー");
        
        when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString()))
        .thenThrow(new SppMemberRegisterException(error));
        
        emailAddressValidator.validate(testAddress);
    }
    
    @Test
    public void 許容ドメインでない() throws Exception {
    	final String testAddress = "user@docomo.ne.jp.com";
    	
    	expectedException.expect(ApplicationException.class);
    	expectedException.expectMessage(MessageFormat.format(ApplicationErrors.INVALID_DOMAIN_MAIL_ADDRESS.getMessage(), testAddress));
        
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenReturn(null);
    	
    	emailAddressValidator.validate(testAddress);
    }
    
    @Theory
    public void 許容ドメイン(String testAddress) throws Exception {
    	when(coreWebApiService.registerSppMember(anyObject(), anyBoolean(), anyBoolean(), anyString())).thenReturn(null);
    	
    	emailAddressValidator.validate(testAddress);
    }
    
    /**
     * 許容ドメインアドレスのテストデータ
     * @return
     */
    @DataPoints
    public static String[] getFixture() {
    	return new String[] {
    			"user.18dklahcosaf@docomo.ne.jp",
    			"user.18dklahcosaf@disneymobile.ne.jp",
    			"user.18dklahcosaf@softbank.ne.jp",
    			"user.18dklahcosaf@i.softbank.jp",
    			"user.18dklahcosaf@d.vodafone.ne.jp",
    			"user.18dklahcosaf@h.vodafone.ne.jp",
    			"user.18dklahcosaf@t.vodafone.ne.jp",
    			"user.18dklahcosaf@c.vodafone.ne.jp",
    			"user.18dklahcosaf@r.vodafone.ne.jp",
    			"user.18dklahcosaf@k.vodafone.ne.jp",
    			"user.18dklahcosaf@n.vodafone.ne.jp",
    			"user.18dklahcosaf@s.vodafone.ne.jp",
    			"user.18dklahcosaf@q.vodafone.ne.jp",
    			"user.18dklahcosaf@ezweb.ne.jp",
    			"user.18dklahcosaf@disney.ne.jp",
    			"user.18dklahcosaf@gmail.com"
    	};
    } 
    
    
}
