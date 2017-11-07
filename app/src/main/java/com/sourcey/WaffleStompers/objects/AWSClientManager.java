package com.sourcey.WaffleStompers.objects;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by Adam on 2017-09-21.
 */
public class AWSClientManager {

    private static CognitoCachingCredentialsProvider provider = null;
    private static AmazonDynamoDBClient ddb = null;
    private static ClientConfiguration configuration = null;
    private static CognitoUserPool userPool = null;

    public static void init(Context context){
        provider = new CognitoCachingCredentialsProvider(context,
                Constants.AWS_ACCOUNT_ID, Constants.COGNITO_POOL_ID, Constants.COGNITO_ROLE_UNAUTH,
                Constants.COGNITO_ROLE_AUTH, Regions.US_EAST_1);

        ddb = new AmazonDynamoDBClient(provider);

        configuration = new ClientConfiguration();

        userPool = new CognitoUserPool(context, Constants.USER_POOL_ID, Constants.CLIENT_ID, Constants.CLIENT_SECRET, configuration );
        
    }

    public static CognitoUserPool getUserPool()
    {
        return userPool;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

}
