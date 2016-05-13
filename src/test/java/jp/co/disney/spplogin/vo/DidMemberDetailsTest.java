package jp.co.disney.spplogin.vo;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class DidMemberDetailsTest {

	@Test
	public void convertToSppMemberDetailsメソッドのテスト() throws Exception {
		final DidMemberDetails did = new DidMemberDetails();
		did.setSwid("swid");
		did.setMemberName("memberName");
		did.setEmailAddress("emailAddress");
		did.setPassword("password");
		did.setGuardianEmailAddress("guardianEmailAddress");
		did.setGender("gender");
		did.setPrefectureCode("prefectureCode");
		did.setFirstNameKanji("firstNameKanji");
		did.setLastNameKanji("lastNameKanji");
		did.setNameKana("nameKana");
		did.setDateOfBirth("dateOfBirth");
		did.setPostalCode("postalCode");
		did.setAddress1("address1");
		did.setAddress2("address2");
		did.setPhoneNumber("phoneNumber");
		did.setAgeBand("ageBand");
		did.setEmailActivation(true);
		did.setLegalTou(true);
		did.setLegalPp(true);
		did.setFob(true);

		final SppMemberDetails spp  = did.convertToSppMemberDetails();
		
		assertThat(did.getSwid(), is(spp.getSwid()));
		assertThat(did.getMemberName(), is(spp.getMemberName()));
		assertThat(did.getEmailAddress(), is(spp.getEmailAddress()));
		assertThat(did.getPassword(), is(spp.getPassword()));
		assertThat(did.getGuardianEmailAddress(), is(spp.getGuardianEmailAddress()));
		assertThat(did.getGender(), is(spp.getGender()));
		assertThat(did.getPrefectureCode(), is(spp.getPrefectureCode()));
		assertThat(did.getFirstNameKanji(), is(spp.getFirstNameKanji()));
		assertThat(did.getLastNameKanji(), is(spp.getLastNameKanji()));
		assertThat(did.getNameKana(), is(spp.getNameKana()));
		assertThat(did.getDateOfBirth(), is(spp.getDateOfBirth()));
		assertThat(did.getPostalCode(), is(spp.getPostalCode()));
		assertThat(did.getAddress1(), is(spp.getAddress1()));
		assertThat(did.getAddress2(), is(spp.getAddress2()));
		assertThat(did.getPhoneNumber(), is(spp.getPhoneNumber()));
		assertThat(did.getAgeBand(), is(spp.getAgeBand()));

	}
}
