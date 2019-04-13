package nl.surfnet.demo;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.AccessTokenInfo;
import org.wso2.carbon.apimgt.api.model.AccessTokenRequest;
import org.wso2.carbon.apimgt.api.model.OAuthAppRequest;
import org.wso2.carbon.apimgt.api.model.OAuthApplicationInfo;

public class MyTestClass {

	public static void main(String[] args) throws APIManagementException {
		SurfOAuthClient2 surfOAuthClient2 = new SurfOAuthClient2();
		
		/*OAuthAppRequest oAuthAppRequest = new OAuthAppRequest();
		
		OAuthApplicationInfo oAuthApplicationInfo = new OAuthApplicationInfo();
		oAuthApplicationInfo.setClientName("MyTestClient_10");
		
		oAuthAppRequest.setOAuthApplicationInfo(oAuthApplicationInfo);
		
		OAuthApplicationInfo resp = surfOAuthClient2.createApplication(oAuthAppRequest);
		
		
		OAuthApplicationInfo resp1 =surfOAuthClient2.retrieveApplication(resp.getClientId());
		
		System.out.println("Hi.." + resp);*/
		
		AccessTokenRequest tokenRequest = new AccessTokenRequest();
		tokenRequest.setClientId("abc-fin-tect");
		tokenRequest.setClientSecret("3837c91e-2c4c-4f44-966c-fa8553a8e2c3");
		
		AccessTokenInfo accessTokenInfo = surfOAuthClient2.getNewApplicationAccessToken(tokenRequest);
		
		AccessTokenInfo accessTokenInfo1 = surfOAuthClient2.getTokenMetaData(accessTokenInfo.getAccessToken());
		
		System.out.println("Hi.." + accessTokenInfo1);

	}

}
