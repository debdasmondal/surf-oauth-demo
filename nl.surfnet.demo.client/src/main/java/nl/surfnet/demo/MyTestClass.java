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
		
		/*AccessTokenRequest tokenRequest = new AccessTokenRequest();
		tokenRequest.setClientId("abc-fin-tect");
		tokenRequest.setClientSecret("3837c91e-2c4c-4f44-966c-fa8553a8e2c3");
		
		AccessTokenInfo accessTokenInfo = surfOAuthClient2.getNewApplicationAccessToken(tokenRequest);
		
		AccessTokenInfo accessTokenInfo1 = surfOAuthClient2.getTokenMetaData(accessTokenInfo.getAccessToken());*/
		AccessTokenInfo accessTokenInfo1 = surfOAuthClient2.getTokenMetaData("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJIUjZaWEVuZ1Q4Y2pPcUd6dHNQWUNIYTVHSTE4UlM3R1hCbl9HalFPek5ZIn0.eyJqdGkiOiI4OTE5M2JhNC05OTliLTQ0ODItODg4My1lMmUzODEwZDM3YmMiLCJleHAiOjE1NTU0MjI0NTAsIm5iZiI6MCwiaWF0IjoxNTU1NDIyMTUwLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvTXlGaXJzc3RSZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJkMjM2NTA1OS1jMzM5LTQyNDItOTI1MC02ODlhMTU5MjNkMzkiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJNeUFwcGxpY2F0aW9uXzEyX1NBTkRCT1giLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiJjZWEyNGEwOS0zODRhLTQwMjAtODI1YS00ZTY1NmI3NTBmZDEiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJjbGllbnRJZCI6Ik15QXBwbGljYXRpb25fMTJfU0FOREJPWCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SG9zdCI6IjE3Mi4xNy4wLjEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtbXlhcHBsaWNhdGlvbl8xMl9zYW5kYm94IiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4xNy4wLjEiLCJlbWFpbCI6InNlcnZpY2UtYWNjb3VudC1teWFwcGxpY2F0aW9uXzEyX3NhbmRib3hAcGxhY2Vob2xkZXIub3JnIn0.H39-E6zOL2UE5bmZ5s-Bwk2pHVPs5nn0UXwXCO9O9XHvFyQkFmBn7gAPjD6Oe4MOPgKQuIvyGXpwA862Ep6eURcYuRIgaX-jSzxavH1A2GUI1-x3tidizctls2swkyudatIqLl7d2w0TaMTDVkhA1aJ09sqnBy3bLC7LrEgJrfa8ruVwBmo9pwP058i5f3jDj6wssNgsZ33_4_zuRar95sjRB2SrVtveMhSMN8JWeRtuAAX2qHgewp4uYdApiYSJcShY-K3a8SD-7zlABlhjvnIm7uIMDGJYUqNI5ihQEfG_P64e0OpeQwV4XM2-p_f9KpMD2KuWDxceXwi-MSUvEg");
		
		System.out.println("Hi.." + accessTokenInfo1);

	}

}
