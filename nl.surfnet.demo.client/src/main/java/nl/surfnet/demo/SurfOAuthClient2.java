/*
 *
 *   Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package nl.surfnet.demo;

import org.apache.axiom.om.util.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.oltu.oauth2.common.OAuth;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.AccessTokenInfo;
import org.wso2.carbon.apimgt.api.model.AccessTokenRequest;
import org.wso2.carbon.apimgt.api.model.ApplicationConstants;
import org.wso2.carbon.apimgt.api.model.KeyManagerConfiguration;
import org.wso2.carbon.apimgt.api.model.OAuthAppRequest;
import org.wso2.carbon.apimgt.api.model.OAuthApplicationInfo;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.AbstractKeyManager;
import org.wso2.carbon.apimgt.impl.factory.KeyManagerHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides the implementation to use "Apis" {@link "https://github.com/OAuth-Apis/apis"} for managing
 * OAuth clients and Tokens needed by WSO2 API Manager.
 */
public class SurfOAuthClient2 extends AbstractKeyManager {

    private static final Log log = LogFactory.getLog(SurfOAuthClient2.class);

    // We need to maintain a mapping between Consumer Key and id. To get details of a specific client,
    // we need to call client registration endpoint using id.
    Map<String, String> nameIdMapping = new HashMap<String, String>();
    static Map<String, String> registrationAccessTokenMap = new HashMap<String, String>();
    static Map<String, String> clientIdSecretMap = new HashMap<String, String>();
    
    private KeyManagerConfiguration configuration;

    /**
     * {@code APIManagerComponent} calls this method, passing KeyManagerConfiguration as a {@code String}.
     *
     * @param configuration Configuration as a {@link org.wso2.carbon.apimgt.api.model.KeyManagerConfiguration}
     */
    @Override
    public void loadConfiguration(KeyManagerConfiguration configuration) throws APIManagementException {

        this.configuration = configuration;
    }

