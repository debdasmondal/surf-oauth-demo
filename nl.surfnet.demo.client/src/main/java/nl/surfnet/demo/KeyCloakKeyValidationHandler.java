package nl.surfnet.demo;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.AccessTokenInfo;
import org.wso2.carbon.apimgt.impl.factory.KeyManagerHolder;
import org.wso2.carbon.apimgt.keymgt.APIKeyMgtException;
import org.wso2.carbon.apimgt.keymgt.handlers.DefaultKeyValidationHandler;
import org.wso2.carbon.apimgt.keymgt.service.TokenValidationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyCloakKeyValidationHandler extends DefaultKeyValidationHandler {
	
	private static final Log log = LogFactory.getLog(DefaultKeyValidationHandler.class);
	
	@Override
    public boolean validateToken(TokenValidationContext validationContext) throws APIKeyMgtException {
		
		AccessTokenInfo tokenInfo;
		try {
			tokenInfo = KeyManagerHolder.getKeyManagerInstance().getTokenMetaData(validationContext.getAccessToken());
		} catch (APIManagementException e) {
			log.error("Error while obtaining Token Metadata from Authorization Server", e);
            throw new APIKeyMgtException("Error while obtaining Token Metadata from Authorization Server");
		}
		
		return tokenInfo.isTokenValid();
	}
	
	@Override
    public boolean validateScopes(TokenValidationContext validationContext) throws APIKeyMgtException {
		return true;
	}

}
