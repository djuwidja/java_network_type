/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djuwidja.networktype.transformer;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.djuwidja.networktype.NTDict;
import com.djuwidja.networktype.NTNull;
import com.djuwidja.networktype.NTObject;

/**
 * Transform an object into MQDict.
 * @author kennethdjuwidja
 * @since 1.0.0
 * @version 1.0.0
 */
@Service
public class MQTypeTransformerService {  
    private SupportedClassMapper classMapper;
    
    public MQTypeTransformerService(){
        classMapper = SupportedClassMapper.getInstance();
    }
    /**
     * transform a field in the the object. 
     * Will attempt to transform the field as an object by calling {@link MQTypeTransformerService#transformObj(Object)}
     * if no supported transformer can be found.
     * @param field the field to be transformed.
     * @param data the transforming data object.
     * @return the transformed MQType.
     */
    private NTObject transformField(Field field, Object data) {   	
        try {
            Object obj = field.get(data);
            if (obj != null){
            	Class<?> fieldType = field.getType();
            	try {
            		MQTypeTransformer transformer = classMapper.getTransformer(fieldType);
            		return transformer.transform(obj);
            	} catch (final SupportedClassMapperException e) {
            		//try to treat the object as a base object when no supported transformer can be found.
                	return transformObj(obj);
                }
            }
        } catch (final Exception e){

        }
        
        return new NTNull();
    }    
    /**
     * Transform the current object into MQDict.
     * @param cls class of the object to be transformed.
     * @param data the object to be transformed.
     * @return the transformed MQDict.
     * @throws IllegalAccessException fails when there is an illegal access exception when accessing data.
     */
    private NTDict transformCurrObj(Class<?> cls, Object data) throws IllegalAccessException {
        NTDict returnData = new NTDict();        
        
        Field[] fieldList = cls.getDeclaredFields();
        for (Field field : fieldList){
            String fieldName = field.getName();
            //ignore jacoco data and any other weirdly named variables
            if (fieldName.charAt(0) != '$' && !fieldName.equals("serialVersionUID")){ 
                NTObject returnType = transformField(field, data);
                if (returnType != null)
                    returnData.put(fieldName, returnType);
            }
        }
        
        return returnData;
    }
    /**
     * Transform an object into MQDict. Unsupported types are ignored. 
     * Please see {@link SupportedClassMapper} for a list of supported type.
     * @param data object to be transformed.
     * @return transformed MQDict.
     * @throws MQTypeTransformerException fails when there is an illegal access exception when accessing data.
     */
    public NTDict transformObj(Object data) throws MQTypeTransformerException {
    	try {
    		Stack<Class<?>> hierarchyStack = new Stack<>();
            
            Class<?> curCls = data.getClass();
            do {
                hierarchyStack.add(curCls);
                curCls = curCls.getSuperclass();
            } while (curCls != null);
            
            NTDict returnData = new NTDict();
            
            while (hierarchyStack.size() > 0){
                Class<?> handleCls = hierarchyStack.pop();
                NTDict clsFields = transformCurrObj(handleCls, data);
                Set<String> keySet = clsFields.get().keySet();
                for (String key : keySet){
                    returnData.put(key, clsFields.get(key));
                }
            }
            
            return returnData;
    	}
    	catch (final IllegalAccessException e) {
    		throw new MQTypeTransformerException(e);
    	}        
    }
    /**
     * Transform a JSONObject into MQDict. Unsupported types are ignored.
     * Please see {@link SupportedClassMapper} for a list of supported type.
     * @param data JSONObject to be transformed.
     * @return transformed MQDict.
     * @throws MQTypeTransformerException fails when unable to transform JSON.
     */
    public NTDict transformJson(JSONObject data) throws MQTypeTransformerException {
    	try {
    		MQTypeTransformer transformer = classMapper.getTransformer(data.getClass());
        	NTDict result = (NTDict) transformer.transform(data);
        	return result;
    	}
    	catch (final SupportedClassMapperException e) {
    		throw new MQTypeTransformerException(e);
    	}
    	catch (final IllegalAccessException e) {
    		throw new MQTypeTransformerException(e);
    	}
    	
    }
}