    /**
     * This method will Register the client in Authorization Server.
     *
     * @param oauthAppRequest this object holds all parameters required to register an OAuth Client.
     */
    @Override
    public OAuthApplicationInfo createApplication(OAuthAppRequest oauthAppRequest) throws APIManagementException {

        OAuthApplicationInfo oAuthApplicationInfo = oauthAppRequest.getOAuthApplicationInfo();
        OAuthApplicationInfo oAuthApplicationInfoResponse = null;

        log.debug("Creating a new oAuthApp in Authorization Server");

       // KeyManagerConfiguration config = KeyManagerHolder.getKeyManagerInstance().getKeyManagerConfiguration();

        // Getting Client Registration Url and Access Token from Config.
        String registrationEndpoint = configuration.getParameter(SurfClientConstants.CLIENT_REG_ENDPOINT);
        		//"http://10.138.16.90:8080/auth/realms/openbanking/clients-registrations/default";
        String registrationToken = configuration.getParameter(SurfClientConstants.REGISTRAION_ACCESS_TOKEN);
        		//"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhMDVlNDI4YS05MWI2LTQwY2EtYWRiNS1hYWYxZDEwNTdhNjUifQ.eyJqdGkiOiIxNmFiYjQ1Ni1iNDcxLTRkNzEtYjMzMy01NTlhZjY0ODQ3NmIiLCJleHAiOjE4MTQyNTA5MTQsIm5iZiI6MCwiaWF0IjoxNTU1MDUwOTE0LCJpc3MiOiJodHRwOi8vMTAuMTM4LjE2LjkwOjgwODAvYXV0aC9yZWFsbXMvb3BlbmJhbmtpbmciLCJhdWQiOiJodHRwOi8vMTAuMTM4LjE2LjkwOjgwODAvYXV0aC9yZWFsbXMvb3BlbmJhbmtpbmciLCJ0eXAiOiJJbml0aWFsQWNjZXNzVG9rZW4ifQ.d5E5P2Y1WVRjHmGXu1cJ0JC5a1qfXBgQ5gEFEVZUqlA";
        
        String applicationName = oAuthApplicationInfo.getClientName();
        String keyType = (String) oAuthApplicationInfo.getParameter(ApplicationConstants.APP_KEY_TYPE);
        if (keyType != null) {
            applicationName = applicationName + "_" + keyType;
        }

        HttpPost httpPost = new HttpPost(registrationEndpoint.trim());

        HttpClient httpClient = getHttpClient();

        BufferedReader reader = null;
        Map<String, Object> serverparams;

        try {
           // serverparams = getResourceServerParams(registrationEndpoint, registrationToken);
        	serverparams =  new HashMap<String, Object>();
            //putting client name
            serverparams.put("clientId", applicationName);
            serverparams.put("serviceAccountsEnabled", Boolean.TRUE);
            serverparams.put("directAccessGrantsEnabled", Boolean.TRUE);
            
            String jsonPayload = JSONObject.toJSONString(serverparams);
            //createJsonPayloadFromMap(serverparams);

            log.debug("Payload for creating new client : " + jsonPayload);

            httpPost.setEntity(new StringEntity(jsonPayload, SurfClientConstants.UTF_8));
            httpPost.setHeader(SurfClientConstants.CONTENT_TYPE, SurfClientConstants.APPLICATION_JSON_CONTENT_TYPE);

            // Setting Authorization Header, with Access Token
            httpPost.setHeader(SurfClientConstants.AUTHORIZATION, SurfClientConstants.BEARER + registrationToken);

            HttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            JSONObject parsedObject;
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), SurfClientConstants.UTF_8));

            // If successful a 201 will be returned.
            if (HttpStatus.SC_CREATED == responseCode) {

                parsedObject = getParsedObjectByReader(reader);
                if (parsedObject != null) {
                	oAuthApplicationInfoResponse = createOAuthAppfromResponse(parsedObject);

                    // We need the id when retrieving a single OAuth Client. So we have to maintain a mapping
                    // between the consumer key and the ID.
                    nameIdMapping.put(oAuthApplicationInfoResponse.getClientId(), (String) oAuthApplicationInfoResponse.getParameter
                            ("id"));
                    registrationAccessTokenMap.put(oAuthApplicationInfoResponse.getClientId(), (String) oAuthApplicationInfoResponse.getParameter
                            ("registrationAccessToken"));
                    

                    return oAuthApplicationInfoResponse;
                }
            } else {
                handleException("Some thing wrong here while registering the new client " +
                                "HTTP Error response code is " + responseCode);
            }

        } catch (UnsupportedEncodingException e) {
            handleException("Encoding for the Response not-supported.", e);
        } catch (ParseException e) {
            handleException("Error while parsing response json", e);
        } catch (IOException e) {
            handleException("Error while reading response body ", e);
        } finally {
            //close buffer reader.
            if (reader != null) {
                IOUtils.closeQuietly(reader);
            }
            httpClient.getConnectionManager().shutdown();
        }
        return null;
    }
   /* public OAuthApplicationInfo createApplication(OAuthAppRequest oauthAppRequest) throws APIManagementException {
    	
    	 String introspectionConsumerKey = configuration.getParameter(SurfClientConstants.INTROSPECTION_CK);
    	 String introspectionConsumerSecret = configuration.getParameter(SurfClientConstants.INTROSPECTION_CS);
 
    	OAuthApplicationInfo info = new OAuthApplicationInfo();
        info.setClientId(introspectionConsumerKey);
        info.setClientSecret(introspectionConsumerSecret);
        
        return info;
    }*/

    /**
     * This method will update an existing OAuth Client.
     *
     * @param oauthAppRequest Parameters to be passed to Authorization Server,
     *                        encapsulated as an {@code OAuthAppRequest}
     * @return Details of updated OAuth Client.
     * @throws APIManagementException
     */
    @Override
    public OAuthApplicationInfo updateApplication(OAuthAppRequest oauthAppRequest) throws APIManagementException {
    	return oauthAppRequest.getOAuthApplicationInfo();
    }

    /**
     * Deletes OAuth Client from Authorization Server.
     *
     * @param consumerKey consumer key of the OAuth Client.
     * @throws APIManagementException
     */
    @Override
    public void deleteApplication(String consumerKey) throws APIManagementException {
    	
    }

    /**
     * This method retrieves OAuth application details by given consumer key.
     *
     * @param consumerKey consumer key of the OAuth Client.
     * @return an {@code OAuthApplicationInfo} having all the details of an OAuth Client.
     * @throws APIManagementException
     */
    @Override
    public OAuthApplicationInfo retrieveApplication(String consumerKey) throws APIManagementException {

        HttpClient client = getHttpClient();
        OAuthApplicationInfo oAuthApplicationInfoResponse = null;
        // First get the Id corresponding to consumerKey
      //  String id = nameIdMapping.get(consumerKey);
        String registrationURL = configuration.getParameter(SurfClientConstants.CLIENT_REG_ENDPOINT);
        		//"http://10.138.16.90:8080/auth/realms/openbanking/clients-registrations/default";
        String accessToken = registrationAccessTokenMap.get(consumerKey);
        
        BufferedReader reader = null;
        registrationURL += "/" + consumerKey;

        try {

            HttpGet request = new HttpGet(registrationURL);
            //set authorization header.
            request.addHeader(SurfClientConstants.AUTHORIZATION, SurfClientConstants.BEARER + accessToken);
            HttpResponse response = client.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            Object parsedObject;

            HttpEntity entity = response.getEntity();

            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            if (responseCode == HttpStatus.SC_CREATED || responseCode == HttpStatus.SC_OK) {
                JSONParser parser = new JSONParser();
                if (reader != null) {
                    parsedObject = parser.parse(reader);

                    // If we have appended the ID, then the response is a JSONObject if not the response is a JSONArray.
                    if (parsedObject instanceof JSONArray) {
                        // If the response is a JSONArray, then we prime the nameId map,
                        // with the response received. And then return details of the specific client.
                        addToNameIdMap((JSONArray) parsedObject);
                        for (Object object : (JSONArray) parsedObject) {
                            JSONObject jsonObject = (JSONObject) object;
                            if ((jsonObject.get(SurfClientConstants.CLIENT_ID)).equals
                                    (consumerKey)) {
                            	oAuthApplicationInfoResponse = createOAuthAppfromResponse(jsonObject);
                            	 registrationAccessTokenMap.put(oAuthApplicationInfoResponse.getClientId(), (String) oAuthApplicationInfoResponse.getParameter
                                         ("registrationAccessToken"));
                            	 
                            	 clientIdSecretMap.put(oAuthApplicationInfoResponse.getClientId(),oAuthApplicationInfoResponse.getClientSecret());
                            	 
                                return oAuthApplicationInfoResponse;
                            }
                        }
                    } else {
                    	oAuthApplicationInfoResponse = createOAuthAppfromResponse((JSONObject) parsedObject);
                    	registrationAccessTokenMap.put(oAuthApplicationInfoResponse.getClientId(), (String) oAuthApplicationInfoResponse.getParameter
                                ("registrationAccessToken"));
                    	clientIdSecretMap.put(oAuthApplicationInfoResponse.getClientId(),oAuthApplicationInfoResponse.getClientSecret());
                        return oAuthApplicationInfoResponse;
                    }
                }

            } else {
                handleException("Something went wrong while retrieving client for consumer key " + consumerKey + "\n Token.." +accessToken);
            }

        } catch (ParseException e) {
            handleException("Error while parsing response json.", e);
        } catch (IOException e) {
            handleException("Error while reading response body.", e);
        } finally {
            client.getConnectionManager().shutdown();
            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    @Override
    public AccessTokenRequest buildAccessTokenRequestFromOAuthApp(OAuthApplicationInfo oAuthApplication,
                                                                  AccessTokenRequest tokenRequest)
            throws APIManagementException {
    	
    	AccessTokenRequest response = new AccessTokenRequest();
    	
    	response.setClientId(oAuthApplication.getClientId());
    	response.setClientSecret(oAuthApplication.getClientSecret());
    	
    	log.info("buildAccessTokenRequestFromOAuthApp called.." + oAuthApplication.getClientId() + "secret.." + oAuthApplication.getClientSecret());
    	
        return response;
    }

    @Override
    public AccessTokenInfo getNewApplicationAccessToken(AccessTokenRequest tokenRequest) throws APIManagementException {

        if (tokenRequest == null) {
            return null;
        }
        String clientId = tokenRequest.getClientId();
        String clientSecret = tokenRequest.getClientSecret();
        AccessTokenInfo accessTokenInfo = null;
        HttpPost httpTokenPost = null;

        if (clientId != null && clientSecret != null) {
            String tokenEp = configuration.getParameter(SurfClientConstants.TOKEN_ENDPOINT);
            		//"http://10.138.16.90:8080/auth/realms/openbanking/protocol/openid-connect/token";
            if (tokenEp != null) {
                HttpClient tokenEPClient = new DefaultHttpClient();

                httpTokenPost = new HttpPost(tokenEp);

                // Request parameters.
                List<NameValuePair> revokeParams = new ArrayList<NameValuePair>();
                revokeParams.add(new BasicNameValuePair(OAuth.OAUTH_GRANT_TYPE, "client_credentials"));
               
                String combinedKeySecret = clientId + ":" + clientSecret;
                httpTokenPost.setHeader(SurfClientConstants.AUTHORIZATION, SurfClientConstants.BASIC + " " + Base64.encode
                        (combinedKeySecret.getBytes()));


                HttpResponse tokenResponse = null;
                BufferedReader reader = null;
                int statusCode;
                try {
                    httpTokenPost.setEntity(new UrlEncodedFormEntity(revokeParams, "UTF-8"));
                    tokenResponse = tokenEPClient.execute(httpTokenPost);
                    statusCode = tokenResponse.getStatusLine().getStatusCode();
                    HttpEntity entity = tokenResponse.getEntity();
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

                    if (statusCode == HttpStatus.SC_OK) {
                        JSONParser parser = new JSONParser();
                        if (reader != null) {
                            Object parsedObject = parser.parse(reader);

                            if (parsedObject instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) parsedObject;
                                String accessToken = (String) jsonObject.get(OAuth.OAUTH_ACCESS_TOKEN);
                                Long validityPeriod = (Long) jsonObject.get("expires_in");
                                String[] scopes = null;
                                if (jsonObject.get("scope") != null) {
                                    scopes = ((String) jsonObject.get("scope")).split(" ");
                                }
                                if (accessToken != null) {
                                    accessTokenInfo = new AccessTokenInfo();
                                    accessTokenInfo.setAccessToken(accessToken);
                                    accessTokenInfo.setValidityPeriod(validityPeriod);
                                    accessTokenInfo.setTokenValid(true);
                                    accessTokenInfo.setScope(scopes);
                                    accessTokenInfo.setConsumerKey(clientId);
                                } else {
                                    log.warn("Access Token Null");
                                }
                            }
                        }

                    } else {
                        handleException("Something went wrong while generating the Access Token");
                    }
                } catch (IOException e) {
                    log.error("Exception occurred while generating token.", e);
                } catch (ParseException e) {
                    log.error("Error occurred while parsing the response.", e);
                }
            }

        } else {
            log.warn("Client Key or Secret not specified");
        }

        return accessTokenInfo;
    }

    @Override
    public AccessTokenInfo getTokenMetaData(String accessToken) throws APIManagementException {
        AccessTokenInfo tokenInfo = new AccessTokenInfo();

        //KeyManagerConfiguration config = KeyManagerHolder.getKeyManagerInstance().getKeyManagerConfiguration();

        String introspectionURL = configuration.getParameter(SurfClientConstants.INTROSPECTION_URL);
        		//"http://localhost:8080/auth/realms/MyFirsstRealm/protocol/openid-connect/token/introspect";
        String introspectionConsumerKey = configuration.getParameter(SurfClientConstants.INTROSPECTION_CK);
        		//"xyz-fin-tech";
        String introspectionConsumerSecret = configuration.getParameter(SurfClientConstants.INTROSPECTION_CS);
        		//"da4cc164-e682-468f-ad90-ee8a2da40ebc";
        String encodedSecret = Base64.encode(new String(introspectionConsumerKey + ":" + introspectionConsumerSecret)
                                                     .getBytes());

        BufferedReader reader = null;
        
        log.error("Info.....introspectionConsumerKey : " + introspectionConsumerKey + " introspectionConsumerSecret.." + introspectionConsumerSecret);

        try {
            HttpPost httpPost = new HttpPost(introspectionURL);
            HttpClient client = new DefaultHttpClient();

            httpPost.setHeader("Authorization", "Basic " + encodedSecret);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
         // Request parameters.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", accessToken));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            
            HttpResponse response = client.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            /*if (log.isDebugEnabled()) {
                log.debug("HTTP Response code : " + responseCode);
            }*/
            log.error("Info.....HTTP Response code : " + responseCode);

            // {"audience":"MappedClient","scopes":["test"],"principal":{"name":"mappedclient","roles":[],"groups":[],"adminPrincipal":false,
            // "attributes":{}},"expires_in":1433059160531}
            HttpEntity entity = response.getEntity();
            JSONObject parsedObject;
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            if (HttpStatus.SC_OK == responseCode) {
                //pass bufferReader object  and get read it and retrieve  the parsedJson object
                parsedObject = getParsedObjectByReader(reader);
                if (parsedObject != null) {

                    Map valueMap = parsedObject;
                    
                    String clientId = (String) valueMap.get("client_id");
                    Boolean isActive = (Boolean) valueMap.get("active");
                    
                    log.error("Info....clientId......" + clientId + "isActive....." + isActive);

                    // Returning false if mandatory attributes are missing.
                    if (clientId == null) {
                        tokenInfo.setTokenValid(false);
                        tokenInfo.setErrorcode(APIConstants.KeyValidationStatus.API_AUTH_ACCESS_TOKEN_EXPIRED);
                        return tokenInfo;
                    }

                    if (isActive) {
                    	long currentTime = System.currentTimeMillis();
                    	tokenInfo.setValidityPeriod(3000);
                        // Considering Current Time as the issued time.
                        tokenInfo.setIssuedTime(currentTime);
                        tokenInfo.setTokenValid(true);
                        
                        String[] scopes = ((String) valueMap.get("scope")).split(" ");
                        tokenInfo.setScope(scopes);
                        tokenInfo.setConsumerKey(clientId);//clientIdSecretMap
                       // tokenInfo.setConsumerSecret(introspectionConsumerSecret);
                        tokenInfo.setConsumerSecret(clientIdSecretMap.get(clientId));
                        log.error("Info....clientSecret......" + clientIdSecretMap.get(clientId));
                        tokenInfo.setAccessToken(accessToken);

                    } else {
                        tokenInfo.setTokenValid(false);
                        tokenInfo.setErrorcode(APIConstants.KeyValidationStatus.API_AUTH_ACCESS_TOKEN_INACTIVE);
                        return tokenInfo;
                    }

                } else {
                    log.error("Invalid Token " + accessToken);
                    tokenInfo.setTokenValid(false);
                    tokenInfo.setErrorcode(APIConstants.KeyValidationStatus.API_AUTH_ACCESS_TOKEN_INACTIVE);
                    return tokenInfo;
                }
            }//for other HTTP error codes we just pass generic message.
            else {
                log.error("Invalid Token " + accessToken);
                tokenInfo.setTokenValid(false);
                tokenInfo.setErrorcode(APIConstants.KeyValidationStatus.API_AUTH_ACCESS_TOKEN_INACTIVE);
                return tokenInfo;
            }

        } catch (UnsupportedEncodingException e) {
            handleException("The Character Encoding is not supported. " + e.getMessage(), e);
        } catch (ClientProtocolException e) {
            handleException("HTTP request error has occurred while sending request  to OAuth Provider. " +
                            e.getMessage(), e);
        } catch (IOException e) {
            handleException("Error has occurred while reading or closing buffer reader. " + e.getMessage(), e);
        } catch (ParseException e) {
            handleException("Error while parsing response json " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return tokenInfo;
    }
    /*public AccessTokenInfo getTokenMetaData(String accessToken) throws APIManagementException {
        AccessTokenInfo tokenInfo = new AccessTokenInfo();

        

       

                     
                        tokenInfo.setTokenValid(true);
                        
                        tokenInfo.setConsumerKey("MyApplication_15_PRODUCTION");

        return tokenInfo;
    }*/

    @Override
    public KeyManagerConfiguration getKeyManagerConfiguration() throws APIManagementException {
        return configuration;
    }

    @Override
    public OAuthApplicationInfo buildFromJSON(String jsonInput) throws APIManagementException {
        return null;
    }

    /**
     * This method will be called when mapping existing OAuth Clients with Application in API Manager
     *
     * @param appInfoRequest Details of the OAuth Client to be mapped.
     * @return {@code OAuthApplicationInfo} with the details of the mapped client.
     * @throws APIManagementException
     */
    @Override
    public OAuthApplicationInfo mapOAuthApplication(OAuthAppRequest appInfoRequest)
            throws APIManagementException {

        OAuthApplicationInfo oAuthApplicationInfo = appInfoRequest.getOAuthApplicationInfo();
        return oAuthApplicationInfo;
    }

    @Override
    public boolean registerNewResource(API api, Map resourceAttributes) throws APIManagementException {
        return true;
    }

    @Override
    public Map getResourceByApiId(String apiId) throws APIManagementException {
        return null;
    }

    @Override
    public boolean updateRegisteredResource(API api, Map resourceAttributes) throws APIManagementException {
        return true;
    }

    @Override
    public void deleteRegisteredResourceByAPIId(String apiID) throws APIManagementException {

    }

    @Override
    public void deleteMappedApplication(String s) throws APIManagementException {

    }

    @Override
    public Set<String> getActiveTokensByConsumerKey(String s) throws APIManagementException {
        return null;
    }

    @Override
    public AccessTokenInfo getAccessTokenByConsumerKey(String s) throws APIManagementException {
        return null;
    }

    /**
     * This method can be used to create a JSON Payload out of the Parameters defined in an OAuth Application.
     *
     * @param oAuthApplicationInfo Object that needs to be converted.
     * @return
     */
    private String createJsonPayloadFromOauthApplication(OAuthApplicationInfo oAuthApplicationInfo)
            throws APIManagementException {

        Map<String, Object> paramMap = new HashMap<String, Object>();

//        if (oAuthApplicationInfo.getClientName() == null ||
//            oAuthApplicationInfo.getParameter(SurfClientConstants.CLIENT_CONTACT_NAME) == null ||
//            oAuthApplicationInfo.getParameter(SurfClientConstants.CLIENT_SCOPE) == null ||
//            oAuthApplicationInfo.getParameter(SurfClientConstants.CLIENT_CONTAT_EMAIL) == null) {
//            throw new APIManagementException("Mandatory parameters missing");
//        }

        // Format of the request needed.
        // {"name":"TestClient_1","scopes":["scope1"],
        // "contactName":"John Doe",
        // "contactEmail":"john@doe.com"}

        paramMap.put(SurfClientConstants.CLIENT_NAME, oAuthApplicationInfo.getClientName());

//        JSONArray scopes = (JSONArray) oAuthApplicationInfo.getParameter(SurfClientConstants.CLIENT_SCOPE);
//        paramMap.put("scopes", scopes);
//
//        paramMap.put(SurfClientConstants.CLIENT_CONTACT_NAME, oAuthApplicationInfo.getParameter(SurfClientConstants
//                                                                                                        .CLIENT_CONTACT_NAME));
//        paramMap.put(SurfClientConstants.CLIENT_CONTAT_EMAIL, oAuthApplicationInfo.getParameter(SurfClientConstants
//                                                                                                        .CLIENT_CONTAT_EMAIL));

        JSONArray scopes = new JSONArray();
        scopes.add("test");
        paramMap.put("scopes", scopes);
        paramMap.put(SurfClientConstants.CLIENT_CONTACT_NAME, "Nuwandi");
        paramMap.put(SurfClientConstants.CLIENT_CONTAT_EMAIL, "nuwandiw@wso2.com");

        if (oAuthApplicationInfo.getParameter("id") != null) {
            paramMap.put("id", oAuthApplicationInfo.getParameter("id"));
        }

        return JSONObject.toJSONString(paramMap);
    }

    private String createJsonPayloadFromMap(Map responseMap) throws APIManagementException {

        if (responseMap.get(SurfClientConstants.CLIENT_NAME) == null ||
                responseMap.get(SurfClientConstants.CLIENT_CONTACT_NAME) == null ||
                responseMap.get(SurfClientConstants.CLIENT_SCOPE) == null ||
                responseMap.get(SurfClientConstants.CLIENT_CONTAT_EMAIL) == null) {
            throw new APIManagementException("Mandatory parameters missing");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put(SurfClientConstants.CLIENT_NAME, responseMap.get(SurfClientConstants.CLIENT_NAME));

        JSONArray scopes = (JSONArray) responseMap.get(SurfClientConstants.CLIENT_SCOPE);
        paramMap.put("scopes", scopes);

        paramMap.put(SurfClientConstants.CLIENT_CONTACT_NAME, responseMap.get(SurfClientConstants.CLIENT_CONTACT_NAME));
        paramMap.put(SurfClientConstants.CLIENT_CONTAT_EMAIL, responseMap.get(SurfClientConstants.CLIENT_CONTAT_EMAIL));
//        if (responseMap.get("id") != null) {
//            paramMap.put("id", responseMap.get("id"));
//        }

        return JSONObject.toJSONString(paramMap);
    }


    /**
     * Can be used to parse {@code BufferedReader} object that are taken from response stream, to a {@code JSONObject}.
     *
     * @param reader {@code BufferedReader} object from response.
     * @return JSON payload as a name value map.
     */
    private JSONObject getParsedObjectByReader(BufferedReader reader) throws ParseException, IOException {

        JSONObject parsedObject = null;
        JSONParser parser = new JSONParser();
        if (reader != null) {
            parsedObject = (JSONObject) parser.parse(reader);
        }
        return parsedObject;
    }

    /**
     * common method to throw exceptions.
     *
     * @param msg this parameter contain error message that we need to throw.
     * @param e   Exception object.
     * @throws APIManagementException
     */
    private void handleException(String msg, Exception e) throws APIManagementException {
        log.error(msg, e);
        throw new APIManagementException(msg, e);
    }

    /**
     * common method to throw exceptions. This will only expect one parameter.
     *
     * @param msg error message as a string.
     * @throws APIManagementException
     */
    private void handleException(String msg) throws APIManagementException {
        log.error(msg);
        throw new APIManagementException(msg);
    }

    /**
     * This method will create {@code OAuthApplicationInfo} object from a Map of Attributes.
     *
     * @param responseMap Response returned from server as a Map
     * @return OAuthApplicationInfo object will return.
     */
    private OAuthApplicationInfo createOAuthAppfromResponse(Map responseMap) {


        // Sample response returned by client registration endpoint.
        // {"id":305,"creationDate":1430486098086,"modificationDate":1430486098086,"name":"TestClient_2",
        // "clientId":"testclient_2","secret":"3b4dbfb6-0ad9-403e-8ed6-715459fc8c78",
        // "description":null,"contactName":"John Doe","contactEmail":"john@doe.com",
        // "scopes":["scope1"],"attributes":{},"thumbNailUrl":null,"redirectUris":[],
        // "skipConsent":false,"includePrincipal":false,"expireDuration":0,"useRefreshTokens":false,
        // "allowedImplicitGrant":false,"allowedClientCredentials":false}

        OAuthApplicationInfo info = new OAuthApplicationInfo();
        Object clientId = responseMap.get(SurfClientConstants.CLIENT_ID);
        info.setClientId((String) clientId);

        Object clientSecret = responseMap.get(SurfClientConstants.CLIENT_SECRET);
        info.setClientSecret((String) clientSecret);

        Object id = responseMap.get("id");
        info.addParameter("id", id);
        
        Object registrationAccessToken = responseMap.get("registrationAccessToken");
        info.addParameter("registrationAccessToken", registrationAccessToken);
        

        Object contactName = responseMap.get(SurfClientConstants.CLIENT_CONTACT_NAME);
        if (contactName != null) {
            info.addParameter("contactName", contactName);
        }

        Object contactMail = responseMap.get(SurfClientConstants.CLIENT_CONTAT_EMAIL);
        if (contactMail != null) {
            info.addParameter("contactMail", contactMail);
        }

        Object scopes = responseMap.get(SurfClientConstants.DEFAULT_SCOPES);
        if (scopes != null) {
            info.addParameter("scopes", scopes);
        }

        return info;
    }

    public Map<String, Object> getResourceServerParams(String url, String token) throws IOException, ParseException {
        String resServerUrl = url.substring(0, url.indexOf("/client")) ;
        HttpGet httpget = new HttpGet(resServerUrl);
        httpget.setHeader(SurfClientConstants.CONTENT_TYPE, SurfClientConstants.APPLICATION_JSON_CONTENT_TYPE);

        // Setting Authorization Header, with Access Token
        httpget.setHeader(SurfClientConstants.AUTHORIZATION, SurfClientConstants.BEARER + token);
        HttpClient httpClient = getHttpClient();

        HttpResponse response;
        BufferedReader reader;

        response = httpClient.execute(httpget);
        int responseCode = response.getStatusLine().getStatusCode();

        JSONObject parsedObject;
        HttpEntity entity = response.getEntity();
        reader = new BufferedReader(new InputStreamReader(entity.getContent(), SurfClientConstants.UTF_8));

        if (HttpStatus.SC_OK == responseCode) {
            parsedObject = getParsedObjectByReader(reader);
            return parsedObject;
        }
        return null;
    }

    /**
     * This method will return HttpClient object.
     *
     * @return HttpClient object.
     */
    private HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }

    private void addToNameIdMap(JSONArray clientArray) {
        for (Object jsonObject : clientArray) {
            if (jsonObject instanceof JSONObject) {
                String id = (String) ((JSONObject) jsonObject).get("id");
                String consumerId = (String) ((JSONObject) jsonObject).get(SurfClientConstants.CLIENT_ID);
                nameIdMapping.put(consumerId, id);
            }
        }
    }
   
}
