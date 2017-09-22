package com.cmcc.normandy.client.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmcc.normandy.client.common.service.impl.ClientCache;
import com.cmcc.normandy.client.constants.ErrorCode;
import com.cmcc.normandy.client.exception.ClientServiceException;

public class CheckUseTime {

	
	
    private static final Logger logger = LoggerFactory.getLogger(ClientConstants.CLIENT_LOG);
	  @Autowired
	  private ClientCache clientCache;
	
	 /**
     * 没有包名或者包签名限制每天使用10次
     * @param idSign :appid
     * @throws Exception
     */
    
    private void opidReqHandle(String idSign) throws Exception {
        if (StringUtils.isBlank(idSign) || !ClientConstants.OP_ID_MAP.containsKey(idSign)) {
            idSign = "default";
        }
        try {
        	
            Integer reqnum = 1;
            String cacheData = (String) clientCache
            		.find(ClientConstants.CKECK_USE_LIMIT_KEY + idSign);
            long time = System.currentTimeMillis();
            if(StringUtils.isNotEmpty(cacheData)){
            	String[] arrayData = cacheData.split(",");
            	reqnum=Integer.valueOf(arrayData[0]);
            	Long cacheTime = Long.valueOf(arrayData[1]);
            	int ctime=(int) ((time-cacheTime)/1000);
            	if (reqnum < 10) {
                    reqnum++;
                    cacheData=reqnum+","+cacheTime;
                    clientCache.save(ClientConstants.CKECK_USE_LIMIT_KEY + idSign, cacheData, ctime);
                }else if(reqnum >= 10){
                	throw new ClientServiceException(ErrorCode.REQUEST_TOO_FREQUENCY_ERROR);
                }
            }else{
        		cacheData=reqnum+","+time;
        		clientCache.save(ClientConstants.CKECK_USE_LIMIT_KEY + idSign, cacheData, 86400);
            
            }
            logger.debug("reqnum={}", reqnum);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
